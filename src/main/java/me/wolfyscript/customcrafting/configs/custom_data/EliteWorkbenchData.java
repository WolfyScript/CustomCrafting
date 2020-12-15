package me.wolfyscript.customcrafting.configs.custom_data;

import me.wolfyscript.utilities.api.inventory.custom_items.custom_data.CustomData;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Objects;

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
        this.keepItems = false;
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
    public void writeToJson(JsonGenerator gen) throws IOException {
        gen.writeBooleanField("enabled", enabled);
        gen.writeNumberField("gridSize", gridSize);
        gen.writeBooleanField("advancedRecipes", advancedRecipes);
        gen.writeBooleanField("allowHoppers", allowHoppers);
        gen.writeBooleanField("keepItems", keepItems);
    }

    @Override
    public CustomData readFromJson(JsonNode node) throws IOException {
        EliteWorkbenchData eliteWorkbenchData = new EliteWorkbenchData();
        eliteWorkbenchData.setEnabled(node.get("enabled").asBoolean(false));
        eliteWorkbenchData.setGridSize(node.get("gridSize").asInt(3));
        eliteWorkbenchData.setAdvancedRecipes(node.get("advancedRecipes").asBoolean(false));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EliteWorkbenchData)) return false;
        if (!super.equals(o)) return false;
        EliteWorkbenchData that = (EliteWorkbenchData) o;
        return advancedRecipes == that.advancedRecipes &&
                enabled == that.enabled &&
                allowHoppers == that.allowHoppers &&
                keepItems == that.keepItems &&
                gridSize == that.gridSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), advancedRecipes, enabled, allowHoppers, keepItems, gridSize);
    }
}
