package me.wolfyscript.customcrafting.configs.custom_data;

import me.wolfyscript.utilities.api.custom_items.custom_data.CustomData;
import org.bukkit.util.NumberConversions;

import java.util.HashMap;
import java.util.Map;

public class EliteWorkbenchData extends CustomData implements Cloneable {

    private boolean advancedRecipes;
    private boolean enabled;
    private boolean allowHoppers;
    private boolean keepItems;
    private int gridSize;

    public EliteWorkbenchData() {
        super("elite_workbench");
        this.enabled = false;
        this.gridSize = 3;
        this.allowHoppers = false;
        this.keepItems = true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAdvancedRecipes() {
        return advancedRecipes;
    }

    public void setAdvancedRecipes(boolean advancedRecipes) {
        this.advancedRecipes = advancedRecipes;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public void setAllowHoppers(boolean allowHoppers) {
        this.allowHoppers = allowHoppers;
    }

    public boolean isAllowHoppers() {
        return allowHoppers;
    }

    public void setKeepItems(boolean keepItems) {
        this.keepItems = keepItems;
    }

    public boolean isKeepItems() {
        return keepItems;
    }

    @Override
    public CustomData getDefaultCopy() {
        return new EliteWorkbenchData();
    }

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("enabled", enabled);
        map.put("gridSize", gridSize);
        map.put("advancedRecipes", advancedRecipes);
        map.put("allowHoppers", allowHoppers);
        map.put("keepItems", keepItems);

        return map;
    }

    @Override
    public EliteWorkbenchData fromMap(Map<String, Object> map) {
        EliteWorkbenchData eliteWorkbenchData = new EliteWorkbenchData();
        eliteWorkbenchData.setEnabled((Boolean) map.getOrDefault("enabled", false));
        eliteWorkbenchData.setGridSize(NumberConversions.toInt(map.getOrDefault("gridSize", 3)));
        eliteWorkbenchData.setAdvancedRecipes((Boolean) map.getOrDefault("advancedRecipes", false));
        return eliteWorkbenchData;
    }

    @Override
    public EliteWorkbenchData clone() {
        EliteWorkbenchData eliteWorkbenchData = new EliteWorkbenchData();
        eliteWorkbenchData.setAdvancedRecipes(isAdvancedRecipes());
        eliteWorkbenchData.setEnabled(isEnabled());
        eliteWorkbenchData.setGridSize(getGridSize());
        return eliteWorkbenchData;
    }
}
