package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.type.TypeReference;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.node.ObjectNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class Conditions {

    @JsonIgnore
    private final Map<NamespacedKey, Condition<?>> valuesMap;

    //Conditions initialization
    public Conditions() {
        this.valuesMap = new HashMap<>();
    }

    @JsonCreator
    private Conditions(JsonNode node) {
        if (node.isArray()) {
            //Required for backwards compatibility with previous configs.
            this.valuesMap = new HashMap<>();
            node.elements().forEachRemaining(element -> {
                ((ObjectNode) element).put("key", String.valueOf(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, element.path("id").asText())));
                var condition = JacksonUtil.getObjectMapper().convertValue(element, Condition.class);
                if (!condition.getOption().equals(Option.IGNORE)) {
                    valuesMap.put(condition.getNamespacedKey(), condition);
                }
            });
        } else {
            this.valuesMap = JacksonUtil.getObjectMapper().convertValue(node.path("values"), new TypeReference<Set<Condition<?>>>() {
            }).stream().collect(Collectors.toMap(Condition::getNamespacedKey, condition -> condition));
        }
    }

    public boolean check(String id, ICustomRecipe<?> customRecipe, Data data) {
        var condition = getByID(id);
        return condition != null && condition.check(customRecipe, data);
    }

    public boolean checkConditions(ICustomRecipe<?> customRecipe, Data data) {
        return valuesMap.values().stream().allMatch(condition -> condition.check(customRecipe, data));
    }

    @JsonIgnore
    public EliteWorkbenchCondition getEliteCraftingTableCondition() {
        return (EliteWorkbenchCondition) getByID("elite_crafting_table");
    }

    public <C extends Condition<C>> C getByType(Class<C> type) {
        return valuesMap.values().stream().filter(type::isInstance).map(type::cast).findFirst().orElse(null);
    }

    public Condition<?> getByKey(NamespacedKey key) {
        return valuesMap.get(key);
    }

    public void setCondition(Condition<?> condition) {
        valuesMap.put(condition.getNamespacedKey(), condition);
    }

    public void removeCondition(Condition<?> condition) {
        valuesMap.remove(condition.getNamespacedKey());
    }

    public void removeCondition(NamespacedKey key) {
        valuesMap.remove(key);
    }

    public Collection<Condition<?>> getValues() {
        return valuesMap.values();
    }

    @Deprecated
    public Condition<?> getByID(String id) {
        return valuesMap.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, id));
    }

    public enum Option {
        EXACT,
        @Deprecated IGNORE,
        HIGHER,
        HIGHER_EXACT,
        LOWER,
        LOWER_EXACT,
        HIGHER_LOWER;

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

        public Data(Player player, Block block) {
            this(player, block, null);
        }

        public Data(Player player) {
            this(player, null, null);
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

}
