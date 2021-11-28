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

package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.gui.potion_creator.ClusterPotionCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class TabPotion extends ItemCreatorTabVanilla {

    public static final String KEY = "potion";

    public TabPotion() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.POTION, this));
        creator.registerButton(new ActionButton<>("potion.add", PlayerHeadUtils.getViaURL("9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            cache.getPotionEffectCache().setApplyPotionEffect((potionEffectCache1, cache1, potionEffect) -> {
                var itemMeta = items.getItem().getItemMeta();
                if (itemMeta instanceof PotionMeta) {
                    ((PotionMeta) itemMeta).addCustomEffect(potionEffect, true);
                }
                items.getItem().setItemMeta(itemMeta);
            });
            cache.getPotionEffectCache().setRecipePotionEffect(false);
            guiHandler.openWindow(ClusterPotionCreator.POTION_CREATOR);
            return true;
        }));
        creator.registerButton(new ActionButton<>("potion.remove", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffectType((cache1, type) -> {
                var itemMeta = items.getItem().getItemMeta();
                if (itemMeta instanceof PotionMeta) {
                    ((PotionMeta) itemMeta).removeCustomEffect(type);
                }
                items.getItem().setItemMeta(itemMeta);
            });
            potionEffectCache.setOpenedFrom("item_creator", "main_menu");
            guiHandler.openWindow(ClusterPotionCreator.POTION_EFFECT_TYPE_SELECTION);
            return true;
        }));
    }

    @Override
    public boolean shouldRender(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        return super.shouldRender(update, cache, items, customItem, item) && items.getItem() != null && item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta;
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "potion.add");
        update.setButton(31, "potion_beta.add");
        update.setButton(32, "potion.remove");
    }
}
