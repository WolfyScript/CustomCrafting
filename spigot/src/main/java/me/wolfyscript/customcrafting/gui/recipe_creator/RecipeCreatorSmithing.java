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

package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class RecipeCreatorSmithing extends RecipeCreator {

    private static final String CHANGE_MATERIAL = "change_material";
    private static final String PRESERVE_ENCHANTS = "preserve_enchants";
    private static final String PRESERVE_DAMAGE = "preserve_damage";
    private static final String PRESERVE_TRIM = "preserve_trim";

    public RecipeCreatorSmithing(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "smithing", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        var btnB = getButtonBuilder();
        registerButton(new ButtonRecipeIngredient(0));
        registerButton(new ButtonRecipeIngredient(1));
        registerButton(new ButtonRecipeIngredient(2));
        registerButton(new ButtonRecipeResult());
        btnB.toggle(CHANGE_MATERIAL).enabledState(s -> s.subKey("enabled").icon(Material.PAPER).action((cache, handler, player, inv, i, e) -> {
            cache.getRecipeCreatorCache().getSmithingCache().setOnlyChangeMaterial(false);
            return true;
        })).disabledState(s -> s.subKey("disabled").icon(Material.WRITABLE_BOOK).action((cache, handler, player, inv, i, e) -> {
            cache.getRecipeCreatorCache().getSmithingCache().setOnlyChangeMaterial(true);
            return true;
        })).register();
        btnB.toggle(PRESERVE_ENCHANTS).stateFunction((cache, handler, player, inv, i) -> cache.getRecipeCreatorCache().getSmithingCache().isPreserveEnchants()).enabledState(s -> s.subKey("enabled").icon(Material.ENCHANTED_BOOK).action((cache, handler, player, inv, i, e) -> {
            cache.getRecipeCreatorCache().getSmithingCache().setPreserveEnchants(false);
            return true;
        })).disabledState(s -> s.subKey("disabled").icon(Material.BOOK).action((cache, handler, player, inv, i, e) -> {
            cache.getRecipeCreatorCache().getSmithingCache().setPreserveEnchants(true);
            return true;
        })).register();
        btnB.toggle(PRESERVE_DAMAGE).stateFunction((cache, handler, player, inv, i) -> cache.getRecipeCreatorCache().getSmithingCache().isPreserveDamage()).enabledState(s -> s.subKey("enabled").icon(new ItemBuilder(Material.IRON_SWORD).addUnsafeEnchantment(Enchantment.DURABILITY, 0).create()).action((cache, handler, player, inv, i, e) -> {
            cache.getRecipeCreatorCache().getSmithingCache().setPreserveDamage(false);
            return true;
        })).disabledState(s -> s.subKey("disabled").icon(Material.IRON_SWORD).action((cache, handler, player, inv, i, e) -> {
            cache.getRecipeCreatorCache().getSmithingCache().setPreserveDamage(true);
            return true;
        })).register();
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
            btnB.toggle(PRESERVE_TRIM).stateFunction((cache, handler, player, inv, i) -> cache.getRecipeCreatorCache().getSmithingCache().isPreserveTrim()).enabledState(s -> s.subKey("enabled").icon(new ItemBuilder(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE).addUnsafeEnchantment(Enchantment.DURABILITY, 0).create()).action((cache, handler, player, inv, i, e) -> {
                cache.getRecipeCreatorCache().getSmithingCache().setPreserveTrim(false);
                return true;
            })).disabledState(s -> s.subKey("disabled").icon(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE).action((cache, handler, player, inv, i, e) -> {
                cache.getRecipeCreatorCache().getSmithingCache().setPreserveTrim(true);
                return true;
            })).register();
        }
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCCache cache = event.getGuiHandler().getCustomCache();
        event.setButton(0, BACK);
        var smithingRecipe = cache.getRecipeCreatorCache().getSmithingCache();
        event.setButton(1, ClusterRecipeCreator.HIDDEN);
        event.setButton(3, ClusterRecipeCreator.CONDITIONS);
        event.setButton(5, ClusterRecipeCreator.PRIORITY);
        event.setButton(7, ClusterRecipeCreator.EXACT_META);
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
            event.setButton(19, "recipe.ingredient_0");
            event.setButton(20, "recipe.ingredient_1");
        } else {
            event.setButton(19, "recipe.ingredient_1");
        }
        event.setButton(21, "recipe.ingredient_2");
        event.setButton(25, "recipe.result");

        event.setButton(37, PRESERVE_ENCHANTS);
        event.setButton(38, PRESERVE_DAMAGE);
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
            event.setButton(39, PRESERVE_TRIM);
        }
        event.setButton(40, CHANGE_MATERIAL);

        event.setButton(42, ClusterRecipeCreator.GROUP);
        if (smithingRecipe.isSaved()) {
            event.setButton(43, ClusterRecipeCreator.SAVE);
        }
        event.setButton(44, ClusterRecipeCreator.SAVE_AS);
    }

}
