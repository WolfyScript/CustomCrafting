package me.wolfyscript.customcrafting.gui.recipe_creator.buttons.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.WorldNameCondition;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.inventory.InventoryClickEvent;

public class WorldNameConditionButton extends ActionButton<CCCache> {

    public WorldNameConditionButton() {
        super("conditions.world_name", new ButtonState<>("world_name", Material.GRASS_BLOCK, (cache, guiHandler, player, inventory, slot, event) -> {
            GuiWindow<CCCache> window = guiHandler.getWindow();
            ICustomRecipe<?,?> recipeConfig = guiHandler.getCustomCache().getRecipe();
            Conditions conditions = recipeConfig.getConditions();
            if (event instanceof InventoryClickEvent){
                if (((InventoryClickEvent) event).getClick().isRightClick()) {
                    //Change Mode
                    conditions.getByType(WorldNameCondition.class).toggleOption();
                    recipeConfig.setConditions(conditions);
                } else if (!((InventoryClickEvent) event).isShiftClick()) {
                    //CONFIGURE ELITE WORKBENCHES
                    window.openChat("world_name", guiHandler, (guiHandler1, player1, s, args) -> {
                        if (!s.isEmpty()) {
                            World world = Bukkit.getWorld(s);
                            if (world == null) {
                                window.sendMessage(player1, "missing_world");
                                return true;
                            }
                            WorldNameCondition condition = conditions.getByType(WorldNameCondition.class);
                            if (condition.getWorldNames().contains(s)) {
                                window.sendMessage(player1, "already_existing");
                                return true;
                            }
                            conditions.getByType(WorldNameCondition.class).addWorldName(s);
                            recipeConfig.setConditions(conditions);
                            return false;
                        }
                        return true;
                    });
                }else {
                    if (!conditions.getByType(WorldNameCondition.class).getWorldNames().isEmpty()) {
                        conditions.getByType(WorldNameCondition.class).getWorldNames().remove(conditions.getByType(WorldNameCondition.class).getWorldNames().size() - 1);
                        recipeConfig.setConditions(conditions);
                    }
                }
            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, b) -> {
            WorldNameCondition condition = guiHandler.getCustomCache().getRecipe().getConditions().getByType(WorldNameCondition.class);
            hashMap.put("%MODE%", condition.getOption().getDisplayString(CustomCrafting.inst().getApi()));
            for (int i = 0; i < 4; i++) {
                if (i < condition.getWorldNames().size()) {
                    hashMap.put("%var" + i + "%", condition.getWorldNames().get(i));
                } else {
                    hashMap.put("%var" + i + "%", "...");
                }
            }
            return itemStack;
        }));
    }
}
