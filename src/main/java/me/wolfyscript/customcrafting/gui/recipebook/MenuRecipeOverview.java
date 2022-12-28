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

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.TagResolverUtil;
import com.wolfyscript.utilities.bukkit.gui.GUIHolder;
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.nms.api.inventory.GUIInventory;
import com.wolfyscript.utilities.bukkit.nms.inventory.InventoryUpdate;
import com.wolfyscript.utilities.bukkit.world.inventory.PlayerHeadUtils;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import java.util.List;
import java.util.function.Supplier;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

public class MenuRecipeOverview extends CCWindow {

    private static final String BACK = "back";
    private static final String NEXT_RECIPE = "next_recipe";
    private static final String PREVIOUS_RECIPE = "previous_recipe";
    private final BukkitTask ingredientTask;
    private final BukkitTask containerTask;

    MenuRecipeOverview(ClusterRecipeBook cluster, CustomCrafting customCrafting) {
        super(cluster, ClusterRecipeBook.RECIPE_BOOK.getKey(), 54, customCrafting);
        this.ingredientTask = Bukkit.getScheduler().runTaskTimerAsynchronously(customCrafting, () -> {
            for (int i = 0; i < 37; i++) {
                Button<CCCache> btn = cluster.getButton("ingredient.container_" + i);
                if (btn instanceof ButtonContainerIngredient cBtn) {
                    Bukkit.getScheduler().runTask(customCrafting, () -> cBtn.getTasks().removeIf(Supplier::get));
                }
            }
        }, 1, 25);
        this.containerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(customCrafting, () -> {
            for (int i = 0; i < 45; i++) {
                Button<CCCache> mainContainerBtn = cluster.getButton("recipe_book.container_" + i);
                if (mainContainerBtn instanceof ButtonContainerRecipeBook cBtn) {
                    Bukkit.getScheduler().runTask(customCrafting, () -> cBtn.getTasks().removeIf(Supplier::get));
                }
            }
        }, 1, 25);
    }

    public void reset() {
        this.containerTask.cancel();
        this.ingredientTask.cancel();
    }

    @Override
    public Component onUpdateTitle(GUIHolder<CCCache> holder) {

        var recipeBookCache = holder.getGuiHandler().getCustomCache().getRecipeBookCache();
        if (recipeBookCache.getSubFolder() > 0) {
            List<CustomRecipe<?>> recipes = recipeBookCache.getSubFolderRecipes();
            int maxPages = recipes.size();
            if (recipeBookCache.getSubFolderPage() >= maxPages) {
                recipeBookCache.setSubFolderPage(0);
            }
            if (recipeBookCache.getSubFolderPage() < recipes.size()) {
                CustomRecipe<?> customRecipe = recipes.get(recipeBookCache.getSubFolderPage());
                final TagResolver papiResolver = TagResolverUtil.papi(holder.getPlayer());
                final TagResolver langResolver = TagResolver.resolver("translate", (args, context) -> {
                    String text = args.popOr("The <translate> tag requires exactly one argument! The path to the language entry!").value();
                    return Tag.selfClosingInserting(getChat().translated(text, papiResolver));
                });
                String text = customCrafting.getConfigHandler().getConfig().getRecipeBookTypeName(customRecipe.getRecipeType());
                TagResolver recipeTypeTitle = Placeholder.component("recipe_type_title", getChat().getMiniMessage().deserialize(text, papiResolver, langResolver));
                return wolfyUtilities.getLanguageAPI().getComponent("inventories." + getNamespacedKey().getNamespace() + "." + getNamespacedKey().getKey() + ".gui_name", recipeTypeTitle, TagResolverUtil.papi(holder.getPlayer()));
            }
        }
        return super.onUpdateTitle(holder);
    }

    @Override
    public void onInit() {
        getButtonBuilder().action(BACK).state(state -> state.key(ClusterMain.BACK_BOTTOM).icon(Material.BARRIER).action((holder, cache, btn, slot, details) -> {
            ButtonContainerIngredient.resetButtons(holder.getGuiHandler());
            ButtonContainerRecipeBook.resetButtons(holder.getGuiHandler());
            holder.getGuiHandler().openPreviousWindow();
            return ButtonInteractionResult.cancel(true);
        })).register();
        getButtonBuilder().action(NEXT_RECIPE).state(state -> state.icon(PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287")).action((holder, cache, btn, slot, details) -> {
            var book = cache.getRecipeBookCache();
            ButtonContainerIngredient.resetButtons(holder.getGuiHandler());
            int nextPage = book.getSubFolderPage() + 1;
            if (nextPage < book.getSubFolderRecipes().size()) {
                book.setSubFolderPage(nextPage);
                book.setPrepareRecipe(true);
            }
            return ButtonInteractionResult.cancel(true);
        }).render((holder, cache, btn, slot, itemStack) -> {
            var book = holder.getGuiHandler().getCustomCache().getRecipeBookCache();
            return CallbackButtonRender.Result.of(Placeholder.unparsed("page", String.valueOf(book.getSubFolderPage() + 1)), Placeholder.unparsed("max_pages", String.valueOf(book.getSubFolderRecipes().size())));
        })).register();
        getButtonBuilder().action(PREVIOUS_RECIPE).state(state -> state.icon(PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d")).action((holder, cache, btn, slot, details) -> {
            var book = cache.getRecipeBookCache();
            ButtonContainerIngredient.resetButtons(holder.getGuiHandler());
            if (book.getSubFolderPage() > 0) {
                book.setSubFolderPage(book.getSubFolderPage() - 1);
                book.setPrepareRecipe(true);
            }
            return ButtonInteractionResult.cancel(true);
        }).render((holder, cache, btn, slot, itemStack) -> {
            var book = holder.getGuiHandler().getCustomCache().getRecipeBookCache();
            return CallbackButtonRender.Result.of(Placeholder.unparsed("page", String.valueOf(book.getSubFolderPage() + 1)), Placeholder.unparsed("max_pages", String.valueOf(book.getSubFolderRecipes().size())));
        })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        var player = event.getPlayer();
        CCPlayerData playerStore = PlayerUtil.getStore(player);
        NamespacedKey grayBtnKey = playerStore.getLightBackground();
        var recipeBookCache = event.getGuiHandler().getCustomCache().getRecipeBookCache();
        if (recipeBookCache.getSubFolder() > 0) {
            if (customCrafting.getConfigHandler().getConfig().isGUIDrawBackground()) {
                for (int i = 1; i < 9; i++) {
                    event.setButton(i, grayBtnKey);
                }
                for (int i = 36; i < 45; i++) {
                    event.setButton(i, grayBtnKey);
                }
            }
            List<CustomRecipe<?>> recipes = recipeBookCache.getSubFolderRecipes();
            int maxPages = recipes.size();
            if (recipeBookCache.getSubFolderPage() >= maxPages) {
                recipeBookCache.setSubFolderPage(0);
            }
            if (recipeBookCache.getSubFolderPage() < recipes.size()) {
                CustomRecipe<?> customRecipe = recipes.get(recipeBookCache.getSubFolderPage());
                if (recipeBookCache.isPrepareRecipe()) { //This makes sure we only prepare the recipe once
                    //A new prepare can be queued by using book.setPrepareRecipe(true)
                    recipeBookCache.applyRecipeToButtons(event.getGuiHandler(), customRecipe);
                    recipeBookCache.setPrepareRecipe(false);
                    InventoryUpdate.updateInventory(wolfyUtilities.getCore().getWolfyUtils().getPlugin(), player, onUpdateTitle(event.getGuiHolder()));
                }
                customRecipe.renderMenu(this, event);
                boolean elite = RecipeType.Container.ELITE_CRAFTING.isInstance(customRecipe);
                if (recipeBookCache.getSubFolderPage() > 0) {
                    event.setButton(elite ? 51 : 48, PREVIOUS_RECIPE);
                }
                event.setButton(elite ? 52 : 49, ClusterRecipeBook.BACK_TO_LIST);
                if (recipeBookCache.getSubFolderPage() + 1 < recipes.size()) {
                    event.setButton(elite ? 53 : 50, NEXT_RECIPE);
                }
            }
        }

    }

    @Override
    public boolean onClose(GUIHolder<CCCache> holder) {
        ButtonContainerIngredient.removeTasks(holder.getGuiHandler());
        ButtonContainerRecipeBook.resetButtons(holder.getGuiHandler());
        holder.getGuiHandler().getCustomCache().getRecipeBookCache().setEliteCraftingTable(null);
        return super.onClose(holder);
    }
}
