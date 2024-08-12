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

package me.wolfyscript.customcrafting.gui.recipebook;

import com.wolfyscript.utilities.bukkit.TagResolverUtil;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.Tag;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class MenuSingleRecipe extends CCWindow {

    public static final String KEY = "single_recipe";

    MenuSingleRecipe(GuiCluster<CCCache> guiCluster, CustomCrafting customCrafting) {
        super(guiCluster, KEY, 54, customCrafting);
    }

    @Override
    public void onInit() {

    }
    @Override
    public Component onUpdateTitle(Player player, @Nullable GUIInventory<CCCache> inventory, GuiHandler<CCCache> guiHandler) {
        Optional<CustomRecipe<?>> recipeOptional = guiHandler.getCustomCache().getCacheRecipeView().getRecipe();
        if (recipeOptional.isPresent()) {
            CustomRecipe<?> customRecipe = recipeOptional.get();
            final TagResolver papiResolver = TagResolverUtil.papi(player);
            final TagResolver langResolver = TagResolver.resolver("translate", (args, context) -> {
                String text = args.popOr("The <translate> tag requires exactly one argument! The path to the language entry!").value();
                return Tag.selfClosingInserting(getChat().translated(text, papiResolver));
            });
            String text = customCrafting.getConfigHandler().getConfig().getRecipeBookTypeName(customRecipe.getRecipeType());
            TagResolver recipeTypeTitle = Placeholder.component("recipe_type_title", getChat().getMiniMessage().deserialize(text, papiResolver, langResolver));
            return wolfyUtilities.getLanguageAPI().getComponent("inventories." + getNamespacedKey().getNamespace() + "." + getNamespacedKey().getKey() + ".gui_name", recipeTypeTitle, TagResolverUtil.papi(player));
        }
        return super.onUpdateTitle(player, inventory, guiHandler);
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        Optional<CustomRecipe<?>> recipeOptional = update.getGuiHandler().getCustomCache().getCacheRecipeView().getRecipe();
        if (recipeOptional.isPresent()) {
            CCPlayerData playerStore = PlayerUtil.getStore(update.getPlayer());
            NamespacedKey grayBtnKey = playerStore.getLightBackground();
            for (int i = 1; i < 9; i++) {
                update.setButton(i, grayBtnKey);
            }
            for (int i = 36; i < 45; i++) {
                update.setButton(i, grayBtnKey);
            }
            CustomRecipe<?> customRecipe = recipeOptional.get();
            customRecipe.renderMenu(this, update);
        } else {
            // If there is no recipe available, why is the menu open?
            Bukkit.getScheduler().runTask(customCrafting, () -> update.getGuiHandler().close());
        }
    }

    @Override
    public boolean onClose(GuiHandler<CCCache> guiHandler, GUIInventory<CCCache> guiInventory, InventoryView transaction) {
        ButtonContainerIngredient.removeTasks(guiHandler, ClusterRecipeView.KEY);
        guiHandler.getCustomCache().getCacheRecipeView().setRecipe(null);
        guiHandler.getHistory(getCluster()).remove(0);
        return super.onClose(guiHandler, guiInventory, transaction);
    }
}
