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
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.item_creator.ClusterItemCreator;
import me.wolfyscript.customcrafting.gui.recipebook_editor.ClusterRecipeBookEditor;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public class MenuMain extends CCWindow {

    private static final String CRAFTING = RecipeType.Container.CRAFTING.getCreatorID();
    private static final String ELITE_CRAFTING = RecipeType.Container.ELITE_CRAFTING.getCreatorID();
    private static final String FURNACE = RecipeType.FURNACE.getId();
    private static final String ANVIL = RecipeType.ANVIL.getId();
    private static final String BLAST_FURNACE = RecipeType.BLAST_FURNACE.getId();
    private static final String SMOKER = RecipeType.SMOKER.getId();
    private static final String CAMPFIRE = RecipeType.CAMPFIRE.getId();
    private static final String STONECUTTER = RecipeType.STONECUTTER.getId();
    private static final String GRINDSTONE = RecipeType.GRINDSTONE.getId();
    private static final String BREWING_STAND = RecipeType.BREWING_STAND.getId();
    private static final String CAULDRON = RecipeType.CAULDRON.getId();
    private static final String SMITHING = RecipeType.SMITHING.getId();

    private static final String SETTINGS = "settings";

    private static final String RECIPE_BOOK_EDITOR = "recipe_book_editor";
    private static final String ITEM_EDITOR = "item_editor";

    MenuMain(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "main_menu", 54, customCrafting);
    }

    @Override
    public void onInit() {
        var builder = getButtonBuilder();
        registerButton(new ButtonRecipeType(CRAFTING, RecipeType.CRAFTING_SHAPED, Material.CRAFTING_TABLE));
        registerButton(new ButtonRecipeType(FURNACE, RecipeType.FURNACE, Material.FURNACE));
        registerButton(new ButtonRecipeType(ANVIL, RecipeType.ANVIL, Material.ANVIL));
        registerButton(new ButtonRecipeType(BLAST_FURNACE, RecipeType.BLAST_FURNACE, Material.BLAST_FURNACE));
        registerButton(new ButtonRecipeType(SMOKER, RecipeType.SMOKER, Material.SMOKER));
        registerButton(new ButtonRecipeType(CAMPFIRE, RecipeType.CAMPFIRE, Material.CAMPFIRE));
        registerButton(new ButtonRecipeType(STONECUTTER, RecipeType.STONECUTTER, Material.STONECUTTER));
        registerButton(new ButtonRecipeType(GRINDSTONE, RecipeType.GRINDSTONE, Material.GRINDSTONE));
        registerButton(new ButtonRecipeType(BREWING_STAND, RecipeType.BREWING_STAND, Material.BREWING_STAND));
        registerButton(new ButtonRecipeType(ELITE_CRAFTING, RecipeType.ELITE_CRAFTING_SHAPED, new ItemBuilder(Material.CRAFTING_TABLE).addItemFlags(ItemFlag.HIDE_ENCHANTS).addUnsafeEnchantment(Enchantment.DURABILITY, 0).create()));
        registerButton(new ButtonRecipeType(CAULDRON, RecipeType.CAULDRON, Material.CAULDRON));
        registerButton(new ButtonRecipeType(SMITHING, RecipeType.SMITHING, Material.SMITHING_TABLE));

        builder.action(ITEM_EDITOR).state(state -> state.icon(Material.CHEST).action((cache, guiHandler, player, guiInventory, i, event) -> {
            cache.setSetting(Setting.ITEMS);
            cache.getItems().setRecipeItem(false);
            cache.getItems().setSaved(false);
            cache.getItems().setNamespacedKey(null);
            guiHandler.openCluster(ClusterItemCreator.KEY);
            return true;
        })).register();
        builder.action(SETTINGS).state(state -> state.icon(PlayerHeadUtils.getViaURL("b3f293ebd0911bb8133e75802890997e82854915df5d88f115de1deba628164")).action((cache, guiHandler, player, guiInventory, i, event) -> {
            guiHandler.openWindow(SETTINGS);
            return true;
        })).register();
        builder.action(RECIPE_BOOK_EDITOR).state(state -> state.icon(Material.KNOWLEDGE_BOOK).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            guiHandler.openCluster(ClusterRecipeBookEditor.KEY);
            return true;
        })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        event.setButton(0, SETTINGS);
        event.setButton(8, ClusterMain.GUI_HELP);

        event.setButton(4, ClusterMain.PATREON);
        event.setButton(48, ClusterMain.GITHUB);
        event.setButton(49, ClusterMain.YOUTUBE);
        event.setButton(50, ClusterMain.DISCORD);

        event.setButton(10, CRAFTING);
        event.setButton(12, FURNACE);
        event.setButton(14, ANVIL);
        event.setButton(16, CAULDRON);

        event.setButton(19, BLAST_FURNACE);
        event.setButton(21, SMOKER);
        event.setButton(23, CAMPFIRE);
        event.setButton(25, STONECUTTER);
        if (customCrafting.getConfigHandler().getConfig().isBrewingRecipes()) {
            event.setButton(28, GRINDSTONE);
            event.setButton(30, BREWING_STAND);
            event.setButton(32, ELITE_CRAFTING);
            event.setButton(34, SMITHING);
        } else {
            event.setButton(29, GRINDSTONE);
            event.setButton(31, ELITE_CRAFTING);
            event.setButton(33, SMITHING);
        }
        for (int i = 37; i < 44; i++) {
            event.setButton(i, data.getLightBackground());
        }
        event.setButton(36, ITEM_EDITOR);
        event.setButton(44, ClusterMain.RECIPE_LIST);
        event.setButton(45, ClusterMain.ITEM_LIST);
        event.setButton(53, RECIPE_BOOK_EDITOR);
    }
}