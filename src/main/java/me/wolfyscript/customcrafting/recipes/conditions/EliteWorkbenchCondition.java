package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.world.WorldUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EliteWorkbenchCondition extends Condition {

    private final List<NamespacedKey> eliteWorkbenches;

    public EliteWorkbenchCondition() {
        super("elite_workbench");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.IGNORE);
        this.eliteWorkbenches = new ArrayList<>();
    }

    @Override
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
        if (option.equals(Conditions.Option.IGNORE)) {
            return true;
        }
        if (recipe instanceof EliteCraftingRecipe) {
            if (data.getBlock() != null) {
                Location location = data.getBlock().getLocation();
                CustomItem customItem = WorldUtils.getWorldCustomItemStore().getCustomItem(location);
                if (customItem != null && customItem.getApiReference() instanceof WolfyUtilitiesRef) {
                    if (eliteWorkbenches.contains(((WolfyUtilitiesRef) customItem.getApiReference()).getNamespacedKey())) {
                        EliteWorkbenchData eliteWorkbenchData = (EliteWorkbenchData) customItem.getCustomData(new NamespacedKey("customcrafting","elite_workbench"));
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
        for (NamespacedKey s : eliteWorkbenches) {
            gen.writeString(s.toString());
        }
        gen.writeEndArray();
    }

    @Override
    public void readFromJson(JsonNode node) {
        JsonNode array = node.get("elite_workbenches");
        array.elements().forEachRemaining(element -> {
            if(element.isValueNode()){
                addEliteWorkbenches(NamespacedKey.getByString(element.asText()));
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
