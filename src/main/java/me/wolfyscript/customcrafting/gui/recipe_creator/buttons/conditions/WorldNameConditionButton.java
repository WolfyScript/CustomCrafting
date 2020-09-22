package me.wolfyscript.customcrafting.gui.recipe_creator.buttons.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.WorldNameCondition;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

public class WorldNameConditionButton extends ActionButton {

    public WorldNameConditionButton() {
        super("conditions.world_name", new ButtonState("world_name", Material.GRASS_BLOCK, (guiHandler, player, inventory, i, event) -> {
            GuiWindow window = guiHandler.getCurrentInv();
            ICustomRecipe recipeConfig = ((TestCache) guiHandler.getCustomCache()).getRecipe();
            Conditions conditions = recipeConfig.getConditions();
            if (event.getClick().isRightClick()) {
                //Change Mode
                conditions.getByID("world_name").toggleOption();
                recipeConfig.setConditions(conditions);
            } else if (!event.isShiftClick()) {
                //CONFIGURE ELITE WORKBENCHES
                guiHandler.getCurrentInv().openChat("world_name", guiHandler, (guiHandler1, player1, s, args) -> {
                    if (!s.isEmpty()) {
                        World world = Bukkit.getWorld(s);
                        if (world == null) {
                            window.sendMessage(player1, "missing_world");
                            return true;
                        }
                        WorldNameCondition condition = (WorldNameCondition) conditions.getByID("world_name");
                        if (condition.getWorldNames().contains(s)) {
                            window.sendMessage(player1, "already_existing");
                            return true;
                        }
                        ((WorldNameCondition) conditions.getByID("world_name")).addWorldName(s);
                        recipeConfig.setConditions(conditions);
                        return false;
                    }
                    return true;
                });
            } else {
                if (((WorldNameCondition) conditions.getByID("world_name")).getWorldNames().size() > 0) {
                    ((WorldNameCondition) conditions.getByID("world_name")).getWorldNames().remove(((WorldNameCondition) conditions.getByID("world_name")).getWorldNames().size() - 1);
                    recipeConfig.setConditions(conditions);
                }
                return false;
            }
            return true;
        }, (hashMap, guiHandler, player, itemStack, slot, b) -> {
            WorldNameCondition condition = (WorldNameCondition) ((TestCache) guiHandler.getCustomCache()).getRecipe().getConditions().getByID("world_name");
            hashMap.put("%MODE%", condition.getOption().getDisplayString(CustomCrafting.getApi()));
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
