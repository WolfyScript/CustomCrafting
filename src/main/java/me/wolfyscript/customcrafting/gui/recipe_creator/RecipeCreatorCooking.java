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
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import org.bukkit.Material;

public class RecipeCreatorCooking extends RecipeCreator {

    public static final String XP = "xp";
    public static final String COOKING_TIME = "cooking_time";

    public RecipeCreatorCooking(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "cooking", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        registerButton(new ButtonRecipeIngredient(0));
        registerButton(new ButtonRecipeResult());
        getButtonBuilder().chatInput(XP).state(state -> state.icon(Material.EXPERIENCE_BOTTLE).render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("xp", String.valueOf(cache.getRecipeCreatorCache().getCookingCache().getExp()))))).inputAction((guiHandler, player, s, args) -> {
            float xp;
            try {
                xp = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                getChat().sendMessage(player, getCluster().translatedMsgKey("valid_number"));
                return true;
            }
            guiHandler.getCustomCache().getRecipeCreatorCache().getCookingCache().setExp(xp);
            return false;
        }).register();
        getButtonBuilder().chatInput(COOKING_TIME).state(state -> state.icon(Material.COAL).render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("time", String.valueOf(cache.getRecipeCreatorCache().getCookingCache().getCookingTime()))))).inputAction((guiHandler, player, s, args) -> {
            int time;
            try {
                time = Short.parseShort(args[0]);
            } catch (NumberFormatException e) {
                getChat().sendMessage(player, getCluster().translatedMsgKey("valid_number"));
                return true;
            }
            guiHandler.getCustomCache().getRecipeCreatorCache().getCookingCache().setCookingTime(time);
            return false;
        }).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, BACK);
        CCCache cache = update.getGuiHandler().getCustomCache();

        CCPlayerData data = PlayerUtil.getStore(update.getPlayer());

        update.setButton(1, ClusterRecipeCreator.VANILLA_BOOK);
        update.setButton(3, ClusterRecipeCreator.HIDDEN);
        update.setButton(5, ClusterRecipeCreator.CONDITIONS);
        update.setButton(7, ClusterRecipeCreator.EXACT_META);
        update.setButton(20, data.getLightBackground());
        update.setButton(11, "recipe.ingredient_0");
        update.setButton(24, "recipe.result");
        update.setButton(10, data.getLightBackground());
        update.setButton(12, data.getLightBackground());
        update.setButton(22, XP);
        update.setButton(29, COOKING_TIME);

        update.setButton(42, ClusterRecipeCreator.GROUP);
        if (cache.getRecipeCreatorCache().getCookingCache().isSaved()) {
            update.setButton(43, ClusterRecipeCreator.SAVE);
        }
        update.setButton(44, ClusterRecipeCreator.SAVE_AS);
    }

}
