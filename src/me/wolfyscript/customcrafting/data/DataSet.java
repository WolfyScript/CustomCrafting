package me.wolfyscript.customcrafting.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class DataSet {

    static final long serialVersionUID = -134354643112L;

    private HashMap<String, String> workbenches = new HashMap<>();

    public DataSet(){}

    public void add(Location location, String name){
        workbenches.put(location.getWorld().getUID()+";"+location.getBlockX()+";"+location.getBlockY()+";"+location.getBlockZ(), name);
    }

    public void remove(Location location){
        workbenches.remove(location.getWorld().getUID()+";"+location.getBlockX()+";"+location.getBlockY()+";"+location.getBlockZ());
    }

    public String getData(Location location){
        return workbenches.get(location.getWorld().getUID()+";"+location.getBlockX()+";"+location.getBlockY()+";"+location.getBlockZ());
    }

    public HashMap<Location, String> getWorkbenches() {
        HashMap<Location, String> deserializedWorkbenches = new HashMap<>();
        for(String worldCode : this.workbenches.keySet()){
            String[] codes = worldCode.split(";");
            deserializedWorkbenches.put(new Location(Bukkit.getWorld(UUID.fromString(codes[0])), Double.parseDouble(codes[1]),Double.parseDouble(codes[2]),Double.parseDouble(codes[3])), this.workbenches.get(worldCode));
        }
        return deserializedWorkbenches;
    }

    public void setWorkbenches(HashMap<Location, String> newWorkbenches) {
        this.workbenches.clear();
        for(Location location : newWorkbenches.keySet()){
            this.workbenches.put(location.getWorld().getUID()+";"+location.getBlockX()+";"+location.getBlockY()+";"+location.getBlockZ(), newWorkbenches.get(location));
        }
    }

}
