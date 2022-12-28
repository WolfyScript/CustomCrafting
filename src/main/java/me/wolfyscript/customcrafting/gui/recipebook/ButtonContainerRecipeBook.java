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
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.GUIHolder;
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeContainer;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

class ButtonContainerRecipeBook extends Button<CCCache> {

    private final Map<GuiHandler<?>, RecipeContainer> containers = new HashMap<>();
    private final Map<GuiHandler<CCCache>, Integer> timings = new HashMap<>();
    private final Map<GuiHandler<CCCache>, Supplier<Boolean>> tasks = new HashMap<>();

    private static final String KEY = "recipe_book.container_";

    ButtonContainerRecipeBook(int slot) {
        super(key(slot), null);
    }

    static String key(int slot) {
        return KEY + slot;
    }

    static NamespacedKey namespacedKey(int slot) {
        return new BukkitNamespacedKey(ClusterRecipeBook.KEY, key(slot));
    }

    public static void resetButtons(GuiHandler<CCCache> guiHandler) {
        for (int i = 0; i < 54; i++) {
            if (guiHandler.getInvAPI().getButton(namespacedKey(i)) instanceof ButtonContainerRecipeBook button) {
                button.removeTask(guiHandler);
                button.setTiming(guiHandler, 0);
            }
        }
    }

    @Override
    public void init(GuiWindow guiWindow) {
    }

    @Override
    public void init(GuiCluster<CCCache> guiCluster) {

    }

    @Override
    public ButtonInteractionResult execute(GUIHolder<CCCache> holder, int slot) {
        final var guiHandler = holder.getGuiHandler();
        var cache = guiHandler.getCustomCache();
        var book = cache.getRecipeBookCache();
        var customItem = new CustomItem(CustomCrafting.inst().getApi(), Material.AIR);
        List<CustomRecipe<?>> recipes = getRecipeContainer(guiHandler).getRecipes(holder.getPlayer());
        if (!recipes.isEmpty()) {
            book.setSubFolderPage(0);
            book.addResearchItem(customItem);
            book.setSubFolderRecipes(customItem, recipes);
            book.setPrepareRecipe(true);
            resetButtons(guiHandler);
        }
        guiHandler.openWindow(ClusterRecipeBook.RECIPE_BOOK);
        return ButtonInteractionResult.cancel(true);
    }

    @Override
    public void postExecute(GUIHolder<CCCache> holder, ItemStack itemStack, int slot) throws IOException {

    }

    @Override
    public void preRender(GUIHolder<CCCache> holder, ItemStack itemStack, int slot) {

    }

    @Override
    public void render(GUIHolder<CCCache> holder, Inventory queueInventory, int slot) {
        final var guiHandler = holder.getGuiHandler();
        final List<ItemStack> displayItems = getRecipeContainer(guiHandler).getDisplayItems(holder.getPlayer());
        final int timing = getTiming(guiHandler);
        queueInventory.setItem(slot, timing < displayItems.size() ? displayItems.get(timing) : new ItemStack(Material.STONE));
        if (displayItems.size() > 1) {
            //Only use tasks if there are multiple display items
            final var bookCache = guiHandler.getCustomCache().getRecipeBookCache();
            final int openedPage = bookCache.getPage();
            final var currentFilter = bookCache.getCategoryFilter();
            synchronized (tasks) {
                tasks.computeIfAbsent(guiHandler, thatGuiHandler ->
                        () -> {
                            var newBookCache = thatGuiHandler.getCustomCache().getRecipeBookCache();
                            if (slot < queueInventory.getSize() && !displayItems.isEmpty() && openedPage == newBookCache.getPage() && currentFilter.map(filter -> newBookCache.getCategoryFilter().map(filter::equals).orElse(false)).orElse(true)) {
                                int variant = getTiming(thatGuiHandler);
                                variant = variant < displayItems.size() - 1 ? ++variant : 0;
                                queueInventory.setItem(slot, displayItems.get(variant));
                                setTiming(thatGuiHandler, variant);
                                return false;
                            }
                            //Remove the task if it is no longer valid
                            return true;
                        }
                );
            }
        }
    }

    public RecipeContainer getRecipeContainer(GuiHandler<CCCache> guiHandler) {
        return containers.getOrDefault(guiHandler, null);
    }

    public void setRecipeContainer(GuiHandler<CCCache> guiHandler, RecipeContainer item) {
        containers.put(guiHandler, item);
    }

    public void setTiming(GuiHandler<CCCache> guiHandler, int timing) {
        timings.put(guiHandler, timing);
    }

    public int getTiming(GuiHandler<CCCache> guiHandler) {
        return timings.getOrDefault(guiHandler, 0);
    }

    public void removeTask(GuiHandler<CCCache> guiHandler) {
        synchronized (tasks) {
            tasks.remove(guiHandler);
        }
    }

    public Collection<Supplier<Boolean>> getTasks() {
        synchronized (tasks) {
            return tasks.values();
        }
    }
}
