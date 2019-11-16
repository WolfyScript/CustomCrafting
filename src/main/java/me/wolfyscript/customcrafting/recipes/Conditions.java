package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.conditions.*;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Conditions extends HashMap<String, Condition> {

    //Conditions initialization
    public Conditions() {
        addCondition(new PermissionCondition());
        addCondition(new AdvancedWorkbenchCondition());
        addCondition(new EliteWorkbenchCondition());
        addCondition(new WorldTimeCondition());
        addCondition(new WeatherCondition());
    }

    public Conditions(Map<String, String> map) {
        this();
        for (Map.Entry<String, String> condition : map.entrySet()) {
            getByID(condition.getKey()).fromString(condition.getValue());
        }
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

    public HashMap<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        for (String id : keySet()) {
            map.put(id, get(id).toString());
        }
        return map;
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

        public Data(Player player, Block block, InventoryView inventoryView) {
            this.player = player;
            this.block = block;
            this.inventoryView = inventoryView;
        }

        public Player getPlayer() {
            return player;
        }

        public void setPlayer(Player player) {
            this.player = player;
        }

        public Block getBlock() {
            return block;
        }

        public void setBlock(Block block) {
            this.block = block;
        }

        public InventoryView getInventoryView() {
            return inventoryView;
        }

        public void setInventoryView(InventoryView inventoryView) {
            this.inventoryView = inventoryView;
        }
    }

}
