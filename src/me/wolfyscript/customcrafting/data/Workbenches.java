package me.wolfyscript.customcrafting.data;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Shulker;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Consumer;
import org.bukkit.util.EulerAngle;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

public class Workbenches {

    private WolfyUtilities api;
    private int task;
    private int particles;

    private HashMap<String, List<Map<String, Object>>> workbenches = new HashMap<>();
    private List<String> furnaces = new ArrayList<>();

    public Workbenches(WolfyUtilities api) {
        this.api = api;
        load();
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(api.getPlugin(), () -> {
            api.sendConsoleMessage("[Auto save Workbenches]");
            save();
            api.sendConsoleMessage("[ Auto save complete! ]");
        }, CustomCrafting.getConfigHandler().getConfig().getAutosaveInterval() * 1200, CustomCrafting.getConfigHandler().getConfig().getAutosaveInterval() * 1200);

        particles = Bukkit.getScheduler().scheduleSyncRepeatingTask(api.getPlugin(), () -> {
            for (String loc : workbenches.keySet()) {
                Location location = stringToLocation(loc);
                World world = location.getWorld();
                world.spawnParticle(Particle.ENCHANTMENT_TABLE, location.clone().add(0.5, 1.3, 0.5), 4, 0, 0, 0, 0.5);
                Collection<Entity> entities = world.getNearbyEntities(BoundingBox.of(location, 1d, 1d, 1d), entity -> (entity instanceof ArmorStand) && !entity.getCustomName().isEmpty() && entity.getCustomName().startsWith("cc:"));
                if(entities.isEmpty()){
                    List<ItemStack> contents = getContents(location);
                    if(!contents.isEmpty()){
                        for(int i = 0; i < 9; i++){
                            ItemStack item = contents.get(i);
                            if(!item.getType().equals(Material.AIR)){
                                ArmorStand itemStand = world.spawn(location, ArmorStand.class, armorStand -> {
                                    armorStand.setVisible(true);
                                    armorStand.setCustomName("cc:"+item.getType().toString());
                                    armorStand.setCustomNameVisible(false);
                                    armorStand.setSmall(true);
                                    armorStand.setArms(true);
                                    armorStand.setLeftArmPose(new EulerAngle(3.141593,0,0));
                                    armorStand.setGravity(false);
                                    armorStand.getEquipment().setItemInOffHand(item);
                                });
                                int j = (i - ((i/3)*3));
                                int k = i/3;
                                float x = 0.185f * j;
                                float z = 0.185f * k;
                                itemStand.teleport(itemStand.getLocation().add(0.125 + x, 0.04, 0.45 + z));
                            }
                        }
                    }else{
                        for (Entity entity : entities){
                            entity.remove();
                        }
                    }
                }
            }
        }, 10, 2);

    }

    public void addWorkbench(Location location) {
        if (!workbenches.containsKey(locationToString(location))) {
            workbenches.put(locationToString(location), new ArrayList<>());
        }
    }

    public void removeWorkbench(Location location) {
        workbenches.remove(locationToString(location));
    }

    public void setContents(Location location, ItemStack[] matrix) {
        if (workbenches.containsKey(locationToString(location))) {
            List<Map<String, Object>> items = new ArrayList<>();
            for (ItemStack itemStack : matrix) {
                if (itemStack == null) {
                    ItemStack newItem = new ItemStack(Material.AIR);
                    items.add(newItem.serialize());
                } else {
                    items.add(itemStack.serialize());
                }
            }
            workbenches.put(locationToString(location), items);
        }
    }

    public List<ItemStack> getContents(Location location){
        List<ItemStack> itemStacks = new ArrayList<>();
        if (workbenches.containsKey(locationToString(location))) {
            List<Map<String, Object>> items = workbenches.get(locationToString(location));
            for(Map<String, Object> map : items){
                itemStacks.add(ItemStack.deserialize(map));
            }
        }
        return itemStacks;
    }

    public boolean isWorkbench(Location location) {
        return workbenches.containsKey(locationToString(location));
    }

    public void addFurnace(Location location) {
        if (!furnaces.contains(locationToString(location))) {
            furnaces.add(locationToString(location));
        }
    }

    public void removeFurnace(Location location) {
        furnaces.remove(locationToString(location));
    }

    public boolean isFurnace(Location location) {
        return furnaces.contains(locationToString(location));
    }

    public String locationToString(Location location) {
        return location.getWorld().getUID() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    public Location stringToLocation(String loc) {
        String[] args = loc.split(";");
        return new Location(Bukkit.getWorld(UUID.fromString(args[0])), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
    }

    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(new File(CustomCrafting.getInst().getDataFolder() + File.separator + "workbenches.dat"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(workbenches);
            oos.writeObject(furnaces);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File file = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "workbenches.dat");
        if (file.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                try {
                    Object object = ois.readObject();
                    if (object instanceof HashMap) {
                        this.workbenches = (HashMap<String, List<Map<String, Object>>>) object;
                    } else if (object instanceof List) {
                        this.furnaces = (List<String>) object;
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

    public void migrate() {
        DataSet dataSet = null;
        File file = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "dataSet.dat");
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                try {
                    Object object = ois.readObject();
                    if (object instanceof DataSet) {
                        dataSet = (DataSet) object;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (dataSet != null) {
            dataSet.getWorkbenches();
        }
    }

    public void endTask() {
        Bukkit.getScheduler().cancelTask(task);
    }

}
