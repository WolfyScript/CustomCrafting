package me.wolfyscript.customcrafting.data.cauldron;

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
    private final Random random = new Random();

    //Hashmap of all the locations of the valid cauldrons. The Key is the Location. The Value is the current active recipe, which is going to be saved on server shutdown.
    private final Map<Location, List<Cauldron>> cauldrons = new Hashtable<>();

    public Cauldrons(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.api = WolfyUtilities.get(customCrafting);
        load();
        autosaveTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(api.getPlugin(), this::save, customCrafting.getConfigHandler().getConfig().getAutosaveInterval() * 1200L, customCrafting.getConfigHandler().getConfig().getAutosaveInterval() * 1200L);

        AtomicInteger particleTicker = new AtomicInteger(0);

        Bukkit.getScheduler().runTaskTimer(api.getPlugin(), () -> {
            final boolean spawnParticles = particleTicker.incrementAndGet() >= 4;
            if (spawnParticles) {
                particleTicker.set(0);
            }
            synchronized (cauldrons) {
                cauldrons.entrySet().stream().filter(entry -> entry.getKey() != null && entry.getKey().getWorld() != null && entry.getKey().getWorld().isChunkLoaded(entry.getKey().getBlockX() >> 4, entry.getKey().getBlockZ() >> 4) && entry.getKey().getBlock().getType().equals(Material.CAULDRON)).forEach(entry -> {
                    final Location loc = entry.getKey();
                    final World world = loc.getWorld();
                    final Block block = loc.getBlock();
                    final Levelled levelled = (Levelled) block.getBlockData();
                    final boolean isLit = isCustomCauldronLit(block);
                    int level = levelled.getLevel();
                    if (spawnParticles && isLit && level > 0) {
                        world.spawnParticle(Particle.BUBBLE_POP, loc.clone().add(0.5, 0.35 + level * 0.2, 0.5), 1, 0.15, 0.1, 0.15, 0.0000000001);
                    }
                    entry.getValue().removeIf(Cauldron::isForRemoval);
                    if (entry.getValue().isEmpty()) return;
                    Iterator<Cauldron> cauldronItr = entry.getValue().iterator();
                    while (cauldronItr.hasNext()) {
                        Cauldron cauldron = cauldronItr.next();
                        CauldronRecipe recipe = cauldron.getRecipe();
                        if (level >= recipe.getWaterLevel() && (level == 0 || recipe.needsWater()) && (!recipe.needsFire() || isLit)) {
                            Bukkit.getScheduler().runTaskAsynchronously(customCrafting, () -> {
                                if (cauldron.getPassedTicks() >= cauldron.getCookingTime() && !cauldron.isDone()) {
                                    cauldron.setDone(true);
                                    Future<Boolean> checkCauldron = Bukkit.getScheduler().callSyncMethod(customCrafting, () -> {
                                        CauldronCookEvent event = new CauldronCookEvent(cauldron);
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
                                            recipe.getResult().executeExtensions(loc.clone(), true, null);
                                            if (event.dropItems()) {
                                                world.dropItemNaturally(loc.clone().add(0.0, 0.5, 0.0), event.getResult().create());
                                                return true;
                                            }
                                        }
                                        return false;
                                    });
                                    try {
                                        cauldron.setForRemoval(checkCauldron.get());
                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Bukkit.getScheduler().runTask(customCrafting, () -> {
                                        world.spawnParticle(Particle.BUBBLE_POP, loc.clone().add(0.5, 0.35 + level * 0.2, 0.5), 1, 0.15, 0.1, 0.15, 0.0000000001);
                                        world.spawnParticle(Particle.REDSTONE, loc.clone().add(0.5, 0.35 + level * 0.2, 0.5), 1, 0.17, 0.2, 0.17, 4.0, new Particle.DustOptions(Color.fromBGR(random.nextInt(255), random.nextInt(255), random.nextInt(255)), random.nextInt(2)));
                                    });
                                    if (!cauldron.isDone()) {
                                        cauldron.increasePassedTicks();
                                    }
                                }
                            });
                        } else {
                            cauldron.decreasePassedTicks(2);
                            if (cauldron.getPassedTicks() <= 0) {
                                for (CustomItem customItem : recipe.getIngredients().getChoices()) {
                                    Bukkit.getScheduler().runTask(customCrafting, () -> world.dropItemNaturally(loc.add(0.0, 0.5, 0.0), customItem.getItemStack()));
                                }
                                cauldronItr.remove();
                            }
                        }
                    }
                });
            }
        }, 20, 1);
    }

    public boolean isCustomCauldronLit(Block block) {
        if (block.getRelative(BlockFace.DOWN).getType().equals(Material.CAMPFIRE)) {
            return ((Campfire) block.getRelative(BlockFace.DOWN).getBlockData()).isLit();
        }
        return false;
    }

    public void addCauldron(Location location) {
        synchronized (cauldrons) {
            if (!cauldrons.containsKey(location)) {
                cauldrons.put(location, new ArrayList<>());
            }
        }
    }

    public void removeCauldron(Location location) {
        synchronized (cauldrons) {
            cauldrons.remove(location);
        }
    }

    public synchronized Map<Location, List<Cauldron>> getCauldrons() {
        return cauldrons;
    }

    public synchronized boolean isCauldron(Location location) {
        return cauldrons.containsKey(location);
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
            FileOutputStream fos = new FileOutputStream(customCrafting.getDataFolder() + File.separator + "cauldrons.dat");
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(fos);
            Hashtable<String, List<String>> saveMap = new Hashtable<>();
            synchronized (cauldrons) {
                cauldrons.entrySet().stream().filter(entry -> entry.getKey() != null).forEach(entry -> {
                    String loc = locationToString(entry.getKey());
                    if (loc != null) {
                        saveMap.put(loc, entry.getValue() == null ? new ArrayList<>() : entry.getValue().stream().filter(Objects::nonNull).map(Cauldron::toString).collect(Collectors.toList()));
                    }
                });
            }
            oos.writeObject(saveMap);
            oos.close();
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
                    this.cauldrons.clear();
                    Map<String, List<String>> loadMap = (Map<String, List<String>>) object;
                    for (Map.Entry<String, List<String>> entry : loadMap.entrySet()) {
                        Location location = stringToLocation(entry.getKey());
                        if (location != null) {
                            this.cauldrons.put(location, entry.getValue() == null ? new ArrayList<>() : entry.getValue().stream().map(Cauldron::fromString).filter(Objects::nonNull).collect(Collectors.toList()));
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
