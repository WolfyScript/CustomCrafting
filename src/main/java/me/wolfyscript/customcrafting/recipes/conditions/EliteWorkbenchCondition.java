package me.wolfyscript.customcrafting.recipes.conditions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class EliteWorkbenchCondition extends Condition {

    private List<String> eliteWorkbenches;

    public EliteWorkbenchCondition() {
        super("elite_workbench");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.IGNORE);
        this.eliteWorkbenches = new ArrayList<>();
    }

    @Override
    public boolean check(CustomRecipe recipe, Conditions.Data data) {
        if (option.equals(Conditions.Option.IGNORE)) {
            return true;
        }
        if (recipe instanceof EliteCraftingRecipe) {
            if (data.getBlock() != null) {
                Location location = data.getBlock().getLocation();
                CustomItem customItem = CustomItems.getStoredBlockItem(location);
                if (customItem != null) {
                    if (eliteWorkbenches.contains(customItem.getId())) {
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
    public JsonElement toJsonElement() {
        JsonObject jsonObject = (JsonObject) super.toJsonElement();
        JsonArray jsonArray = new JsonArray();
        eliteWorkbenches.forEach(s -> jsonArray.add(s));
        jsonObject.add("elite_workbenches", jsonArray);
        return jsonObject;
    }

    @Override
    public void fromJsonElement(JsonElement jsonElement) {
        JsonObject jsonObject = (JsonObject) jsonElement;
        JsonArray jsonArray = jsonObject.getAsJsonArray("elite_workbenches");
        Iterator<JsonElement> iterator = jsonArray.iterator();
        while (iterator.hasNext()){
            JsonElement element = iterator.next();
            if(element instanceof JsonPrimitive){
                addEliteWorkbenches(element.getAsString());
            }
        }
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
