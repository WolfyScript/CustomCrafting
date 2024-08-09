/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.CustomRecipeAnvil;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RecipeCreatorAnvil extends RecipeCreator {

    public static final String MODE = "mode";
    public static final String REPAIR_MODE = "repair_mode";
    public static final String REPAIR_APPLY = "repair_apply";
    public static final String BLOCK_REPAIR = "block_repair";
    public static final String BLOCK_RENAME = "block_rename";
    public static final String BLOCK_ENCHANT = "block_enchant";
    public static final String REPAIR_COST = "repair_cost";
    public static final String DURABILITY = "durability";

    public RecipeCreatorAnvil(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "anvil", 54, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        registerButton(new ButtonRecipeIngredient(0));
        registerButton(new ButtonRecipeIngredient(1));
        registerButton(new ButtonRecipeResult());
        var btnB = getButtonBuilder();
        btnB.action(MODE).state(s -> s.icon(Material.REDSTONE).action((cache, guiHandler, player, inventory, slot, event) -> {
            var mode = cache.getRecipeCreatorCache().getAnvilCache().getMode();
            int id = mode.getId();
            if (id < 2) {
                id++;
            } else {
                id = 0;
            }
            cache.getRecipeCreatorCache().getAnvilCache().setMode(CustomRecipeAnvil.Mode.getById(id));
            return true;
        }).render((cache, guiHandler, player, guiInventory, itemStack, slot) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("mode", cache.getRecipeCreatorCache().getAnvilCache().getMode().name())))).register();
        btnB.action(REPAIR_MODE).state(s -> s.icon(Material.GLOWSTONE_DUST).action((cache, guiHandler, player, inventory, slot, event) -> {
            int index = CustomRecipeAnvil.RepairCostMode.getModes().indexOf(cache.getRecipeCreatorCache().getAnvilCache().getRepairCostMode()) + 1;
            cache.getRecipeCreatorCache().getAnvilCache().setRepairCostMode(CustomRecipeAnvil.RepairCostMode.getModes().get(index >= CustomRecipeAnvil.RepairCostMode.getModes().size() ? 0 : index));
            return true;
        }).render((cache, guiHandler, player, guiInventory, itemStack, slot) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("var", cache.getRecipeCreatorCache().getAnvilCache().getRepairCostMode().name())))).register();
        btnB.toggle(REPAIR_APPLY).enabledState(s -> s.subKey("true").icon(Material.GREEN_CONCRETE).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setApplyRepairCost(false);
            return true;
        })).disabledState(s -> s.subKey("false").icon(Material.RED_CONCRETE).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setApplyRepairCost(true);
            return true;
        })).register();
        btnB.toggle(BLOCK_REPAIR).enabledState(s -> s.subKey("true").icon(Material.IRON_SWORD).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setBlockRepair(false);
            return true;
        })).disabledState(s -> s.subKey("false").icon(Material.IRON_SWORD).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setBlockRepair(true);
            return true;
        })).register();
        btnB.toggle(BLOCK_RENAME).enabledState(s -> s.subKey("true").icon(Material.WRITABLE_BOOK).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setBlockRename(false);
            return true;
        })).disabledState(s -> s.subKey("false").icon(Material.WRITABLE_BOOK).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setBlockRename(true);
            return true;
        })).register();
        btnB.toggle(BLOCK_ENCHANT).enabledState(s -> s.subKey("true").icon(Material.ENCHANTING_TABLE).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setBlockEnchant(false);
            return true;
        })).disabledState(s -> s.subKey("false").icon(Material.ENCHANTING_TABLE).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setBlockEnchant(true);
            return true;
        })).register();
        btnB.chatInput(REPAIR_COST).state(s -> s.icon(Material.EXPERIENCE_BOTTLE).render((cache, guiHandler, player, guiInventory, itemStack, slot) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("var", String.valueOf(cache.getRecipeCreatorCache().getAnvilCache().getRepairCost()))))).inputAction((guiHandler, player, msg, args) -> {
            int repairCost;
            try {
                repairCost = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                sendMessage(guiHandler, getCluster().translatedMsgKey("valid_number"));
                return true;
            }
            guiHandler.getCustomCache().getRecipeCreatorCache().getAnvilCache().setRepairCost(repairCost);
            return false;
        }).register();
        btnB.chatInput(DURABILITY).state(s -> s.icon(Material.IRON_SWORD).render((cache, guiHandler, player, guiInventory, itemStack, slot) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("var", String.valueOf(cache.getRecipeCreatorCache().getAnvilCache().getDurability()))))).inputAction((guiHandler, player, msg, args) -> {
            int durability;
            try {
                durability = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sendMessage(guiHandler, getCluster().translatedMsgKey("valid_number"));
                return true;
            }
            guiHandler.getCustomCache().getRecipeCreatorCache().getAnvilCache().setDurability(durability);
            return false;
        }).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCCache cache = event.getGuiHandler().getCustomCache();
        event.setButton(0, BACK);
        var anvilCache = cache.getRecipeCreatorCache().getAnvilCache();
        event.setButton(1, ClusterRecipeCreator.HIDDEN);
        event.setButton(3, ClusterRecipeCreator.CONDITIONS);
        event.setButton(5, ClusterRecipeCreator.PRIORITY);
        event.setButton(7, ClusterRecipeCreator.EXACT_META);
        event.setButton(19, "recipe.ingredient_0");
        event.setButton(21, "recipe.ingredient_1");
        if (anvilCache.getMode().equals(CustomRecipeAnvil.Mode.RESULT)) {
            event.setButton(25, "recipe.result");
        } else if (anvilCache.getMode().equals(CustomRecipeAnvil.Mode.DURABILITY)) {
            event.setButton(25, DURABILITY);
        } else {
            event.setItem(25, new ItemStack(Material.BARRIER));
        }
        event.setButton(23, MODE);
        event.setButton(36, BLOCK_ENCHANT);
        event.setButton(37, BLOCK_RENAME);
        event.setButton(38, BLOCK_REPAIR);
        event.setButton(40, REPAIR_APPLY);
        event.setButton(41, REPAIR_COST);
        event.setButton(42, REPAIR_MODE);

        event.setButton(51, ClusterRecipeCreator.GROUP);
        if (anvilCache.isSaved()) {
            event.setButton(52, ClusterRecipeCreator.SAVE);
        }
        event.setButton(53, ClusterRecipeCreator.SAVE_AS);
    }

}
