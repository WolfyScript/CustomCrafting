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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeContainer;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ButtonContainerRecipeBook extends Button<CCCache> {

    private final Map<GuiHandler<?>, RecipeContainer> containers = new HashMap<>();
    private final Map<GuiHandler<CCCache>, Integer> timings = new HashMap<>();
    private final Map<GuiHandler<CCCache>, Runnable> tasks = new HashMap<>();

    private static final String KEY = "recipe_book.container_";

    ButtonContainerRecipeBook(int slot) {
        super(key(slot), null);
    }

    static String key(int slot) {
        return KEY + slot;
    }

    static NamespacedKey namespacedKey(int slot) {
        return new NamespacedKey(ClusterRecipeBook.KEY, key(slot));
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
    public void postExecute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException {

    }

    @Override
    public void preRender(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, boolean help) {

    }

    @Override
    public boolean execute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, int slot, InventoryInteractEvent event) {
        var cache = guiHandler.getCustomCache();
        var book = cache.getKnowledgeBook();
        var customItem = new CustomItem(Material.AIR);
        List<CustomRecipe<?>> recipes = getRecipeContainer(guiHandler).getRecipes(player);
        if (!recipes.isEmpty()) {
            book.setSubFolderPage(0);
            book.addResearchItem(customItem);
            book.setSubFolderRecipes(customItem, recipes);
            book.applyRecipeToButtons(guiHandler, recipes.get(0));
            resetButtons(guiHandler);
        }
        return true;
    }

    @Override
    public void render(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        List<ItemStack> itemStacks = getRecipeContainer(guiHandler).getDisplayItems(player);
        int timing = getTiming(guiHandler);
        inventory.setItem(slot, timing < itemStacks.size() ? itemStacks.get(getTiming(guiHandler)) : new ItemStack(Material.STONE));
        final int openedPage = guiHandler.getCustomCache().getKnowledgeBook().getPage();
        if (itemStacks.size() > 1) {
            Bukkit.getScheduler().runTaskLater(CustomCrafting.inst(), () -> {
                if (guiHandler.getCustomCache().getKnowledgeBook().getSubFolder() == 0 && openedPage == guiHandler.getCustomCache().getKnowledgeBook().getPage()) {
                    synchronized (tasks) {
                        tasks.computeIfAbsent(guiHandler, ccCacheGuiHandler -> () -> {
                            if (slot < inventory.getSize() && !itemStacks.isEmpty()) {
                                int variant = getTiming(guiHandler);
                                variant = ++variant < itemStacks.size() ? variant : 0;
                                guiInventory.setItem(slot, itemStacks.get(variant));
                                setTiming(guiHandler, variant);
                            }
                        });
                    }
                }
            }, 20);

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

    public Collection<Runnable> getTasks() {
        synchronized (tasks) {
            return tasks.values();
        }
    }
}
