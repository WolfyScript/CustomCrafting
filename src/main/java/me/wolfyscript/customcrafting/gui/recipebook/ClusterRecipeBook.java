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
import me.wolfyscript.customcrafting.gui.CCCluster;
import me.wolfyscript.customcrafting.gui.elite_crafting.EliteCraftingCluster;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipeAnvil;
import me.wolfyscript.customcrafting.recipes.CustomRecipeBrewing;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCooking;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.PermissionCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WeatherCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WorldTimeCondition;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;

public class ClusterRecipeBook extends CCCluster {

    public static final String KEY = "recipe_book";

    public static final NamespacedKey MAIN_MENU = new NamespacedKey(KEY, "main_menu");
    public static final NamespacedKey RECIPE_BOOK = new NamespacedKey(KEY, "recipe_book");
    public static final NamespacedKey CATEGORY_OVERVIEW = new NamespacedKey(KEY, "category_overview");

    public static final NamespacedKey BACK_TO_LIST = new NamespacedKey(KEY, "back_to_list");
    public static final NamespacedKey NEXT_PAGE = new NamespacedKey(KEY, "next_page");
    public static final NamespacedKey PREVIOUS_PAGE = new NamespacedKey(KEY, "previous_page");
    public static final NamespacedKey ITEM_CATEGORY = new NamespacedKey(KEY, "item_category");
    public static final NamespacedKey PERMISSION = new NamespacedKey(KEY, "permission");

    public static final NamespacedKey COOKING_ICON = new NamespacedKey(ClusterRecipeBook.KEY, "cooking.icon");
    public static final NamespacedKey STONECUTTER = new NamespacedKey(ClusterRecipeBook.KEY, "stonecutter");
    public static final NamespacedKey FURNACE = new NamespacedKey(ClusterRecipeBook.KEY, "furnace");
    public static final NamespacedKey BLAST_FURNACE = new NamespacedKey(ClusterRecipeBook.KEY, "blast_furnace");
    public static final NamespacedKey CAMPFIRE = new NamespacedKey(ClusterRecipeBook.KEY, "campfire");
    public static final NamespacedKey GRINDSTONE = new NamespacedKey(ClusterRecipeBook.KEY, "grindstone");
    public static final NamespacedKey SMOKER = new NamespacedKey(ClusterRecipeBook.KEY, "smoker");
    public static final NamespacedKey SMITHING = new NamespacedKey(ClusterRecipeBook.KEY, "smithing");

    public static final NamespacedKey CAULDRON_CAMPFIRE = new NamespacedKey(KEY, "cauldron.campfire");
    public static final NamespacedKey CAULDRON_SOUL_CAMPFIRE = new NamespacedKey(KEY, "cauldron.soul_campfire");
    public static final NamespacedKey CAULDRON_SIGNAL_FIRE = new NamespacedKey(KEY, "cauldron.signal_fire");

    public static final NamespacedKey CAULDRON_COOK_WATER = new NamespacedKey(KEY, "cauldron.can_cook_in_water");
    public static final NamespacedKey CAULDRON_COOK_LAVA = new NamespacedKey(KEY, "cauldron.can_cook_in_lava");
    public static final NamespacedKey CAULDRON_EMPTY = new NamespacedKey(KEY, "cauldron.empty_cauldron");

    public ClusterRecipeBook(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new MenuRecipeOverview(this, customCrafting));
        registerGuiWindow(new MenuCategoryOverview(this, customCrafting));
        registerGuiWindow(new MenuMain(this, customCrafting));
        setEntry(MAIN_MENU);
        var btnB = getButtonBuilder();
        registerButton(new ButtonCategoryItem(customCrafting));
        btnB.action(NEXT_PAGE.getKey()).state(s -> s.icon(PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287")).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            ButtonContainerRecipeBook.resetButtons(guiHandler);
            var book = guiHandler.getCustomCache().getRecipeBookCache();
            book.setPage(book.getPage() + 1);
            return true;
        })).register();
        btnB.action(PREVIOUS_PAGE.getKey()).state(s -> s.icon(PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d")).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            ButtonContainerRecipeBook.resetButtons(guiHandler);
            var book = guiHandler.getCustomCache().getRecipeBookCache();
            book.setPage(book.getPage() - 1);
            return true;
        })).register();
        btnB.action(BACK_TO_LIST.getKey()).state(s -> s.icon(Material.BARRIER).action((cache, guiHandler, player, guiInventory, i, event) -> {
            if (event instanceof InventoryClickEvent clickEvent) {
                var book = cache.getRecipeBookCache();
                ButtonContainerIngredient.resetButtons(guiHandler);
                if (clickEvent.getClick().equals(ClickType.MIDDLE)) {
                    Bukkit.getScheduler().runTask(customCrafting, () -> {
                        if (cache.getRecipeBookCache().hasEliteCraftingTable()) {
                            guiHandler.openCluster(EliteCraftingCluster.KEY);
                        } else {
                            guiHandler.close();
                        }
                        cache.getRecipeBookCache().setEliteCraftingTable(null);
                    });
                    return true;
                } else if (clickEvent.isLeftClick()) {
                    book.removePreviousResearchItem();
                    if (book.getSubFolder() > 0) {
                        CustomItem item = book.getResearchItem();
                        if (book.getSubFolderRecipes().isEmpty()) {
                            book.setSubFolderRecipes(item, customCrafting.getRegistries().getRecipes().get(item));
                        }
                        if (!book.getSubFolderRecipes().isEmpty()) {
                            book.setPrepareRecipe(true);
                        }
                        return true;
                    }
                } else {
                    book.setResearchItems(new ArrayList<>());
                }
                guiHandler.openPreviousWindow();
            }
            return true;
        })).register();
        btnB.toggle(PERMISSION.getKey()).enabledState(s -> s.subKey("enabled").icon(Material.GREEN_CONCRETE).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> true)).disabledState(s -> s.subKey("disabled").icon(Material.RED_CONCRETE).action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> true)).register();

        registerRecipeIcons(btnB);
        for (int i = 0; i < 37; i++) {
            registerButton(new ButtonContainerIngredient(customCrafting, i));
        }
        for (int i = 0; i < 45; i++) {
            registerButton(new ButtonContainerRecipeBook(i));
        }
        registerConditionDisplays(btnB);
    }

    private void registerRecipeIcons(ButtonBuilder<CCCache> btnB) {
        btnB.dummy("workbench.shapeless_on").state(s -> s.icon(Material.CRAFTING_TABLE)).register();
        btnB.dummy("workbench.shapeless_off").state(s -> s.icon(Material.CRAFTING_TABLE)).register();
        btnB.dummy("anvil.durability").state(s -> s.icon(Material.ANVIL).render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("var", String.valueOf(((CustomRecipeAnvil) guiHandler.getCustomCache().getRecipeBookCache().getCurrentRecipe()).getDurability()))))).register();
        btnB.dummy("anvil.result").state(s -> s.icon(Material.ANVIL)).register();
        btnB.dummy("anvil.none").state(s -> s.icon(Material.ANVIL)).register();
        btnB.dummy(COOKING_ICON.getKey()).state(s -> s.icon(Material.FURNACE).render((cache, guiHandler, player, guiInventory, itemStack, i) -> {
            var knowledgeBook = cache.getRecipeBookCache();
            RecipeType<?> recipeType = knowledgeBook.getCurrentRecipe().getRecipeType();
            CustomRecipeCooking<?, ?> cookingRecipe = ((CustomRecipeCooking<?, ?>) knowledgeBook.getCurrentRecipe());
            itemStack.setType(Material.matchMaterial(recipeType.name()));
            return CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("type", StringUtils.capitalize(recipeType.getId().replace("_", " "))), Placeholder.unparsed("time", String.valueOf(cookingRecipe.getCookingTime())), Placeholder.unparsed("xp", String.valueOf(cookingRecipe.getExp())));
        })).register();
        btnB.dummy(FURNACE.getKey()).state(s -> s.icon(Material.FURNACE)).register();
        btnB.dummy(STONECUTTER.getKey()).state(s -> s.icon(Material.STONECUTTER)).register();
        btnB.dummy(BLAST_FURNACE.getKey()).state(s -> s.icon(Material.BLAST_FURNACE)).register();
        btnB.dummy(CAMPFIRE.getKey()).state(s -> s.icon(Material.CAMPFIRE)).register();
        btnB.dummy(GRINDSTONE.getKey()).state(s -> s.icon(Material.GRINDSTONE)).register();
        btnB.dummy(SMOKER.getKey()).state(s -> s.icon(Material.SMOKER)).register();
        btnB.dummy(SMITHING.getKey()).state(s -> s.icon(Material.SMITHING_TABLE)).register();

        // Register Cauldron Menu Buttons
        btnB.dummy("cauldron.water.disabled").state(s -> s.icon(Material.CAULDRON)).register();
        btnB.dummy("cauldron.water.enabled").state(s -> s.icon(PlayerHeadUtils.getViaURL("848a19cdf42d748b41b72fb4376ae3f63c1165d2dce0651733df263446c77ba6")).render((cache, guiHandler, player, guiInventory, itemStack, i) -> {
            var knowledgeBook = cache.getRecipeBookCache();
            return CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("time", String.valueOf(((CustomRecipeCauldron) knowledgeBook.getCurrentRecipe()).getCookingTime())), Placeholder.unparsed("lvl", String.valueOf(((CustomRecipeCauldron) knowledgeBook.getCurrentRecipe()).getFluidLevel())));
        })).register();
        btnB.dummy(CAULDRON_CAMPFIRE.getKey()).state(s -> s.icon(Material.CAMPFIRE)).register();
        btnB.dummy(CAULDRON_SOUL_CAMPFIRE.getKey()).state(s -> s.icon(Material.SOUL_CAMPFIRE)).register();
        btnB.dummy(CAULDRON_SIGNAL_FIRE.getKey()).state(s -> s.icon(Material.HAY_BLOCK)).register();

        btnB.dummy(CAULDRON_COOK_WATER.getKey()).state(s -> s.icon(Material.WATER_BUCKET)).register();
        btnB.dummy(CAULDRON_COOK_LAVA.getKey()).state(s -> s.icon(Material.LAVA_BUCKET)).register();
        btnB.dummy(CAULDRON_EMPTY.getKey()).state(s -> s.icon(Material.BUCKET)).register();

        // Register Brewing Menu Buttons
        btnB.dummy("brewing.icon").state(s -> s.icon(Material.BREWING_STAND).render((cache, guiHandler, player, guiInventory, itemStack, i) -> {
            CustomRecipeBrewing cookingRecipe = (CustomRecipeBrewing) (cache.getRecipeBookCache()).getCurrentRecipe();
            return CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("time", String.valueOf(cookingRecipe.getBrewTime())), Placeholder.unparsed("cost", String.valueOf(cookingRecipe.getFuelCost())));
        })).register();
        btnB.dummy("brewing.potion_duration").state(s -> s.icon(Material.CLOCK).render((cache, guiHandler, player, inv, stack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("value", String.valueOf(((CustomRecipeBrewing) (cache.getRecipeBookCache()).getCurrentRecipe()).getDurationChange()))))).register();
        btnB.dummy("brewing.potion_amplifier").state(s -> s.icon(Material.CLOCK).render((cache, guiHandler, player, inv, stack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("value", String.valueOf(((CustomRecipeBrewing) (cache.getRecipeBookCache()).getCurrentRecipe()).getAmplifierChange()))))).register();
    }

    private void registerConditionDisplays(ButtonBuilder<CCCache> btnB) {
        btnB.dummy("conditions.world_time").state(s -> s.icon(Material.CLOCK).render((cache, guiHandler, player, guiInventory, itemStack, i) -> {
            CustomRecipe<?> recipe = (cache.getRecipeBookCache()).getCurrentRecipe();
            var option = recipe.getConditions().getByType(WorldTimeCondition.class).getOption();
            return CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("value", String.valueOf(recipe.getConditions().getByType(WorldTimeCondition.class).getTime())), Placeholder.unparsed("mode", option.equals(Conditions.Option.EXACT) ? "" : option.getDisplayString(wolfyUtilities)));
        })).register();
        btnB.dummy("conditions.weather").state(s -> s.icon(Material.WATER_BUCKET).render((cache, guiHandler, player, inv, stack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("value", (cache.getRecipeBookCache()).getCurrentRecipe().getConditions().getByType(WeatherCondition.class).getWeather().getDisplay(wolfyUtilities))))).register();
        btnB.dummy("conditions.permission").state(s -> s.icon(Material.REDSTONE).render((cache, guiHandler, player, inv, stack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("value", (cache.getRecipeBookCache()).getCurrentRecipe().getConditions().getByType(PermissionCondition.class).getPermission())))).register();
    }
}
