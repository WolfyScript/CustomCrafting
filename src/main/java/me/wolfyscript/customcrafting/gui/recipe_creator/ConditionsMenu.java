package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.conditions.EliteWorkbenchConditionButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.conditions.WorldBiomeConditionButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.conditions.WorldNameConditionButton;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.ExperienceCondition;
import me.wolfyscript.customcrafting.recipes.conditions.PermissionCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WeatherCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WorldTimeCondition;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ConditionsMenu extends ExtendedGuiWindow {

    public ConditionsMenu(GuiCluster<TestCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "conditions", 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));

        registerButton(new ActionButton("conditions.world_time", new ButtonState("world_time", Material.CLOCK, (guiHandler, player, inventory, i, event) -> {
            Conditions conditions = ((TestCache) guiHandler.getCustomCache()).getRecipe().getConditions();
            if (event.getClick().isRightClick()) {
                //Change Mode
                conditions.getByID("world_time").toggleOption();
            } else {
                //Change Value
                openChat("world_time", (GuiHandler<TestCache>) guiHandler, (guiHandler1, player1, s, strings) -> {
                    try {
                        long value = Long.parseLong(s);
                        ((WorldTimeCondition) conditions.getByID("world_time")).setTime(value);
                    } catch (NumberFormatException ex) {
                        api.getChat().sendPlayerMessage(player1, "recipe_creator", "valid_number");
                    }
                    return false;
                });
            }
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            ICustomRecipe<?> recipeConfig = ((TestCache) guiHandler.getCustomCache()).getRecipe();
            hashMap.put("%VALUE%", ((WorldTimeCondition) recipeConfig.getConditions().getByID("world_time")).getTime());
            hashMap.put("%MODE%", recipeConfig.getConditions().getByID("world_time").getOption().getDisplayString(api));
            return itemStack;
        })));

        registerButton(new ActionButton("conditions.player_experience", new ButtonState("player_experience", Material.EXPERIENCE_BOTTLE, (guiHandler, player, inventory, i, event) -> {
            Conditions conditions = ((TestCache) guiHandler.getCustomCache()).getRecipe().getConditions();
            if (event.getClick().isRightClick()) {
                //Change Mode
                conditions.getByID("player_experience").toggleOption();
            } else {
                //Change Value
                openChat("player_experience", (GuiHandler<TestCache>) guiHandler, (guiHandler1, player1, s, strings) -> {
                    try {
                        int value = Integer.parseInt(s);
                        ((ExperienceCondition) conditions.getByID("player_experience")).setExpLevel(value);
                    } catch (NumberFormatException ex) {
                        api.getChat().sendPlayerMessage(player1, "recipe_creator", "valid_number");
                    }
                    return false;
                });
            }
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            ICustomRecipe recipeConfig = ((TestCache) guiHandler.getCustomCache()).getRecipe();
            hashMap.put("%VALUE%", ((ExperienceCondition) recipeConfig.getConditions().getByID("player_experience")).getExpLevel());
            hashMap.put("%MODE%", recipeConfig.getConditions().getByID("player_experience").getOption().getDisplayString(api));
            return itemStack;
        })));

        registerButton(new ActionButton("conditions.weather", new ButtonState("weather", Material.WATER_BUCKET, (guiHandler, player, inventory, i, event) -> {
            ICustomRecipe recipeConfig = ((TestCache) guiHandler.getCustomCache()).getRecipe();
            Conditions conditions = recipeConfig.getConditions();
            if (event.getClick().isRightClick()) {
                //Change Mode
                conditions.getByID("weather").toggleOption();
            } else {
                //Change Value
                ((WeatherCondition) conditions.getByID("weather")).toggleWeather();
            }
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            ICustomRecipe recipeConfig = ((TestCache) guiHandler.getCustomCache()).getRecipe();
            hashMap.put("%VALUE%", ((WeatherCondition) recipeConfig.getConditions().getByID("weather")).getWeather().getDisplay(api));
            hashMap.put("%MODE%", recipeConfig.getConditions().getByID("weather").getOption().getDisplayString(api));
            return itemStack;
        })));

        registerButton(new ActionButton("conditions.advanced_workbench", new ButtonState("advanced_workbench", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, event) -> {
            if (event.getClick().isLeftClick()) {
                //Change Mode
                ((TestCache) guiHandler.getCustomCache()).getRecipe().getConditions().getByID("advanced_workbench").toggleOption();
            }
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%MODE%", ((TestCache) guiHandler.getCustomCache()).getRecipe().getConditions().getByID("advanced_workbench").getOption().getDisplayString(api));
            return itemStack;
        })));

        registerButton(new EliteWorkbenchConditionButton());
        registerButton(new WorldBiomeConditionButton());
        registerButton(new WorldNameConditionButton());

        registerButton(new ActionButton("conditions.permission", new ButtonState("permission", Material.REDSTONE, (guiHandler, player, inventory, i, event) -> {
            if (event.getClick().isRightClick()) {
                //Change Mode
                ((TestCache) guiHandler.getCustomCache()).getRecipe().getConditions().getByID("permission").toggleOption();
            } else {
                //SET Custom Permission String
                openChat("permission", (GuiHandler<TestCache>) guiHandler, (guiHandler1, player1, s, strings) -> {
                    ((PermissionCondition) ((TestCache) guiHandler.getCustomCache()).getRecipe().getConditions().getByID("permission")).setPermission(s.trim());
                    return false;
                });
            }
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            ICustomRecipe recipeConfig = ((TestCache) guiHandler.getCustomCache()).getRecipe();
            hashMap.put("%VALUE%", ((PermissionCondition) recipeConfig.getConditions().getByID("permission")).getPermission());
            hashMap.put("%MODE%", recipeConfig.getConditions().getByID("permission").getOption().getDisplayString(api));
            return itemStack;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<TestCache> event) {
        super.onUpdateAsync(event);
        TestCache cache = event.getGuiHandler().getCustomCache();
        event.setButton(0, "back");

        List<String> values = new ArrayList<>();
        values.add("conditions.world_time");
        values.add("conditions.world_name");
        values.add("conditions.world_biome");
        values.add("conditions.weather");
        switch (cache.getRecipeType().getType()) {
            case WORKBENCH:
                values.add("conditions.permission");
                values.add("conditions.player_experience");
                values.add("conditions.advanced_workbench");
                break;
            case ELITE_WORKBENCH:
                values.add("conditions.permission");
                values.add("conditions.player_experience");
                values.add("conditions.elite_workbench");
                break;
            case BREWING_STAND:
            case GRINDSTONE:
                values.add("conditions.permission");
                values.add("conditions.player_experience");
        }
        int item = 9;
        for (int i = 0; i < values.size() && item < 45; i++) {
            event.setButton(item++, values.get(i));
        }
    }
}
