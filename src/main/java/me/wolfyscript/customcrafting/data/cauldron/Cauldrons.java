package me.wolfyscript.customcrafting.data.cauldron;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronCookEvent;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Cauldrons {

    private WolfyUtilities api;
    private int autosaveTask;
    private BukkitTask recipeTick;
    private int particles;
    private Random random = new Random();

    //Hashmap of all the locations of the valid cauldrons. The Key is the Location. The Value is the current active recipe, which is going to be saved on server shutdown.
    private HashMap<Location, List<Cauldron>> cauldrons = new HashMap<>();

    public Cauldrons(WolfyUtilities api) {
        this.api = api;
        load();
        autosaveTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(api.getPlugin(), () -> {
            if (CustomCrafting.getConfigHandler().getConfig().isAutoSaveMesage()) {
                api.sendConsoleMessage("[$msg.auto_save.start$]");
                save();
                api.sendConsoleMessage("[$msg.auto_save.complete$]");
            } else {
                save();
            }
        }, CustomCrafting.getConfigHandler().getConfig().getAutosaveInterval() * 1200, CustomCrafting.getConfigHandler().getConfig().getAutosaveInterval() * 1200);
        particles = Bukkit.getScheduler().scheduleSyncRepeatingTask(api.getPlugin(), () -> {
            for (Location loc : cauldrons.keySet()) {
                if (loc != null) {
                    World world = loc.getWorld();
                    if (world != null) {
                        if (loc.getBlock().getType().equals(Material.CAULDRON)) {
                            Levelled data = (Levelled) loc.getBlock().getBlockData();
                            int level = data.getLevel();
                            if (isCustomCauldronLit(loc.getBlock())) {
                                if (level > 0) {
                                    world.spawnParticle(Particle.BUBBLE_POP, loc.clone().add(0.5, 0.35 + level * 0.2, 0.5), 1, 0.15, 0.1, 0.15, 0.0000000001);
                                    //world.spawnParticle(Particle.BUBBLE_POP, loc.clone().add(0.5, 0.35 + level * 0.2, 0.5), 5, 0.15, 0.1, 0.15, 0.00000001);
                                }
                            }
                        }
                    }
                }
            }
        }, 10, 4);
        recipeTick = Bukkit.getScheduler().runTaskTimerAsynchronously(api.getPlugin(), () -> {
            for (Map.Entry<Location, List<Cauldron>> cauldronEntry : cauldrons.entrySet()) {
                Location loc = cauldronEntry.getKey().clone();
                List<Cauldron> cauldronEntryValue = cauldronEntry.getValue();
                if (loc != null && loc.getWorld() != null) {
                    World world = loc.getWorld();
                    if (!cauldronEntryValue.isEmpty()) {
                        Levelled levelled = (Levelled) loc.getBlock().getBlockData();
                        int level = levelled.getLevel();
                        Iterator<Cauldron> cauldronItr = cauldronEntry.getValue().iterator();
                        while (cauldronItr.hasNext()) {
                            Cauldron cauldron = cauldronItr.next();
                            CauldronRecipe recipe = cauldron.getRecipe();
                            if (level >= recipe.getWaterLevel() && (level == 0 || recipe.needsWater()) && (!recipe.needsFire() || isCustomCauldronLit(loc.getBlock()))) {
                                if (cauldron.getPassedTicks() >= cauldron.getCookingTime() && !cauldron.isDone()) {
                                    //Execute CauldronRecipeDoneEvent
                                    cauldron.setDone(true);
                                    CauldronCookEvent event = new CauldronCookEvent(cauldron);
                                    Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                                        Bukkit.getPluginManager().callEvent(event);
                                        if (event.isCancelled()) {
                                            cauldron.setDone(false);
                                            cauldron.setPassedTicks(0);
                                        } else {
                                            if (event.getRecipe().getWaterLevel() > 0) {
                                                int newLevel = levelled.getLevel() - event.getRecipe().getWaterLevel();
                                                if (newLevel >= 0) {
                                                    levelled.setLevel(newLevel);
                                                } else {
                                                    levelled.setLevel(0);
                                                }
                                                loc.getBlock().setBlockData(levelled);
                                            }
                                            if (event.dropItems()) {
                                                Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> world.dropItemNaturally(loc.add(0.0, 0.5, 0.0), event.getResult()));
                                                cauldronItr.remove();
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
                                        }
                                    });
                                } else {
                                    world.spawnParticle(Particle.BUBBLE_POP, loc.clone().add(0.5, 0.35 + level * 0.2, 0.5), 1, 0.15, 0.1, 0.15, 0.0000000001);
                                    world.spawnParticle(Particle.REDSTONE, loc.clone().add(0.5, 0.35 + level * 0.2, 0.5), 1, 0.17, 0.2, 0.17, 4.0, new Particle.DustOptions(Color.fromBGR(random.nextInt(255), random.nextInt(255), random.nextInt(255)), random.nextInt(2)));
                                    cauldron.increasePassedTicks();
                                }
                            } else {
                                cauldron.decreasePassedTicks(2);
                                if (cauldron.getPassedTicks() <= 0) {
                                    for (CustomItem customItem : cauldron.getRecipe().getIngredients()) {
                                        Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> world.dropItemNaturally(loc.add(0.0, 0.5, 0.0), customItem.getItemStack()));
                                    }
                                    cauldronItr.remove();
                                }
                            }
                        }
                    }
                }
            }
        }, 20, 1);
    }

    public boolean isCustomCauldronLit(Block block) {
        if (WolfyUtilities.hasVillagePillageUpdate()) {
            if (block.getRelative(BlockFace.DOWN).getType().equals(Material.CAMPFIRE)) {
                return ((Campfire) block.getRelative(BlockFace.DOWN).getBlockData()).isLit();
            }
        } else return block.getRelative(BlockFace.DOWN).getType().equals(Material.FIRE);
        return false;
    }

    public void addCauldron(Location location) {
        if (!cauldrons.containsKey(location)) {
            cauldrons.put(location, new ArrayList<>());
        }
    }

    public void removeCauldron(Location location) {
        cauldrons.remove(location);
    }

    public void removeCauldron(Location location, Cauldron cauldron) {
        List<Cauldron> values = cauldrons.get(location);
        values.remove(cauldron);
        cauldrons.put(location, values);
    }

    public HashMap<Location, List<Cauldron>> getCauldrons() {
        return cauldrons;
    }

    public boolean isCauldron(Location location) {
        if (cauldrons.containsKey(location)) {
            return cauldrons.containsKey(location);
        } else {
            for (Location location1 : cauldrons.keySet()) {
                if (location1.equals(location)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String locationToString(Location location) {
        return location.getWorld().getUID() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    private Location stringToLocation(String loc) {
        String[] args = loc.split(";");
        return new Location(Bukkit.getWorld(UUID.fromString(args[0])), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
    }

    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(new File(CustomCrafting.getInst().getDataFolder() + File.separator + "cauldrons.dat"));
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(fos);
            HashMap<String, List<String>> saveMap = new HashMap<>();
            for (Map.Entry<Location, List<Cauldron>> entry : cauldrons.entrySet()) {
                List<String> values = new ArrayList<>();
                for (Cauldron cauldron : entry.getValue()) {
                    values.add(cauldron.toString());
                }
                saveMap.put(locationToString(entry.getKey()), values);
            }
            oos.writeObject(saveMap);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File file = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "cauldrons.dat");
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
                        List<Cauldron> value = new ArrayList<>();
                        if (entry.getValue() != null) {
                            for (String data : entry.getValue()) {
                                value.add(Cauldron.fromString(data));
                            }
                        }
                        cauldrons.put(stringToLocation(entry.getKey()), value);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void endAutoSaveTask() {
        Bukkit.getScheduler().cancelTask(autosaveTask);
    }
}
