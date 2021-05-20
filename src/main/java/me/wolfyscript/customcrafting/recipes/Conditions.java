package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.conditions.*;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonParser;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.DeserializationContext;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

@JsonSerialize(using = Conditions.Serialization.class)
@JsonDeserialize(using = Conditions.Deserialization.class)
public class Conditions extends HashMap<String, Condition> {

    //Conditions initialization
    public Conditions() {
        addCondition(new PermissionCondition());
        addCondition(new AdvancedWorkbenchCondition());
        addCondition(new EliteWorkbenchCondition());
        addCondition(new WorldTimeCondition());
        addCondition(new WorldNameCondition());
        addCondition(new WeatherCondition());
        addCondition(new ExperienceCondition());
        addCondition(new WorldBiomeCondition());
        addCondition(new CraftDelayCondition());
    }

    public boolean checkConditions(ICustomRecipe<?,?> customRecipe, Data data) {
        for (Condition condition : values()) {
            if (!condition.check(customRecipe, data)) {
                return false;
            }
        }
        return true;
    }

    public EliteWorkbenchCondition getEliteCraftingTableCondition() {
        return (EliteWorkbenchCondition) get("elite_crafting_table");
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

        private final String displayString;

        Option() {
            this.displayString = "$inventories.recipe_creator.conditions.mode_names." + this.toString().toLowerCase(Locale.ROOT) + "$";
        }

        public String getDisplayString() {
            return displayString;
        }

        public String getDisplayString(WolfyUtilities api) {
            return api.getLanguageAPI().replaceKeys(displayString);
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

    public static class Serialization extends StdSerializer<Conditions> {

        public Serialization() {
            super(Conditions.class);
        }

        protected Serialization(Class<Conditions> t) {
            super(t);
        }

        @Override
        public void serialize(Conditions conditions, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            gen.writeStartArray();
            for(Condition condition : conditions.values()){
                gen.writeStartObject();
                gen.writeStringField("id", condition.getId());
                gen.writeStringField("option", condition.getOption().toString());
                condition.writeJson(gen);
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }
    }

    public static class Deserialization extends StdDeserializer<Conditions> {

        public Deserialization() {
            super(Conditions.class);
        }

        protected Deserialization(Class<?> vc) {
            super(vc);
        }

        @Override
        public Conditions deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = p.readValueAsTree();
            Conditions conditions = new Conditions();
            if (node.isArray()) {
                node.elements().forEachRemaining(element -> {
                    if (element.isObject()) {
                        String id = element.get("id").asText();
                        Conditions.Option option = Conditions.Option.valueOf(element.get("option").asText());
                        Condition condition = conditions.getByID(id);
                        if (condition != null) {
                            condition.setOption(option);
                            condition.readFromJson(element);
                        }
                    }
                });
            }
            return conditions;
        }
    }

}
