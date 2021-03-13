package me.wolfyscript.customcrafting.gui.recipe_creator.buttons.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.EliteWorkbenchCondition;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class EliteWorkbenchConditionButton extends ActionButton<CCCache> {

    public EliteWorkbenchConditionButton() {
        super("conditions.elite_workbench", new ButtonState<>("elite_workbench", Material.CRAFTING_TABLE, (cache, guiHandler, player, inventory, slot, event) -> {
            GuiWindow<CCCache> window = inventory.getWindow();
            ICustomRecipe<?,?> recipeConfig = cache.getRecipe();
            Conditions conditions = recipeConfig.getConditions();
            if(event instanceof InventoryClickEvent){
                if (((InventoryClickEvent) event).getClick().isRightClick()) {
                    //Change Mode
                    conditions.getEliteCraftingTableCondition().toggleOption();
                    recipeConfig.setConditions(conditions);
                } else if (!((InventoryClickEvent) event).isShiftClick()) {
                    //CONFIGURE ELITE WORKBENCHES
                    window.openChat("elite_workbench", guiHandler, (guiHandler1, player1, s, args) -> {
                        if (args.length > 1) {
                            CustomItem customItem = Registry.CUSTOM_ITEMS.get(new NamespacedKey(args[0], args[1]));
                            if (customItem == null) {
                                window.sendMessage(player1, "error");
                                return true;
                            }
                            NamespacedKey namespacedKey = customItem.getNamespacedKey();
                            EliteWorkbenchData data = (EliteWorkbenchData) customItem.getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE);
                            if (!data.isEnabled()) {
                                window.sendMessage(player1, "not_elite_workbench");
                                return true;
                            }
                            EliteWorkbenchCondition condition = conditions.getEliteCraftingTableCondition();
                            if (condition.getEliteWorkbenches().contains(namespacedKey)) {
                                window.sendMessage(player1, "already_existing");
                                return true;
                            }
                            condition.addEliteWorkbenches(namespacedKey);
                            recipeConfig.setConditions(conditions);
                            return false;
                        }
                        window.sendMessage(player1, "no_name");
                        return true;
                    });
                } else {
                    if (conditions.getEliteCraftingTableCondition().getEliteWorkbenches().size() > 0) {
                        conditions.getEliteCraftingTableCondition().getEliteWorkbenches().remove(conditions.getEliteCraftingTableCondition().getEliteWorkbenches().size() - 1);
                        recipeConfig.setConditions(conditions);
                    }
                    return false;
                }
            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, b) -> {
            EliteWorkbenchCondition condition = guiHandler.getCustomCache().getRecipe().getConditions().getEliteCraftingTableCondition();
            hashMap.put("%MODE%", condition.getOption().getDisplayString(CustomCrafting.getApi()));
            for (int i = 0; i < 4; i++) {
                if (i < condition.getEliteWorkbenches().size()) {
                    hashMap.put("%var" + i + "%", condition.getEliteWorkbenches().get(i));
                } else {
                    hashMap.put("%var" + i + "%", "...");
                }
            }
            return itemStack;
        }));
    }
}
