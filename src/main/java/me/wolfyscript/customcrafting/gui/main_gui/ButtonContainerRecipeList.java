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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.recipe_creator.ClusterRecipeCreator;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.lib.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.lib.net.kyori.adventure.text.format.NamedTextColor;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;

class ButtonContainerRecipeList extends Button<CCCache> {

    private static final String KEY = "recipe_list.container_";

    private final WolfyUtilities api;
    private final CustomCrafting customCrafting;
    private final HashMap<GuiHandler<CCCache>, Recipe> recipes = new HashMap<>();
    private final HashMap<GuiHandler<CCCache>, CustomRecipe<?>> customRecipes = new HashMap<>();

    ButtonContainerRecipeList(int slot, CustomCrafting customCrafting) {
        super(key(slot), null);
        this.customCrafting = customCrafting;
        this.api = customCrafting.getApi();
    }

    /**
     * Creates the key for the specified slot.
     *
     * @param slot The slot to get the id for.
     * @return The slot specific key of this button.
     */
    static String key(int slot) {
        return KEY + slot;
    }

    @Override
    public void init(GuiWindow guiWindow) {
        //Not Required
    }

    @Override
    public void init(GuiCluster<CCCache> guiCluster) {
        //Not Required
    }

    @Override
    public void postExecute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) {
        //Not Required
    }

    @Override
    public void preRender(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, boolean help) {
        //Not Required
    }

    @Override
    public boolean execute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, int slot, InventoryInteractEvent event) {
        CCCache cache = guiHandler.getCustomCache();
        if (!(event instanceof InventoryClickEvent clickEvent)) return true;
        CustomRecipe<?> customRecipe = getCustomRecipe(guiHandler);
        if (clickEvent.isShiftClick() && customRecipe != null) {
            var window = inventory.getWindow();
            if (clickEvent.isLeftClick()) {
                try {
                    cache.setSetting(Setting.RECIPE_CREATOR);
                    cache.getRecipeCreatorCache().setRecipeType(customRecipe.getRecipeType());
                    cache.getRecipeCreatorCache().loadRecipeIntoCache(customRecipe);
                    Bukkit.getScheduler().runTaskLater(customCrafting, () -> guiHandler.openWindow(new NamespacedKey(ClusterRecipeCreator.KEY, cache.getRecipeCreatorCache().getRecipeType().getCreatorID())), 1);
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
            customCrafting.getDisableRecipesHandler().toggleBukkitRecipe(((Keyed) getRecipe(guiHandler)).getKey());
        }
        return true;
    }

    @Override
    public void render(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        final var bukkitSerializer = BukkitComponentSerializer.legacy();
        final var langAPI = api.getLanguageAPI();
        if (getCustomRecipe(guiHandler) != null) {
            CustomRecipe<?> recipe = getCustomRecipe(guiHandler);
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
                inventory.setItem(slot, itemB.create());
            }
        } else {
            Recipe recipe = getRecipe(guiHandler);
            if (recipe != null) {
                ItemBuilder itemB;
                if (ItemUtils.isAirOrNull(recipe.getResult())) {
                    itemB = new ItemBuilder(Material.STONE);
                    itemB.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).setDisplayName(ChatColor.BOLD + ChatColor.GRAY.toString() + ((Keyed) recipe).getKey());
                } else {
                    itemB = new ItemBuilder(recipe.getResult());
                }
                itemB.addLoreLine(ChatColor.DARK_GRAY.toString() + ((Keyed) recipe).getKey());
                if (customCrafting.getDisableRecipesHandler().isBukkitRecipeDisabled(((Keyed) recipe).getKey())) {
                    itemB.addLoreLine(bukkitSerializer.serialize(langAPI.getComponent("inventories.none.recipe_list.items.lores.disabled")));
                } else {
                    itemB.addLoreLine(bukkitSerializer.serialize(langAPI.getComponent("inventories.none.recipe_list.items.lores.enabled")));
                }
                inventory.setItem(slot, itemB.create());
            }
        }
    }

    public CustomRecipe<?> getCustomRecipe(GuiHandler<CCCache> guiHandler) {
        return customRecipes.getOrDefault(guiHandler, null);
    }

    public void setCustomRecipe(GuiHandler<CCCache> guiHandler, CustomRecipe<?> recipe) {
        customRecipes.put(guiHandler, recipe);
    }

    public Recipe getRecipe(GuiHandler<CCCache> guiHandler) {
        return recipes.getOrDefault(guiHandler, null);
    }

    public void setRecipe(GuiHandler<CCCache> guiHandler, Recipe recipe) {
        recipes.put(guiHandler, recipe);
    }
}
