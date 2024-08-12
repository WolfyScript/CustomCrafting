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
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import org.bukkit.Material;

public class RecipeCreatorGrindstone extends RecipeCreator {

    public RecipeCreatorGrindstone(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "grindstone", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        registerButton(new ButtonRecipeIngredient(0));
        registerButton(new ButtonRecipeIngredient(1));
        registerButton(new ButtonRecipeResult());
        getButtonBuilder().dummy("grindstone").state(s -> s.icon(Material.GRINDSTONE)).register();
        getButtonBuilder().chatInput("xp").state(s -> s.icon(Material.EXPERIENCE_BOTTLE).render((cache, guiHandler, player, guiInventory, itemStack, slot) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("xp", String.valueOf(cache.getRecipeCreatorCache().getGrindingCache().getXp()))))).inputAction((guiHandler, player, msg, args) -> {
            int xp;
            try {
                xp = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sendMessage(guiHandler, getCluster().translatedMsgKey("valid_number"));
                return true;
            }
            guiHandler.getCustomCache().getRecipeCreatorCache().getGrindingCache().setXp(xp);
            return false;
        }).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, BACK);
        CCCache cache = update.getGuiHandler().getCustomCache();
        var grindstoneRecipe = cache.getRecipeCreatorCache().getGrindingCache();

        update.setButton(1, ClusterRecipeCreator.HIDDEN);
        update.setButton(3, ClusterRecipeCreator.CONDITIONS);
        update.setButton(5, ClusterRecipeCreator.PRIORITY);
        update.setButton(7, ClusterRecipeCreator.EXACT_META);

        update.setButton(11, "recipe.ingredient_0");
        update.setButton(20, "grindstone");
        update.setButton(29, "recipe.ingredient_1");

        update.setButton(23, "xp");
        update.setButton(25, "recipe.result");

        update.setButton(42, ClusterRecipeCreator.GROUP);
        if (grindstoneRecipe.isSaved()) {
            update.setButton(43, ClusterRecipeCreator.SAVE);
        }
        update.setButton(44, ClusterRecipeCreator.SAVE_AS);

    }

}
