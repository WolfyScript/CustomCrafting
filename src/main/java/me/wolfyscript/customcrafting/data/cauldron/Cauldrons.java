package me.wolfyscript.customcrafting.data.cauldron;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronCookEvent;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Cauldrons {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities api;
    private final int autosaveTask;
    private boolean isBeingSaved = false;
    private final Random random = new Random();

    //Hashmap of all the locations of the valid cauldrons. The Key is the Location. The Value is the current active recipe, which is going to be saved on server shutdown.
    private HashMap<Location, List<Cauldron>> cauldrons = new HashMap<>();

    private final HashMap<Location, List<Cauldron>> queuedAddCauldrons = new HashMap<>();
    private final List<Location> queuedRemoveCauldrons = new ArrayList<>();

    public Cauldrons(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.api = WolfyUtilities.get(customCrafting);
        load();
        autosaveTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(api.getPlugin(), this::save, customCrafting.getConfigHandler().getConfig().getAutosaveInterval() * 1200L, customCrafting.getConfigHandler().getConfig().getAutosaveInterval() * 1200L);

        AtomicInteger particleTicker = new AtomicInteger(0);

        Bukkit.getScheduler().runTaskTimer(api.getPlugin(), () -> {
            int particleTick = particleTicker.incrementAndGet();
            final boolean spawnParticles = particleTick >= 4;
            if (spawnParticles) {
                particleTicker.set(0);
                queuedRemoveCauldrons.forEach(l -> cauldrons.remove(l));
                queuedRemoveCauldrons.clear();
                cauldrons.putAll(queuedAddCauldrons);
                queuedAddCauldrons.clear();
            }
            cauldrons.entrySet().stream().filter(entry -> entry.getKey() != null && entry.getKey().getWorld() != null && entry.getKey().getWorld().isChunkLoaded(entry.getKey().getBlockX() >> 4, entry.getKey().getBlockZ() >> 4) && !isBeingSaved && entry.getKey().getBlock().getType().equals(Material.CAULDRON)).forEach(entry -> {
                final Location loc = entry.getKey();
                final World world = loc.getWorld();
                final Block block = loc.getBlock();
                final Levelled levelled = (Levelled) block.getBlockData();
                final boolean isLit = isCustomCauldronLit(block);
                int level = levelled.getLevel();
                if (spawnParticles && isLit && level > 0) {
                    world.spawnParticle(Particle.BUBBLE_POP, loc.clone().add(0.5, 0.35 + level * 0.2, 0.5), 1, 0.15, 0.1, 0.15, 0.0000000001);
                }
                if (entry.getValue().isEmpty()) return;
                Iterator<Cauldron> cauldronItr = entry.getValue().iterator();
                while (cauldronItr.hasNext()) {
                    Cauldron cauldron = cauldronItr.next();
                    CauldronRecipe recipe = cauldron.getRecipe();
                    if (level >= recipe.getWaterLevel() && (level == 0 || recipe.needsWater()) && (!recipe.needsFire() || isLit)) {
                        Bukkit.getScheduler().runTaskAsynchronously(customCrafting, () -> {
                            if (cauldron.getPassedTicks() >= cauldron.getCookingTime() && !cauldron.isDone()) {
                                //Execute CauldronRecipeDoneEvent
                                cauldron.setDone(true);
                                CauldronCookEvent event = new CauldronCookEvent(cauldron);
                                Future<Boolean> checkCauldron = Bukkit.getScheduler().callSyncMethod(customCrafting, () -> {
                                    Bukkit.getPluginManager().callEvent(event);
                                    if (event.isCancelled()) {
                                        cauldron.setDone(false);
                                        cauldron.setPassedTicks(0);
                                    } else {
                                        if (event.getRecipe().getWaterLevel() > 0) {
                                            int newLevel = levelled.getLevel() - event.getRecipe().getWaterLevel();
                                            levelled.setLevel(Math.max(newLevel, 0));
                                            loc.getBlock().setBlockData(levelled);
                                        }
                                        if (WolfyUtilities.hasMythicMobs()) {
                                            if (!cauldron.getRecipe().getMythicMobName().equals("<none>")) {
                                                MythicMob mythicMob = MythicMobs.inst().getMobManager().getMythicMob(cauldron.getRecipe().getMythicMobName());
                                                if (mythicMob != null) {
                                                    Location location = loc.clone().add(cauldron.getRecipe().getMythicMobMod());
                                                    mythicMob.spawn(BukkitAdapter.adapt(location), cauldron.getRecipe().getMythicMobLevel());
                                                }
                                            }
                                        }
                                        if (event.dropItems()) {
                                            world.dropItemNaturally(loc.clone().add(0.0, 0.5, 0.0), event.getResult().create());
                                            return true;
                                        }
                                    }
                                    return false;
                                });
                                while (!checkCauldron.isDone()) {
                                    //Wait for task to be done!
                                }
                                try {
                                    if (checkCauldron.get()) {
                                        cauldronItr.remove();
                                    }
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Bukkit.getScheduler().runTask(customCrafting, () -> {
                                    world.spawnParticle(Particle.BUBBLE_POP, loc.clone().add(0.5, 0.35 + level * 0.2, 0.5), 1, 0.15, 0.1, 0.15, 0.0000000001);
                                    world.spawnParticle(Particle.REDSTONE, loc.clone().add(0.5, 0.35 + level * 0.2, 0.5), 1, 0.17, 0.2, 0.17, 4.0, new Particle.DustOptions(Color.fromBGR(random.nextInt(255), random.nextInt(255), random.nextInt(255)), random.nextInt(2)));
                                });
                                cauldron.increasePassedTicks();
                            }
                        });
                    } else {
                        cauldron.decreasePassedTicks(2);
                        if (cauldron.getPassedTicks() <= 0) {
                            for (CustomItem customItem : cauldron.getRecipe().getIngredients()) {
                                Bukkit.getScheduler().runTask(customCrafting, () -> world.dropItemNaturally(loc.add(0.0, 0.5, 0.0), customItem.getItemStack()));
                            }
                            cauldronItr.remove();
                        }
                    }
                }
            });
        }, 20, 1);
    }

    public boolean isCustomCauldronLit(Block block) {
        if (block.getRelative(BlockFace.DOWN).getType().equals(Material.CAMPFIRE)) {
            return ((Campfire) block.getRelative(BlockFace.DOWN).getBlockData()).isLit();
        }
        return false;
    }

    public void addCauldron(Location location) {
        if (!cauldrons.containsKey(location)) {
            queuedAddCauldrons.put(location, new ArrayList<>());
        }
    }

    public void removeCauldron(Location location) {
        if (!queuedRemoveCauldrons.contains(location)) {
            queuedRemoveCauldrons.add(location);
        }
    }

    public HashMap<Location, List<Cauldron>> getCauldrons() {
        return cauldrons;
    }

    public boolean isCauldron(Location location) {
        return cauldrons.containsKey(location) || cauldrons.keySet().stream().anyMatch(location1 -> location1.equals(location));
    }

    private String locationToString(Location location) {
        if (location == null || location.getWorld() == null) return null;
        return location.getWorld().getUID() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    private Location stringToLocation(String loc) {
        String[] args = loc.split(";");
        try {
            UUID uuid = UUID.fromString(args[0]);
            World world = Bukkit.getWorld(uuid);
            if (world != null) {
                return new Location(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            }
        } catch (IllegalArgumentException e) {
            api.getChat().sendConsoleWarning("Couldn't find world " + args[0]);
        }
        return null;
    }

    public void save() {
        try {
            if (customCrafting.getConfigHandler().getConfig().isAutoSaveMessage()) {
                api.getChat().sendConsoleMessage("Saving Cauldrons");
            }
            this.isBeingSaved = true;
            FileOutputStream fos = new FileOutputStream(customCrafting.getDataFolder() + File.separator + "cauldrons.dat");
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(fos);
            HashMap<String, List<String>> saveMap = new HashMap<>();
            cauldrons.entrySet().stream().filter(entry -> entry.getKey() != null).forEach(entry -> {
                String loc = locationToString(entry.getKey());
                if (loc != null) {
                    saveMap.put(loc, entry.getValue() == null ? new ArrayList<>() : entry.getValue().stream().filter(Objects::nonNull).map(Cauldron::toString).collect(Collectors.toList()));
                }
            });
            oos.writeObject(saveMap);
            oos.close();
            this.isBeingSaved = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        api.getChat().sendConsoleMessage("Loading Cauldrons");
        File file = new File(customCrafting.getDataFolder() + File.separator + "cauldrons.dat");
        if (file.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                BukkitObjectInputStream ois = new BukkitObjectInputStream(fis);
                try {
                    Object object = ois.readObject();
                    this.cauldrons = new HashMap<>();
                    HashMap<String, List<String>> loadMap = (HashMap<String, List<String>>) object;
                    for (Map.Entry<String, List<String>> entry : loadMap.entrySet()) {
                        Location location = stringToLocation(entry.getKey());
                        if (location != null) {
                            this.cauldrons.put(location, entry.getValue() == null ? new ArrayList<>() : entry.getValue().stream().map(s -> Cauldron.fromString(customCrafting, s)).collect(Collectors.toList()));
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                ois.close();
            } catch (IOException e) {
                api.getChat().sendConsoleWarning("Couldn't load cauldrons. No data found");
            }
        }
    }

    public void endAutoSaveTask() {
        Bukkit.getScheduler().cancelTask(autosaveTask);
    }
}
