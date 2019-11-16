package me.wolfyscript.customcrafting.data;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Workbenches {

    private WolfyUtilities api;
    private int task;
    private int particles;

    private HashMap<String, List<ItemStack>> workbenches = new HashMap<>();
    private List<String> furnaces = new ArrayList<>();

    public Workbenches(WolfyUtilities api) {
        this.api = api;
        load();
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(api.getPlugin(), () -> {
            if (CustomCrafting.getConfigHandler().getConfig().isAutoSaveMesage()) {
                api.sendConsoleMessage("[$msg.auto_save.start$]");
                save();
                api.sendConsoleMessage("[$msg.auto_save.complete$]");
            } else {
                save();
            }
        }, CustomCrafting.getConfigHandler().getConfig().getAutosaveInterval() * 1200, CustomCrafting.getConfigHandler().getConfig().getAutosaveInterval() * 1200);

        particles = Bukkit.getScheduler().scheduleSyncRepeatingTask(api.getPlugin(), () -> {
            for (String loc : workbenches.keySet()) {
                Location location = stringToLocation(loc);
                if (location != null) {
                    World world = location.getWorld();
                    if (world != null) {
                        world.spawnParticle(Particle.ENCHANTMENT_TABLE, location.clone().add(0.5, 1.3, 0.5), 4, 0, 0, 0, 0.5);
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

    @Deprecated
    public void setContents(Location location, ItemStack[] matrix) {
        if (workbenches.containsKey(locationToString(location))) {
            List<ItemStack> items = new ArrayList<>();
            for (ItemStack itemStack : matrix) {
                if (itemStack == null) {
                    ItemStack newItem = new ItemStack(Material.AIR);
                    items.add(newItem);
                } else {
                    items.add(new ItemStack(itemStack));
                }
            }
            workbenches.put(locationToString(location), items);
        }
    }

    @Deprecated
    public List<ItemStack> getContents(Location location) {
        if (workbenches.containsKey(locationToString(location))) {
            return new ArrayList<>(workbenches.get(locationToString(location)));
        }
        return new ArrayList<>();
    }

    public boolean isWorkbench(Location location) {
        return workbenches.containsKey(locationToString(location));
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
            FileOutputStream fos = new FileOutputStream(new File(CustomCrafting.getInst().getDataFolder() + File.separator + "workbenches.dat"));
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(fos);
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
                BukkitObjectInputStream ois = new BukkitObjectInputStream(fis);
                try {
                    Object object = ois.readObject();
                    if (object instanceof HashMap) {
                        this.workbenches = (HashMap<String, List<ItemStack>>) object;
                    }
                    Object object2 = ois.readObject();
                    if (object2 instanceof List) {
                        this.furnaces = (List<String>) object2;
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

    public void endTask() {
        Bukkit.getScheduler().cancelTask(task);
    }

}
