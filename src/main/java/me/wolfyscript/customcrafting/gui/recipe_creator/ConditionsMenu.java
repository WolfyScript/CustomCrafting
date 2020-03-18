package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.EliteWorkbenchConditionButton;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.PermissionCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WeatherCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WorldTimeCondition;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConditionsMenu extends ExtendedGuiWindow {

    public ConditionsMenu(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("conditions", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));

        registerButton(new ActionButton("conditions.world_time", new ButtonState("world_time", Material.CLOCK, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                Conditions conditions = ((TestCache) guiHandler.getCustomCache()).getRecipeConfig().getConditions();
                if (event.getClick().isRightClick()) {
                    //Change Mode
                    conditions.getByID("world_time").toggleOption();
                } else {
                    //Change Value
                    openChat("world_time", guiHandler, (guiHandler1, player1, s, strings) -> {
                        try {
                            long value = Long.parseLong(s);
                            ((WorldTimeCondition) conditions.getByID("world_time")).setTime(value);
                            ((TestCache) guiHandler.getCustomCache()).getRecipeConfig().setConditions(conditions);
                        } catch (NumberFormatException ex) {
                            api.sendPlayerMessage(player1, "recipe_creator", "valid_number");
                        }
                        return false;
                    });
                }
                ((TestCache) guiHandler.getCustomCache()).getRecipeConfig().setConditions(conditions);
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean b) {
                RecipeConfig recipeConfig = ((TestCache) guiHandler.getCustomCache()).getRecipeConfig();
                hashMap.put("%VALUE%", ((WorldTimeCondition) recipeConfig.getConditions().getByID("world_time")).getTime());
                hashMap.put("%MODE%", recipeConfig.getConditions().getByID("world_time").getOption().getDisplayString(api));
                return itemStack;
            }
        })));

        registerButton(new ActionButton("conditions.weather", new ButtonState("weather", Material.WATER_BUCKET, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                RecipeConfig recipeConfig = ((TestCache) guiHandler.getCustomCache()).getRecipeConfig();
                Conditions conditions = recipeConfig.getConditions();
                if (event.getClick().isRightClick()) {
                    //Change Mode
                    conditions.getByID("weather").toggleOption();
                } else {
                    //Change Value
                    ((WeatherCondition) conditions.getByID("weather")).toggleWeather();
                }
                recipeConfig.setConditions(conditions);
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean b) {
                RecipeConfig recipeConfig = ((TestCache) guiHandler.getCustomCache()).getRecipeConfig();
                hashMap.put("%VALUE%", ((WeatherCondition) recipeConfig.getConditions().getByID("weather")).getWeather().getDisplay(api));
                hashMap.put("%MODE%", recipeConfig.getConditions().getByID("weather").getOption().getDisplayString(api));
                return itemStack;
            }
        })));

        registerButton(new ActionButton("conditions.advanced_workbench", new ButtonState("advanced_workbench", Material.CRAFTING_TABLE, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                if (event.getClick().isLeftClick()) {
                    //Change Mode
                    Conditions conditions = ((TestCache) guiHandler.getCustomCache()).getRecipeConfig().getConditions();
                    conditions.getByID("advanced_workbench").toggleOption();
                    ((TestCache) guiHandler.getCustomCache()).getRecipeConfig().setConditions(conditions);
                }
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean b) {
                hashMap.put("%MODE%", ((TestCache) guiHandler.getCustomCache()).getRecipeConfig().getConditions().getByID("advanced_workbench").getOption().getDisplayString(api));
                return itemStack;
            }
        })));

        registerButton(new EliteWorkbenchConditionButton());

        registerButton(new ActionButton("conditions.permission", new ButtonState("permission", Material.REDSTONE, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                RecipeConfig recipeConfig = ((TestCache) guiHandler.getCustomCache()).getRecipeConfig();
                Conditions conditions = recipeConfig.getConditions();
                if (event.getClick().isRightClick()) {
                    //Change Mode
                    conditions.getByID("permission").toggleOption();
                    recipeConfig.setConditions(conditions);
                } else {
                    //SET Custom Permission String
                    openChat("permission", guiHandler, (guiHandler1, player1, s, strings) -> {
                        ((PermissionCondition) conditions.getByID("permission")).setPermission(s.trim());
                        recipeConfig.setConditions(conditions);
                        return false;
                    });
                }
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean b) {
                RecipeConfig recipeConfig = ((TestCache) guiHandler.getCustomCache()).getRecipeConfig();
                hashMap.put("%VALUE%", ((PermissionCondition) recipeConfig.getConditions().getByID("permission")).getPermission());
                hashMap.put("%MODE%", recipeConfig.getConditions().getByID("permission").getOption().getDisplayString(api));
                return itemStack;
            }
        })));

    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            TestCache cache = (TestCache)event.getGuiHandler().getCustomCache();
            event.setButton(0, "back");

            List<String> values = new ArrayList<>();
            values.add("conditions.world_time");
            values.add("conditions.weather");
            switch (cache.getSetting()) {
                case WORKBENCH:
                    values.add("conditions.permission");
                    values.add("conditions.advanced_workbench");
                    break;
                case ELITE_WORKBENCH:
                    values.add("conditions.permission");
                    values.add("conditions.elite_workbench");
                    break;
                case BREWING_STAND:
                case GRINDSTONE:
                    values.add("conditions.permission");
            }
            int item = 9;
            for (int i = 0; i < values.size() && item < 45; i++) {
                event.setButton(item++, values.get(i));
            }
        }
    }
}
