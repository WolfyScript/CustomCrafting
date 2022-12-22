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

package me.wolfyscript.customcrafting.gui.main_gui;

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import java.util.HashMap;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.recipe_creator.ClusterRecipeCreator;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

class ButtonContainerRecipeList {

    private static final String KEY = "recipe_list.container_";

    /**
     * Creates the key for the specified slot.
     *
     * @param slot The slot to get the id for.
     * @return The slot specific key of this button.
     */
    static String key(int slot) {
        return KEY + slot;
    }

    static void register(GuiMenuComponent.ButtonBuilder<CCCache> buttonBuilder, int slot, CustomCrafting customCrafting) {
        buttonBuilder.action(key(slot)).state(state -> state.action((cache, guiHandler, player, inventory, button, i, event) -> {
            if (!(event instanceof InventoryClickEvent clickEvent)) return true;
            CustomRecipe<?> customRecipe = cache.getRecipeList().getCustomRecipeForButtonInSlot(slot);
            if (clickEvent.isShiftClick() && customRecipe != null) {
                var window = inventory.getWindow();
                if (clickEvent.isLeftClick()) {
                    try {
                        cache.setSetting(Setting.RECIPE_CREATOR);
                        cache.getRecipeCreatorCache().setRecipeType(customRecipe.getRecipeType());
                        cache.getRecipeCreatorCache().loadRecipeIntoCache(customRecipe);
                        Bukkit.getScheduler().runTaskLater(customCrafting, () -> guiHandler.openWindow(new BukkitNamespacedKey(ClusterRecipeCreator.KEY, cache.getRecipeCreatorCache().getRecipeType().getCreatorID())), 1);
                    } catch (IllegalArgumentException ex) {
                        window.sendMessage(guiHandler, window.translatedMsgKey("invalid_recipe", Placeholder.unparsed("recipe_type", cache.getRecipeCreatorCache().getRecipeType().name())));
                    }
                } else {
                    window.sendMessage(guiHandler, window.translatedMsgKey("delete.confirm", Placeholder.unparsed("recipe", customRecipe.getNamespacedKey().toString())));
                    var confirmComp = window.translatedMsgKey("delete.confirmed").style(b -> b.clickEvent(window.getChat().executable(player, true, (wolfyUtilities, player1) -> {
                        guiHandler.openCluster();
                        Bukkit.getScheduler().runTask(customCrafting, () -> customRecipe.delete(player1));
                    })).hoverEvent(window.translatedMsgKey("delete.confirm_hover")));
                    var declineComp = window.translatedMsgKey("delete.declined").style(b -> b.clickEvent(window.getChat().executable(player, true, (wolfyUtilities, player1) -> guiHandler.openCluster())).hoverEvent(window.translatedMsgKey("delete.decline_hover")));
                    window.sendMessage(guiHandler, confirmComp.append(Component.text(" – ")).append(declineComp));
                }
            } else if (customRecipe != null) {
                customCrafting.getDisableRecipesHandler().toggleRecipe(customRecipe);
            } else {
                customCrafting.getDisableRecipesHandler().toggleBukkitRecipe(((Keyed) cache.getRecipeList().getCustomRecipeForButtonInSlot(slot)).getKey());
            }
            return true;
        }).render((cache, guiHandler, player, guiInventory, button, itemStack, i) -> {
            final var bukkitSerializer = BukkitComponentSerializer.legacy();
            final var langAPI = guiHandler.getWolfyUtils().getLanguageAPI();
            CustomRecipe<?> recipe = cache.getRecipeList().getCustomRecipeForButtonInSlot(slot);
            if (recipe != null) {
                var itemB = new ItemBuilder(new ItemStack(recipe.getResult().getItemStack()));
                if (recipe.getResult().isEmpty()) {
                    itemB.setType(Material.STONE).addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).setDisplayName(ChatColor.BOLD + ChatColor.GRAY.toString() + recipe.getNamespacedKey());
                }
                itemB.addLoreLine(bukkitSerializer.serialize(Component.text(recipe.getNamespacedKey().toString(), NamedTextColor.DARK_GRAY)));
                if (recipe.isDisabled()) {
                    itemB.addLoreLine(bukkitSerializer.serialize(langAPI.getComponent("inventories.none.recipe_list.items.lores.disabled")));
                } else {
                    itemB.addLoreLine(bukkitSerializer.serialize(langAPI.getComponent("inventories.none.recipe_list.items.lores.enabled")));
                }
                itemB.addLoreLine("");
                itemB.addLoreLine(bukkitSerializer.serialize(Component.text(recipe.getRecipeType().name(), NamedTextColor.DARK_GRAY)));
                itemB.addLoreLine(bukkitSerializer.serialize(langAPI.getComponent("inventories.none.recipe_list.items.lores.edit")));
                itemB.addLoreLine(bukkitSerializer.serialize(langAPI.getComponent("inventories.none.recipe_list.items.lores.delete")));
                return CallbackButtonRender.UpdateResult.of(itemB.create());
            } else {
                Recipe bukkitRecipe = cache.getRecipeList().getRecipeForButtonInSlot(slot);
                if (bukkitRecipe != null) {
                    ItemBuilder itemB;
                    if (ItemUtils.isAirOrNull(bukkitRecipe.getResult())) {
                        itemB = new ItemBuilder(Material.STONE);
                        itemB.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).setDisplayName(ChatColor.BOLD + ChatColor.GRAY.toString() + ((Keyed) bukkitRecipe).getKey());
                    } else {
                        itemB = new ItemBuilder(bukkitRecipe.getResult());
                    }
                    itemB.addLoreLine(ChatColor.DARK_GRAY.toString() + ((Keyed) bukkitRecipe).getKey());
                    if (customCrafting.getDisableRecipesHandler().isBukkitRecipeDisabled(((Keyed) bukkitRecipe).getKey())) {
                        itemB.addLoreLine(bukkitSerializer.serialize(langAPI.getComponent("inventories.none.recipe_list.items.lores.disabled")));
                    } else {
                        itemB.addLoreLine(bukkitSerializer.serialize(langAPI.getComponent("inventories.none.recipe_list.items.lores.enabled")));
                    }
                    return CallbackButtonRender.UpdateResult.of(itemB.create());
                }
            }
            return CallbackButtonRender.UpdateResult.of();
        })).register();
    }
}
