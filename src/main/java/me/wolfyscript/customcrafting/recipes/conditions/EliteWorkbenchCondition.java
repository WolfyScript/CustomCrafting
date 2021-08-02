package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EliteWorkbenchCondition extends Condition {

    private final List<NamespacedKey> eliteWorkbenches;

    public EliteWorkbenchCondition() {
        super("elite_crafting_table");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.IGNORE);
        this.eliteWorkbenches = new ArrayList<>();
    }

    @Override
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
        if (option.equals(Conditions.Option.IGNORE)) {
            return true;
        }
        if (RecipeType.ELITE_WORKBENCH.isInstance(recipe)) {
            if (data.getBlock() != null) {
                CustomItem customItem = NamespacedKeyUtils.getCustomItem(data.getBlock());
                if (customItem != null && customItem.getApiReference() instanceof WolfyUtilitiesRef wolfyUtilsRef) {
                    return eliteWorkbenches.contains(wolfyUtilsRef.getNamespacedKey()) && ((EliteWorkbenchData) customItem.getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).isEnabled();
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void writeJson(@NotNull JsonGenerator gen) throws IOException {
        gen.writeArrayFieldStart("elite_crafting_tables");
        for (NamespacedKey s : eliteWorkbenches) {
            gen.writeString(s.toString());
        }
        gen.writeEndArray();
    }

    @Override
    public void readFromJson(JsonNode node) {
        JsonNode array = node.has("elite_crafting_tables") ? node.get("elite_crafting_tables") : node.get("elite_workbenches");
        array.elements().forEachRemaining(element -> {
            if (element.isValueNode()) {
                addEliteWorkbenches(NamespacedKey.of(element.asText()));
            }
        });
    }

    public void addEliteWorkbenches(NamespacedKey eliteWorkbenches) {
        if (!this.eliteWorkbenches.contains(eliteWorkbenches)) {
            this.eliteWorkbenches.add(eliteWorkbenches);
        }
    }

    public List<NamespacedKey> getEliteWorkbenches() {
        return eliteWorkbenches;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(option.toString());
        stringBuilder.append(";");
        for (NamespacedKey eliteWorkbench : eliteWorkbenches) {
            stringBuilder.append(eliteWorkbench.toString()).append(",");
        }
        return stringBuilder.toString();
    }
}
