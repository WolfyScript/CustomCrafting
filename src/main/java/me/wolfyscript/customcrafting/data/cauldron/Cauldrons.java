package me.wolfyscript.customcrafting.data.cauldron;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronCookEvent;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronPreCookEvent;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.*;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
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

    //Hashmap of all the locations of the valid cauldrons. The Key is the Location. The Value is the current active recipe, which can be saved on server shutdown.
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
                        Levelled data = (Levelled) loc.getBlock().getBlockData();
                        int level = data.getLevel();
                        if (level > 0) {
                            world.spawnParticle(Particle.BUBBLE_POP, loc.clone().add(0.5, 0.35 + level * 0.2, 0.5), 5, 0.15, 0, 0.15, 0.00000001);
                            world.spawnParticle(Particle.REDSTONE, loc.clone().add(0.5, 0.35 + level * 0.2, 0.5), 1, 0.17, 0, 0.17, 4.0, new Particle.DustOptions(Color.fromBGR(random.nextInt(255), random.nextInt(255), random.nextInt(255)), random.nextInt(2)));
                        }
                    }
                }
            }
        }, 10, 4);
        final int[] checkForNewRecipes = {0};
        recipeTick = Bukkit.getScheduler().runTaskTimerAsynchronously(api.getPlugin(), () -> {
            for (Map.Entry<Location, List<Cauldron>> cauldronEntry : cauldrons.entrySet()) {
                Location loc = cauldronEntry.getKey();
                List<Cauldron> cauldronEntryValue = cauldronEntry.getValue();
                if (loc != null && loc.getWorld() != null) {
                    if (checkForNewRecipes[0] <= 0) {
                        Bukkit.getScheduler().runTaskLater(api.getPlugin(), () -> {
                            List<Item> items = new ArrayList<>();
                            for (Entity entity : loc.getWorld().getNearbyEntities(loc.clone().add(0.5, 0.4, 0.5), 0.5, 0.4, 0.5, entity -> entity instanceof Item)) {
                                items.add((Item) entity);
                            }
                            if (!items.isEmpty()) {
                                //Check for new possible Recipes
                                List<CauldronRecipe> recipes = CustomCrafting.getRecipeHandler().getCauldronRecipes();
                                for (CauldronRecipe recipe : recipes) {
                                    List<Item> validItems = recipe.checkRecipe(items);
                                    if (validItems != null) {
                                        //Do something with the items! e.g. consume!

                                        CauldronPreCookEvent event = new CauldronPreCookEvent(recipe);
                                        Bukkit.getPluginManager().callEvent(event);
                                        if (!event.isCancelled()) {
                                            cauldronEntryValue.add(new Cauldron(event));
                                            for (int i = 0; i < recipe.getIngredients().size() && i < validItems.size(); i++) {
                                                Item itemEntity = validItems.get(i);
                                                ItemStack itemStack = itemEntity.getItemStack();
                                                CustomItem customItem = recipe.getIngredients().get(i);
                                                customItem.consumeItem(itemStack, customItem.getAmount(), itemEntity.getLocation().clone().add(0.0, 0.5, 0.0));
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }, 1);
                        checkForNewRecipes[0] = 6;
                    } else {
                        checkForNewRecipes[0]--;
                    }
                    if (!cauldronEntryValue.isEmpty()) {
                        for (Cauldron cauldron : cauldronEntryValue) {
                            if (cauldron.getPassedTicks() >= cauldron.getCookingTime() && !cauldron.isDone()) {
                                //Execute CauldronRecipeDoneEvent
                                cauldron.setDone(true);
                                CauldronCookEvent event = new CauldronCookEvent(loc, cauldron);
                                Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                                    Bukkit.getPluginManager().callEvent(event);
                                    if(event.isCancelled()){
                                        cauldron.setDone(false);
                                        cauldron.setPassedTicks(0);
                                    }
                                });
                            } else {
                                cauldron.increasePassedTicks();
                            }
                        }
                    }
                    cauldrons.put(loc, cauldronEntryValue);
                }
            }
        }, 20, 1);
    }


    public void addCauldron(Location location) {
        if (!cauldrons.containsKey(location)) {
            cauldrons.put(location, new ArrayList<>());
        }
    }

    public void removeCauldron(Location location) {
        cauldrons.remove(location);
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
