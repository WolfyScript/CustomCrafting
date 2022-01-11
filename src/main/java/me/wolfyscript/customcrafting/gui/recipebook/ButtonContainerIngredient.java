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
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.items.RecipeItemStack;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonType;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class ButtonContainerIngredient extends Button<CCCache> {

    private static final String KEY = "ingredient.container_";
    private final CustomCrafting plugin;

    public static String key(int slot) {
        return KEY + slot;
    }

    ButtonContainerIngredient(CustomCrafting plugin, int slot) {
        super(key(slot), ButtonType.DUMMY);
        this.plugin = plugin;
    }

    private final Map<GuiHandler<CCCache>, List<CustomItem>> variantsMap = new HashMap<>();
    private final Map<GuiHandler<CCCache>, Integer> timings = new HashMap<>();
    private final Map<GuiHandler<CCCache>, Runnable> tasks = new HashMap<>();

    public static NamespacedKey namespacedKey(int slot) {
        return new NamespacedKey(ClusterRecipeBook.KEY, key(slot));
    }

    public static NamespacedKey key(GuiCluster<CCCache> cluster, int slot) {
        return new NamespacedKey(cluster.getId(), key(slot));
    }

    @Override
    public void init(GuiWindow guiWindow) {
        //NOT NEEDED
    }

    @Override
    public void init(GuiCluster<CCCache> guiCluster) {

    }

    public static void removeTasks(GuiHandler<CCCache> guiHandler) {
        removeTasks(guiHandler, ClusterRecipeBook.KEY);
    }

    public static void removeTasks(GuiHandler<CCCache> guiHandler, String clusterID) {
        GuiCluster<CCCache> cluster = guiHandler.getInvAPI().getGuiCluster(clusterID);
        for (int i = 0; i < 54; i++) {
            if (cluster.getButton(key(i)) instanceof ButtonContainerIngredient button) {
                button.removeTask(guiHandler);
                button.setTiming(guiHandler, 0);
            }
        }
    }

    public static void resetButtons(GuiHandler<CCCache> guiHandler) {
        resetButtons(guiHandler, ClusterRecipeBook.KEY);
    }

    public static void resetButtons(GuiHandler<CCCache> guiHandler, String clusterID) {
        GuiCluster<CCCache> cluster = guiHandler.getInvAPI().getGuiCluster(clusterID);
        for (int i = 0; i < 54; i++) {
            if (cluster.getButton(key(i)) instanceof ButtonContainerIngredient button) {
                button.removeTask(guiHandler);
                button.setTiming(guiHandler, 0);
                button.removeVariants(guiHandler);
            }
        }
    }

    @Override
    public void postExecute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException {

    }

    @Override
    public void preRender(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, boolean help) {

    }

    @Override
    public boolean execute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, int slot, InventoryInteractEvent event) {
        CCCache cache = guiHandler.getCustomCache();
        var book = cache.getKnowledgeBook();
        if (getTiming(guiHandler) < getVariantsMap(guiHandler).size()) {
            var customItem = getVariantsMap(guiHandler).get(getTiming(guiHandler));
            if (!customItem.equals(book.getResearchItem())) {
                List<CustomRecipe<?>> recipes = plugin.getRegistries().getRecipes().getAvailable(customItem.create(), player);
                if (!recipes.isEmpty()) {
                    resetButtons(guiHandler);
                    book.setSubFolderPage(0);
                    book.addResearchItem(customItem);
                    book.setSubFolderRecipes(customItem, recipes);
                    book.applyRecipeToButtons(guiHandler, recipes.get(0));
                }
            }
        }
        return true;
    }

    @Override
    public void render(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        List<CustomItem> variants = getVariantsMap(guiHandler);
        inventory.setItem(slot, variants.isEmpty() ? ItemUtils.AIR : variants.get(getTiming(guiHandler)).create());
        if (variants.size() > 1) {
            final int openPage = guiHandler.getCustomCache().getKnowledgeBook().getSubFolderPage();
            final var openRecipe = guiHandler.getCustomCache().getKnowledgeBook().getCurrentRecipe().getNamespacedKey();
            Bukkit.getScheduler().runTaskLater(CustomCrafting.inst(), () -> {
                var recipeBook = guiHandler.getCustomCache().getKnowledgeBook();
                if (recipeBook.getSubFolder() != 0 && openPage == recipeBook.getSubFolderPage() && openRecipe.equals(recipeBook.getCurrentRecipe().getNamespacedKey())) {
                    synchronized (tasks) {
                        tasks.computeIfAbsent(guiHandler, ccCacheGuiHandler -> () -> {
                            if (player != null && slot < inventory.getSize() && !variants.isEmpty()) {
                                int variant = getTiming(guiHandler);
                                variant = ++variant < variants.size() ? variant : 0;
                                guiInventory.setItem(slot, variants.get(variant).create());
                                setTiming(guiHandler, variant);
                            }
                        });
                    }
                }
            }, 30);
        }
    }

    public void setTiming(GuiHandler<CCCache> guiHandler, int timing) {
        timings.put(guiHandler, timing);
    }

    public int getTiming(GuiHandler<CCCache> guiHandler) {
        return timings.getOrDefault(guiHandler, 0);
    }

    @NotNull
    public List<CustomItem> getVariantsMap(GuiHandler<CCCache> guiHandler) {
        return variantsMap.getOrDefault(guiHandler, new ArrayList<>());
    }

    public void removeVariants(GuiHandler<CCCache> guiHandler) {
        variantsMap.remove(guiHandler);
    }

    public void setVariants(GuiHandler<CCCache> guiHandler, RecipeItemStack recipeItemStack) {
        this.variantsMap.put(guiHandler, recipeItemStack.getChoices(guiHandler.getPlayer()));
    }

    public void setVariants(GuiHandler<CCCache> guiHandler, List<CustomItem> variants) {
        if (variants != null) {
            Iterator<CustomItem> iterator = variants.iterator();
            while (iterator.hasNext()) {
                CustomItem customItem = iterator.next();
                if (!customItem.hasPermission()) {
                    continue;
                }
                if (!guiHandler.getPlayer().hasPermission(customItem.getPermission())) {
                    iterator.remove();
                }
            }
        }
        this.variantsMap.put(guiHandler, variants);
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
