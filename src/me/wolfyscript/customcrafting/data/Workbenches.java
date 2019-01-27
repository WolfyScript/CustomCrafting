package me.wolfyscript.customcrafting.data;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Workbenches {

    private WolfyUtilities api;
    private int task;

    private HashMap<String, List<Map<String, Object>>> workbenches = new HashMap<>();

    public Workbenches(WolfyUtilities api){
        this.api = api;
        load();
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(api.getPlugin(), () -> {
            api.sendConsoleMessage("[Auto save Workbenches]");
            save();
            api.sendConsoleMessage("[ Auto save complete! ]");
        }, CustomCrafting.getConfigHandler().getConfig().getAutosaveInterval() * 1200, CustomCrafting.getConfigHandler().getConfig().getAutosaveInterval() * 1200);
    }

    public void addWorkbench(Location location){
        if(!workbenches.containsKey(location.getWorld().getUID()+";"+location.getBlockX()+";"+location.getBlockY()+";"+location.getBlockZ())){
            workbenches.put(location.getWorld().getUID()+";"+location.getBlockX()+";"+location.getBlockY()+";"+location.getBlockZ(), new ArrayList<>());
        }
    }

    public void setContents(Location location, ItemStack[] matrix){
        if(workbenches.containsKey(location.getWorld().getUID()+";"+location.getBlockX()+";"+location.getBlockY()+";"+location.getBlockZ())){
            List<Map<String, Object>> items = new ArrayList<>();
            for(ItemStack itemStack : matrix){
                if(itemStack == null){
                    ItemStack newItem = new ItemStack(Material.AIR);
                    items.add(newItem.serialize());
                }else{
                    items.add(itemStack.serialize());
                }
            }
            workbenches.put(location.getWorld().getUID()+";"+location.getBlockX()+";"+location.getBlockY()+";"+location.getBlockZ(), items);
        }
    }

    public boolean isWorkbench(Location location){
        return workbenches.containsKey(location.getWorld().getUID()+";"+location.getBlockX()+";"+location.getBlockY()+";"+location.getBlockZ());
    }

    public void save(){
        try {
            FileOutputStream fos = new FileOutputStream(new File(CustomCrafting.getInst().getDataFolder() + File.separator + "workbenches.dat"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(workbenches);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(){
        File file = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "workbenches.dat");
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                try {
                    Object object = ois.readObject();
                    if(object instanceof HashMap){
                        this.workbenches = (HashMap<String, List<Map<String, Object>>>) object;
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

    public void migrate(){
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
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(dataSet != null){
            dataSet.getWorkbenches();
        }
    }

    public void endTask(){
        Bukkit.getScheduler().cancelTask(task);
    }

}
