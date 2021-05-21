package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.conditions.EliteWorkbenchConditionButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.conditions.WorldBiomeConditionButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.conditions.WorldNameConditionButton;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.*;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class ConditionsMenu extends CCWindow {

    private static final String BACK = "back";

    public ConditionsMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "conditions", 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>(BACK, new ButtonState<>(MainCluster.BACK, PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openPreviousWindow();
            return true;
        })));

        registerButton(new ActionButton<>("conditions.world_time", new ButtonState<>("world_time", Material.CLOCK, (cache, guiHandler, player, inventory, slot, event) -> {
            Conditions conditions = guiHandler.getCustomCache().getRecipe().getConditions();
            if(event instanceof InventoryClickEvent){
                if (((InventoryClickEvent) event).getClick().isRightClick()) {
                    //Change Mode
                    conditions.getByID("world_time").toggleOption();
                } else {
                    //Change Value
                    openChat("world_time", guiHandler, (guiHandler1, player1, s, strings) -> {
                        try {
                            long value = Long.parseLong(s);
                            ((WorldTimeCondition) conditions.getByID("world_time")).setTime(value);
                        } catch (NumberFormatException ex) {
                            api.getChat().sendKey(player1, "recipe_creator", "valid_number");
                        }
                        return false;
                    });
                }

            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            ICustomRecipe<?,?> recipeConfig = guiHandler.getCustomCache().getRecipe();
            hashMap.put("%VALUE%", ((WorldTimeCondition) recipeConfig.getConditions().getByID("world_time")).getTime());
            hashMap.put("%MODE%", recipeConfig.getConditions().getByID("world_time").getOption().getDisplayString(api));
            return itemStack;
        })));

        registerButton(new ActionButton<>("conditions.player_experience", new ButtonState<>("player_experience", Material.EXPERIENCE_BOTTLE, (cache, guiHandler, player, inventory, slot, event) -> {
            Conditions conditions = guiHandler.getCustomCache().getRecipe().getConditions();
            if(event instanceof InventoryClickEvent){
                if (((InventoryClickEvent) event).getClick().isRightClick()) {
                    //Change Mode
                    conditions.getByID("player_experience").toggleOption();
                } else {
                    //Change Value
                    openChat("player_experience", guiHandler, (guiHandler1, player1, s, strings) -> {
                        try {
                            int value = Integer.parseInt(s);
                            ((ExperienceCondition) conditions.getByID("player_experience")).setExpLevel(value);
                        } catch (NumberFormatException ex) {
                            api.getChat().sendKey(player1, "recipe_creator", "valid_number");
                        }
                        return false;
                    });
                }
            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            ICustomRecipe<?,?> recipeConfig = guiHandler.getCustomCache().getRecipe();
            hashMap.put("%VALUE%", ((ExperienceCondition) recipeConfig.getConditions().getByID("player_experience")).getExpLevel());
            hashMap.put("%MODE%", recipeConfig.getConditions().getByID("player_experience").getOption().getDisplayString(api));
            return itemStack;
        })));

        registerButton(new ActionButton<>("conditions.weather", new ButtonState<>("weather", Material.WATER_BUCKET, (cache, guiHandler, player, inventory, slot, event) -> {
            ICustomRecipe<?,?> recipeConfig = guiHandler.getCustomCache().getRecipe();
            Conditions conditions = recipeConfig.getConditions();
            if(event instanceof InventoryClickEvent){
                if (((InventoryClickEvent) event).getClick().isRightClick()) {
                    //Change Mode
                    conditions.getByID("weather").toggleOption();
                } else {
                    //Change Value
                    ((WeatherCondition) conditions.getByID("weather")).toggleWeather();
                }
            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            ICustomRecipe<?,?> recipeConfig = guiHandler.getCustomCache().getRecipe();
            hashMap.put("%VALUE%", ((WeatherCondition) recipeConfig.getConditions().getByID("weather")).getWeather().getDisplay(api));
            hashMap.put("%MODE%", recipeConfig.getConditions().getByID("weather").getOption().getDisplayString(api));
            return itemStack;
        })));

        registerButton(new ActionButton<>("conditions.advanced_workbench", new ButtonState<>("advanced_workbench", Material.CRAFTING_TABLE, (cache, guiHandler, player, inventory, slot, event) -> {
            if(event instanceof InventoryClickEvent){
                if (((InventoryClickEvent) event).getClick().isLeftClick()) {
                    //Change Mode
                    guiHandler.getCustomCache().getRecipe().getConditions().getByID("advanced_workbench").toggleOption();
                }
            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%MODE%", guiHandler.getCustomCache().getRecipe().getConditions().getByID("advanced_workbench").getOption().getDisplayString(api));
            return itemStack;
        })));

        registerButton(new EliteWorkbenchConditionButton());
        registerButton(new WorldBiomeConditionButton());
        registerButton(new WorldNameConditionButton());

        registerButton(new ActionButton<>("conditions.permission", new ButtonState<>("permission", Material.REDSTONE, (cache, guiHandler, player, inventory, slot, event) -> {
            if(event instanceof InventoryClickEvent){
                if (((InventoryClickEvent) event).getClick().isRightClick()) {
                    //Change Mode
                    guiHandler.getCustomCache().getRecipe().getConditions().getByID("permission").toggleOption();
                } else {
                    //SET Custom Permission String
                    openChat("permission", guiHandler, (guiHandler1, player1, s, strings) -> {
                        ((PermissionCondition) guiHandler.getCustomCache().getRecipe().getConditions().getByID("permission")).setPermission(s.trim());
                        return false;
                    });
                }
            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            ICustomRecipe<?, ?> recipeConfig = guiHandler.getCustomCache().getRecipe();
            hashMap.put("%VALUE%", ((PermissionCondition) recipeConfig.getConditions().getByID("permission")).getPermission());
            hashMap.put("%MODE%", recipeConfig.getConditions().getByID("permission").getOption().getDisplayString(api));
            return itemStack;
        })));

        registerButton(new ActionButton<>("conditions.craft_delay", new ButtonState<>("craft_delay", Material.CLOCK, (cache, guiHandler, player, inventory, slot, event) -> {
            Conditions conditions = guiHandler.getCustomCache().getRecipe().getConditions();
            if (event instanceof InventoryClickEvent) {
                if (((InventoryClickEvent) event).getClick().isRightClick()) {
                    //Change Mode
                    conditions.getByID("craft_delay").toggleOption();
                } else {
                    //Change Value
                    openChat("craft_delay", guiHandler, (guiHandler1, player1, s, strings) -> {
                        try {
                            long value = Long.parseLong(s);
                            ((CraftDelayCondition) conditions.getByID("craft_delay")).setDelay(value);
                        } catch (NumberFormatException ex) {
                            api.getChat().sendKey(player1, "recipe_creator", "valid_number");
                        }
                        return false;
                    });
                }

            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            ICustomRecipe<?, ?> recipeConfig = guiHandler.getCustomCache().getRecipe();
            hashMap.put("%VALUE%", ((CraftDelayCondition) recipeConfig.getConditions().getByID("craft_delay")).getDelay());
            hashMap.put("%MODE%", recipeConfig.getConditions().getByID("craft_delay").getOption().getDisplayString(api));
            return itemStack;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCCache cache = event.getGuiHandler().getCustomCache();
        event.setButton(0, BACK);

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
                values.add("conditions.craft_delay");
                break;
            case ELITE_WORKBENCH:
                values.add("conditions.permission");
                values.add("conditions.player_experience");
                values.add("conditions.elite_workbench");
                values.add("conditions.craft_delay");
                break;
            case BREWING_STAND:
            case GRINDSTONE:
                values.add("conditions.permission");
                values.add("conditions.player_experience");
                break;
            default:
                //No special conditions
        }
        int item = 9;
        for (int i = 0; i < values.size() && item < 45; i++) {
            event.setButton(item++, values.get(i));
        }
    }
}
