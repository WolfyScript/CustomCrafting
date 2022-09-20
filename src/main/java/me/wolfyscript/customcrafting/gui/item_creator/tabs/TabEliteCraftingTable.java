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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.MultipleChoiceButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.util.version.ServerVersion;
import me.wolfyscript.utilities.util.version.WUVersion;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabEliteCraftingTable extends ItemCreatorTab {

    public static final String KEY = "elite_workbench";

    public TabEliteCraftingTable() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.CRAFTING_TABLE, this));
        creator.registerButton(new DummyButton<>("elite_workbench.particles", Material.FIREWORK_ROCKET));
        if (ServerVersion.getWUVersion().isAfterOrEq(WUVersion.of(4, 16, 6, 1))) {
            new MultipleChoiceButton.Builder<>(creator, "elite_workbench.grid_size")
                    .stateFunction((cache, guiHandler, player, guiInventory, i) -> ((EliteWorkbenchData) cache.getItems().getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).getGridSize() - 2)
                    .addState(state -> state.subKey("size_2").icon(PlayerHeadUtils.getViaURL("9e95293acbcd4f55faf5947bfc5135038b275a7ab81087341b9ec6e453e839")).action((ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                        ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).setGridSize(3);
                        return true;
                    }))
                    .addState(state -> state.subKey("size_3").icon(PlayerHeadUtils.getViaURL("9e95293acbcd4f55faf5947bfc5135038b275a7ab81087341b9ec6e453e839")).action((ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                        ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).setGridSize(4);
                        return true;
                    }))
                    .addState(state -> state.subKey("size_4").icon(PlayerHeadUtils.getViaURL("cbfb41f866e7e8e593659986c9d6e88cd37677b3f7bd44253e5871e66d1d424")).action((ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                        ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).setGridSize(5);
                        return true;
                    }))
                    // Deprecated states
                    .addState(state -> state.subKey("size_5").icon(PlayerHeadUtils.getViaURL("14d844fee24d5f27ddb669438528d83b684d901b75a6889fe7488dfc4cf7a1c")).action((ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                        ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).setGridSize(6);
                        return true;
                    }))
                    .addState(state -> state.subKey("size_6").icon(PlayerHeadUtils.getViaURL("faff2eb498e5c6a04484f0c9f785b448479ab213df95ec91176a308a12add70")).action((ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                        ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).setGridSize(2);
                        return true;
                    })).register();
        } else {
            creator.registerButton(new MultipleChoiceButton<>("elite_workbench.grid_size", (cache, guiHandler, player, guiInventory, i) -> ((EliteWorkbenchData) cache.getItems().getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).getGridSize() - 3,
                    new ButtonState<>("elite_workbench.grid_size.size_3", PlayerHeadUtils.getViaURL("9e95293acbcd4f55faf5947bfc5135038b275a7ab81087341b9ec6e453e839"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                        ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).setGridSize(4);
                        return true;
                    }),
                    new ButtonState<>("elite_workbench.grid_size.size_4", PlayerHeadUtils.getViaURL("cbfb41f866e7e8e593659986c9d6e88cd37677b3f7bd44253e5871e66d1d424"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                        ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).setGridSize(5);
                        return true;
                    }),
                    new ButtonState<>("elite_workbench.grid_size.size_5", PlayerHeadUtils.getViaURL("14d844fee24d5f27ddb669438528d83b684d901b75a6889fe7488dfc4cf7a1c"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                        ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).setGridSize(6);
                        return true;
                    }),
                    new ButtonState<>("elite_workbench.grid_size.size_6", PlayerHeadUtils.getViaURL("faff2eb498e5c6a04484f0c9f785b448479ab213df95ec91176a308a12add70"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                        ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).setGridSize(3);
                        return true;
                    })));
        }
        creator.registerButton(new ToggleButton<>("elite_workbench.toggle", (cache, guiHandler, player, guiInventory, i) -> ((EliteWorkbenchData) cache.getItems().getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).isEnabled(), new ButtonState<>("elite_workbench.toggle.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).setEnabled(false);
            return true;
        }), new ButtonState<>("elite_workbench.toggle.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).setEnabled(true);
            return true;
        })));
        creator.registerButton(new ToggleButton<>("elite_workbench.advanced_recipes", (cache, guiHandler, player, guiInventory, i) -> ((EliteWorkbenchData) cache.getItems().getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).isAdvancedRecipes(), new ButtonState<>("elite_workbench.advanced_recipes.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).setAdvancedRecipes(false);
            return true;
        }), new ButtonState<>("elite_workbench.advanced_recipes.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA)).setAdvancedRecipes(true);
            return true;
        })));
    }

    @Override
    public boolean shouldRender(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        return item.getType().isBlock();
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(28, "elite_workbench.particles");
        update.setButton(30, "elite_workbench.grid_size");
        update.setButton(32, "elite_workbench.toggle");
        update.setButton(34, "elite_workbench.advanced_recipes");
    }
}
