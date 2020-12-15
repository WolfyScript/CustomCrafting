package me.wolfyscript.customcrafting.data;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Workbenches {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities api;
    private final BukkitTask task;
    private final BukkitTask particles;

    private HashMap<String, List<ItemStack>> workbenches = new HashMap<>();
    private List<String> furnaces = new ArrayList<>();

    public Workbenches(CustomCrafting customCrafting) {
        this.api = WolfyUtilities.get(customCrafting);
        this.customCrafting = customCrafting;
        load();
        task = Bukkit.getScheduler().runTaskTimer(api.getPlugin(), () -> {
            if (customCrafting.getConfigHandler().getConfig().isAutoSaveMesage()) {
                api.getChat().sendConsoleMessage("[$msg.auto_save.start$]");
                save();
                api.getChat().sendConsoleMessage("[$msg.auto_save.complete$]");
            } else {
                save();
            }
        }, customCrafting.getConfigHandler().getConfig().getAutosaveInterval() * 1200, customCrafting.getConfigHandler().getConfig().getAutosaveInterval() * 1200);
        particles = Bukkit.getScheduler().runTaskTimer(api.getPlugin(), () -> workbenches.keySet().stream().map(this::stringToLocation).filter(l -> l != null && l.getWorld() != null && l.getWorld().isChunkLoaded(l.getBlockX() >> 4, l.getBlockZ() >> 4)).forEach(l -> l.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, l.clone().add(0.5, 1.3, 0.5), 4, 0, 0, 0, 0.5)), 10, 2);
    }

    public void addWorkbench(Location location) {
        workbenches.putIfAbsent(locationToString(location), new ArrayList<>());
    }

    public void removeWorkbench(Location location) {
        workbenches.remove(locationToString(location));
    }

    @Deprecated
    public void setContents(Location location, ItemStack[] matrix) {
        if (workbenches.containsKey(locationToString(location))) {
            workbenches.put(locationToString(location), Arrays.stream(matrix).map(itemStack -> itemStack == null ? new ItemStack(Material.AIR) : new ItemStack(itemStack)).collect(Collectors.toList()));
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

    @Nullable
    private Location stringToLocation(String loc) {
        String[] args = loc.split(";");
        if (args.length < 4) return null;
        return new Location(Bukkit.getWorld(UUID.fromString(args[0])), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
    }

    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(new File(customCrafting.getDataFolder() + File.separator + "workbenches.dat"));
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(fos);
            oos.writeObject(workbenches);
            oos.writeObject(furnaces);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File file = new File(customCrafting.getDataFolder() + File.separator + "workbenches.dat");
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
        particles.cancel();
        task.cancel();
    }

}
