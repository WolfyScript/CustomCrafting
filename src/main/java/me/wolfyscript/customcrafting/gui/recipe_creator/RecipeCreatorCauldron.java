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
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.BiConsumer;

public class RecipeCreatorCauldron extends RecipeCreator {

    public RecipeCreatorCauldron(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "cauldron", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        var btnB = getButtonBuilder();
        btnB.dummy("cauldron").state(s -> s.icon(Material.CAULDRON)).register();
        registerButton(new ButtonRecipeIngredient(0));
        registerButton(new ButtonRecipeResult());

        btnB.itemInput("handItem_container").state(s -> s.icon(Material.AIR).action((cache, guiHandler, player, inventory, slot, event) -> {
            if (event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().equals(ClickType.SHIFT_RIGHT)) {
                Bukkit.getScheduler().runTask(customCrafting, () -> {
                    if (inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR)) {
                        cache.getItems().setItem(true, CustomItem.getReferenceByItemStack(inventory.getItem(slot)));
                        cache.setApplyItem((items, cache1, customItem) -> cache1.getRecipeCreatorCache().getCauldronCache().setHandItem(items.getItem()));
                        guiHandler.openWindow(ClusterRecipeCreator.ITEM_EDITOR);
                    }
                });
                return true;
            }
            return false;
        }).postAction((cache, guiHandler, player, inventory, itemStack, slot, event) -> {
            if (event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().equals(ClickType.SHIFT_RIGHT) && event.getView().getTopInventory().equals(clickEvent.getClickedInventory())) {
                return;
            }
            cache.getRecipeCreatorCache().getCauldronCache().setHandItem(!ItemUtils.isAirOrNull(inventory.getItem(slot)) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR));
        }).render((cache, guiHandler, player, guiInventory, itemStack, slot) -> {
            var customItem = cache.getRecipeCreatorCache().getCauldronCache().getHandItem();
            if (customItem != null) {
                return CallbackButtonRender.UpdateResult.of(customItem.getItemStack());
            }
            return CallbackButtonRender.UpdateResult.of(itemStack);
        })).register();
        btnB.toggle("dropItems").enabledState(s -> s.subKey("enabled").icon(Material.DROPPER).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setDropItems(false);
            return true;
        })).disabledState(s -> s.subKey("disabled").icon(Material.CHEST).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setDropItems(true);
            return true;
        })).register();
        btnB.toggle("fire").enabledState(s -> s.subKey("enabled").icon(Material.FLINT_AND_STEEL).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setNeedsFire(false);
            return true;
        })).disabledState(s -> s.subKey("disabled").icon(Material.FLINT).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setNeedsFire(true);
            return true;
        })).register();
        btnB.toggle("water").enabledState(s -> s.subKey("enabled").icon(Material.WATER_BUCKET).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setNeedsWater(false);
            return true;
        })).disabledState(s -> s.subKey("disabled").icon(Material.BUCKET).action((cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setNeedsWater(true);
            return true;
        })).register();
        btnB.chatInput("xp").state(s -> s.icon(Material.EXPERIENCE_BOTTLE).render((cache, guiHandler, player, guiInventory, itemStack, slot) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("xp", String.valueOf(cache.getRecipeCreatorCache().getCauldronCache().getXp()))))).inputAction((guiHandler, player, msg, args) -> readNumberFromArgs(args[0], guiHandler, (xp, cache) -> cache.getRecipeCreatorCache().getCauldronCache().setXp(xp))).register();
        btnB.chatInput("cookingTime").state(s -> s.icon(Material.CLOCK).render((cache, guiHandler, player, guiInventory, itemStack, slot) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("time", String.valueOf(cache.getRecipeCreatorCache().getCauldronCache().getCookingTime()))))).inputAction((guiHandler, player, msg, args) -> readNumberFromArgs(args[0], guiHandler, (time, cache) -> cache.getRecipeCreatorCache().getCauldronCache().setCookingTime(time))).register();
        btnB.chatInput("waterLevel").state(s -> s.icon(Material.GLASS_BOTTLE).render((cache, guiHandler, player, guiInventory, itemStack, slot) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("level", String.valueOf(cache.getRecipeCreatorCache().getCauldronCache().getWaterLevel()))))).inputAction((guiHandler, player, msg, args) -> readNumberFromArgs(args[0], guiHandler, (waterLvl, cache) -> {
            if (waterLvl > 3) {
                waterLvl = 3;
            }
            cache.getRecipeCreatorCache().getCauldronCache().setWaterLevel(waterLvl);
        })).register();
    }

    private boolean readNumberFromArgs(String arg, GuiHandler<CCCache> guiHandler, BiConsumer<Integer, CCCache> action) {
        int value;
        try {
            value = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            sendMessage(guiHandler, getCluster().translatedMsgKey("valid_number"));
            return true;
        }
        action.accept(value, guiHandler.getCustomCache());
        return false;
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, BACK);
        CCCache cache = update.getGuiHandler().getCustomCache();
        var cauldronRecipe = cache.getRecipeCreatorCache().getCauldronCache();
        ((ToggleButton<CCCache>) getButton("fire")).setState(update.getGuiHandler(), cauldronRecipe.isNeedsFire());
        ((ToggleButton<CCCache>) getButton("water")).setState(update.getGuiHandler(), cauldronRecipe.isNeedsWater());
        ((ToggleButton<CCCache>) getButton("dropItems")).setState(update.getGuiHandler(), cauldronRecipe.isDropItems());

        update.setButton(1, ClusterRecipeCreator.HIDDEN);
        update.setButton(3, ClusterRecipeCreator.CONDITIONS);
        update.setButton(5, ClusterRecipeCreator.PRIORITY);
        update.setButton(7, ClusterRecipeCreator.EXACT_META);
        update.setButton(11, "recipe.ingredient_0");
        update.setButton(13, "cookingTime");

        update.setButton(19, "water");
        update.setButton(20, "cauldron");
        update.setButton(21, "waterLevel");

        update.setButton(23, "xp");
        update.setButton(25, "recipe.result");

        update.setButton(29, "fire");
        update.setButton(34, "dropItems");

        if (!cauldronRecipe.isDropItems()) {
            update.setButton(33, "handItem_container");
        }

        update.setButton(42, ClusterRecipeCreator.GROUP);
        if (cauldronRecipe.isSaved()) {
            update.setButton(43, ClusterRecipeCreator.SAVE);
        }
        update.setButton(44, ClusterRecipeCreator.SAVE_AS);
    }

}
