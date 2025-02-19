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
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.lib.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.Tag;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.util.reflection.InventoryUpdate;
import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class MenuRecipeOverview extends CCWindow {

    private static final String BACK = "back";
    private static final String NEXT_RECIPE = "next_recipe";
    private static final String PREVIOUS_RECIPE = "previous_recipe";
    private final BukkitTask ingredientTask;

    MenuRecipeOverview(ClusterRecipeBook cluster, CustomCrafting customCrafting) {
        super(cluster, ClusterRecipeBook.RECIPE_OVERVIEW.getKey(), 54, customCrafting);
        this.ingredientTask = Bukkit.getScheduler().runTaskTimerAsynchronously(customCrafting, () -> {
            for (int i = 0; i < 37; i++) {
                Button<CCCache> btn = cluster.getButton("ingredient.container_" + i);
                if (btn instanceof ButtonContainerIngredient cBtn) {
                    Bukkit.getScheduler().runTask(customCrafting, () -> cBtn.getTasks().removeIf(Supplier::get));
                }
            }
        }, 1, customCrafting.getConfigHandler().getRecipeBookConfig().getVariationCycle().getPeriodIngredient());
    }

    public void reset() {
        this.ingredientTask.cancel();
    }

    @Override
    public Component onUpdateTitle(Player player, @Nullable GUIInventory<CCCache> inventory, GuiHandler<CCCache> guiHandler) {
        var recipeBookCache = guiHandler.getCustomCache().getRecipeBookCache();
        if (recipeBookCache.getSubFolder() > 0) {
            List<CustomRecipe<?>> recipes = recipeBookCache.getSubFolderRecipes();
            int maxPages = recipes.size();
            if (recipeBookCache.getSubFolderPage() >= maxPages) {
                recipeBookCache.setSubFolderPage(0);
            }
            if (recipeBookCache.getSubFolderPage() < recipes.size()) {
                CustomRecipe<?> customRecipe = recipes.get(recipeBookCache.getSubFolderPage());
                final TagResolver papiResolver = TagResolverUtil.papi(player);
                final TagResolver langResolver = TagResolver.resolver("translate", (args, context) -> {
                    String text = args.popOr("The <translate> tag requires exactly one argument! The path to the language entry!").value();
                    return Tag.selfClosingInserting(getChat().translated(text, papiResolver));
                });
                String text = customCrafting.getConfigHandler().getConfig().getRecipeBookTypeName(customRecipe.getRecipeType());
                TagResolver recipeTypeTitle = Placeholder.component("recipe_type_title", getChat().getMiniMessage().deserialize(text, papiResolver, langResolver));
                return wolfyUtilities.getLanguageAPI().getComponent("inventories." + getNamespacedKey().getNamespace() + "." + getNamespacedKey().getKey() + ".gui_name", recipeTypeTitle, TagResolverUtil.papi(player));
            }
        }
        return super.onUpdateTitle(player, inventory, guiHandler);
    }

    @Override
    public void onInit() {
        getButtonBuilder().action(BACK).state(state -> state.key(ClusterMain.BACK_BOTTOM).icon(Material.BARRIER).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            ButtonContainerIngredient.resetButtons(guiHandler);
            ButtonContainerRecipeBook.resetButtons(guiHandler);
            guiHandler.openPreviousWindow();
            return true;
        })).register();
        getButtonBuilder().action(NEXT_RECIPE).state(state -> state.icon(PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287")).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            var book = cache.getRecipeBookCache();
            ButtonContainerIngredient.resetButtons(guiHandler);
            int nextPage = book.getSubFolderPage() + 1;
            if (nextPage < book.getSubFolderRecipes().size()) {
                book.setSubFolderPage(nextPage);
                book.setPrepareRecipe(true);
                updateTitle(guiHandler, player, guiInventory);
            }
            return true;
        }).render((cache, guiHandler, player, guiInventory, itemStack, i) -> {
            var book = guiHandler.getCustomCache().getRecipeBookCache();
            return CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("page", String.valueOf(book.getSubFolderPage() + 1)), Placeholder.unparsed("max_pages", String.valueOf(book.getSubFolderRecipes().size())));
        })).register();
        getButtonBuilder().action(PREVIOUS_RECIPE).state(state -> state.icon(PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d")).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            var book = cache.getRecipeBookCache();
            ButtonContainerIngredient.resetButtons(guiHandler);
            if (book.getSubFolderPage() > 0) {
                book.setSubFolderPage(book.getSubFolderPage() - 1);
                book.setPrepareRecipe(true);
                updateTitle(guiHandler, player, guiInventory);
            }
            return true;
        }).render((cache, guiHandler, player, guiInventory, itemStack, i) -> {
            var book = guiHandler.getCustomCache().getRecipeBookCache();
            return CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("page", String.valueOf(book.getSubFolderPage() + 1)), Placeholder.unparsed("max_pages", String.valueOf(book.getSubFolderRecipes().size())));
        })).register();
    }

    void updateTitle(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory) {
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
            try {
                player.getOpenInventory().setTitle(BukkitComponentSerializer.legacy().serialize(onUpdateTitle(player, inventory, guiHandler)));
            } catch (IllegalArgumentException exception) {
                // EMPTY! This shouldn't happen, just make sure to catch it.
            }
        } else {
            InventoryUpdate.updateInventory(wolfyUtilities.getCore(), player, onUpdateTitle(player, inventory, guiHandler));
        }
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
    public boolean onClose(GuiHandler<CCCache> guiHandler, GUIInventory<CCCache> guiInventory, InventoryView transaction) {
        ButtonContainerIngredient.removeTasks(guiHandler);
        ButtonContainerRecipeBook.resetButtons(guiHandler);
        guiHandler.getCustomCache().getRecipeBookCache().setEliteCraftingTable(null);
        return super.onClose(guiHandler, guiInventory, transaction);
    }
}
