package me.wolfyscript.customcrafting.recipes;

import com.google.gson.*;
import me.wolfyscript.customcrafting.recipes.conditions.*;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class Conditions extends HashMap<String, Condition> {

    //Conditions initialization
    public Conditions() {
        addCondition(new PermissionCondition());
        addCondition(new AdvancedWorkbenchCondition());
        addCondition(new EliteWorkbenchCondition());
        addCondition(new WorldTimeCondition());
        addCondition(new WeatherCondition());
    }

    public boolean checkConditions(CustomRecipe customRecipe, Data data) {
        for (Condition condition : values()) {
            if (!condition.check(customRecipe, data)) {
                return false;
            }
        }
        return true;
    }

    public Condition getByID(String id) {
        return get(id);
    }

    public void updateCondition(Condition condition) {
        put(condition.getId(), condition);
    }

    public void addCondition(Condition condition) {
        put(condition.getId(), condition);
    }

    public enum Option {
        EXACT, IGNORE, HIGHER, HIGHER_EXACT, LOWER, LOWER_EXACT, HIGHER_LOWER;

        private String displayString;

        Option() {
            this.displayString = "$inventories.recipe_creator.conditions.mode_names." + this.toString().toLowerCase(Locale.ROOT) + "$";
        }

        public String getDisplayString() {
            return displayString;
        }

        public String getDisplayString(WolfyUtilities api) {
            return api.getLanguageAPI().getActiveLanguage().replaceKeys(displayString);
        }
    }

    public static class Data {

        private Player player;
        private Block block;
        private InventoryView inventoryView;

        public Data(@Nullable Player player, Block block, @Nullable InventoryView inventoryView) {
            this.player = player;
            this.block = block;
            this.inventoryView = inventoryView;
        }

        @Nullable
        public Player getPlayer() {
            return player;
        }

        public void setPlayer(@Nullable Player player) {
            this.player = player;
        }

        public Block getBlock() {
            return block;
        }

        public void setBlock(Block block) {
            this.block = block;
        }

        @Nullable
        public InventoryView getInventoryView() {
            return inventoryView;
        }

        public void setInventoryView(@Nullable InventoryView inventoryView) {
            this.inventoryView = inventoryView;
        }
    }

    public static class Serialization implements JsonSerializer<Conditions>, JsonDeserializer<Conditions> {

        @Override
        public JsonElement serialize(Conditions conditions, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonElement element = new JsonArray();
            for(Condition condition : conditions.values()){
                ((JsonArray) element).add(condition.toJsonElement());
            }
            return element;
        }

        @Override
        public Conditions deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Conditions conditions = new Conditions();
            if(jsonElement instanceof JsonArray){
                Iterator<JsonElement> elementIterator = ((JsonArray) jsonElement).iterator();
                while(elementIterator.hasNext()){
                    JsonElement element = elementIterator.next();
                    if(element instanceof JsonObject){
                        String id = ((JsonObject) element).getAsJsonPrimitive("id").getAsString();
                        Conditions.Option option = Conditions.Option.valueOf(((JsonObject) element).getAsJsonPrimitive("option").getAsString());
                        Condition condition = conditions.getByID(id);
                        if(condition != null){
                            condition.setOption(option);
                            condition.fromJsonElement(element);
                        }
                    }
                }
            }
            return conditions;
        }
    }

}
