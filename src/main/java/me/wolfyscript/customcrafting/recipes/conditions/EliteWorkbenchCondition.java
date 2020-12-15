package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItems;
import me.wolfyscript.utilities.api.inventory.custom_items.api_references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EliteWorkbenchCondition extends Condition {

    private final List<String> eliteWorkbenches;

    public EliteWorkbenchCondition() {
        super("elite_workbench");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.IGNORE);
        this.eliteWorkbenches = new ArrayList<>();
    }

    @Override
    public boolean check(ICustomRecipe recipe, Conditions.Data data) {
        if (option.equals(Conditions.Option.IGNORE)) {
            return true;
        }
        if (recipe instanceof EliteCraftingRecipe) {
            if (data.getBlock() != null) {
                Location location = data.getBlock().getLocation();
                CustomItem customItem = CustomItems.getStoredBlockItem(location);
                if (customItem != null && customItem.getApiReference() instanceof WolfyUtilitiesRef) {
                    if (eliteWorkbenches.contains(((WolfyUtilitiesRef) customItem.getApiReference()).getNamespacedKey().toString())) {
                        EliteWorkbenchData eliteWorkbenchData = (EliteWorkbenchData) customItem.getCustomData("elite_workbench");
                        return eliteWorkbenchData.isEnabled();
                    }
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void writeJson(@NotNull JsonGenerator gen) throws IOException {
        gen.writeArrayFieldStart("elite_workbenches");
        for (String s : eliteWorkbenches) {
            gen.writeString(s);
        }
        gen.writeEndArray();
    }

    @Override
    public void readFromJson(JsonNode node) {
        JsonNode array = node.get("elite_workbenches");
        array.elements().forEachRemaining(element -> {
            if(element.isValueNode()){
                addEliteWorkbenches(element.asText());
            }
        });
    }

    public void addEliteWorkbenches(String eliteWorkbenches) {
        if (!this.eliteWorkbenches.contains(eliteWorkbenches)) {
            this.eliteWorkbenches.add(eliteWorkbenches);
        }
    }

    public List<String> getEliteWorkbenches() {
        return eliteWorkbenches;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(option.toString());
        stringBuilder.append(";");
        for (String eliteWorkbench : eliteWorkbenches) {
            stringBuilder.append(eliteWorkbench).append(",");
        }
        return stringBuilder.toString();
    }
}
