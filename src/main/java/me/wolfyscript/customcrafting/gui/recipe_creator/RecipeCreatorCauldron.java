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

import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonAction;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import java.util.function.BiConsumer;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.recipe_creator.RecipeCacheCauldron;
import me.wolfyscript.customcrafting.recipes.items.Result;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class RecipeCreatorCauldron extends RecipeCreator {

    public RecipeCreatorCauldron(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "cauldron", 54, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        var btnB = getButtonBuilder();
        btnB.dummy("cauldron").state(s -> s.icon(Material.CAULDRON)).register();
        for (int i = 0; i < 6; i++) {
            ButtonRecipeIngredient.register(getButtonBuilder(), i);
        }
        ButtonRecipeResult.register(getButtonBuilder());
        for (int i = 0; i < 3; i++) {
            final int resultSlot = i;
            getButtonBuilder().itemInput("additional_result_" + resultSlot).state(state -> state.icon(Material.AIR)
                    .action((holder, cache, btn, slot, details) -> ButtonInteractionResult.cancel(false))
                    .postAction((holder, cache, btn, slot, itemStack, details) -> {
                        RecipeCacheCauldron cacheCauldron = cache.getRecipeCreatorCache().getCauldronCache();
                        Result result = cacheCauldron.getAdditionalResults()[resultSlot];
                        if ((result.getItems().isEmpty() && !result.getTags().isEmpty()) || event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().equals(ClickType.SHIFT_RIGHT) && event.getView().getTopInventory().equals(clickEvent.getClickedInventory())) {
                            return;
                        }
                        result.put(0, !ItemUtils.isAirOrNull(itemStack) ? CustomItem.getReferenceByItemStack(itemStack) : null);
                        result.buildChoices();
                    })
                    .render((holder, cache, btn, slot, itemStack) -> {
                        Result result = cache.getRecipeCreatorCache().getCauldronCache().getAdditionalResults()[resultSlot];
                        return CallbackButtonRender.Result.of(result == null ? new ItemStack(Material.AIR) : result.getItemStack());
                    })).register();
        }


        btnB.toggle("campfire").enabledState(s -> s.subKey("enabled").icon(Material.CAMPFIRE).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setCampfire(false);
            return ButtonInteractionResult.cancel(true);
        })).disabledState(s -> s.subKey("disabled").icon(Material.CAMPFIRE).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setCampfire(true);
            return ButtonInteractionResult.cancel(true);
        })).stateFunction((holder, cache, slot) -> cache.getRecipeCreatorCache().getCauldronCache().isCampfire()).register();
        btnB.toggle("soul_campfire").enabledState(s -> s.subKey("enabled").icon(Material.SOUL_CAMPFIRE).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setSoulCampfire(false);
            return ButtonInteractionResult.cancel(true);
        })).disabledState(s -> s.subKey("disabled").icon(Material.SOUL_CAMPFIRE).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setSoulCampfire(true);
            return ButtonInteractionResult.cancel(true);
        })).stateFunction((holder, cache, slot) -> cache.getRecipeCreatorCache().getCauldronCache().isSoulCampfire()).register();
        btnB.toggle("signal_fire").enabledState(s -> s.subKey("enabled").icon(Material.HAY_BLOCK).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setSignalFire(false);
            return ButtonInteractionResult.cancel(true);
        })).disabledState(s -> s.subKey("disabled").icon(Material.HAY_BLOCK).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setSignalFire(true);
            return ButtonInteractionResult.cancel(true);
        })).stateFunction((holder, cache, slot) -> cache.getRecipeCreatorCache().getCauldronCache().isSignalFire()).register();

        btnB.toggle("can_cook_in_water").enabledState(s -> s.subKey("enabled").icon(Material.WATER_BUCKET).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setCanCookInWater(false);
            return ButtonInteractionResult.cancel(true);
        })).disabledState(s -> s.subKey("disabled").icon(Material.BUCKET).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setCanCookInWater(true);
            return ButtonInteractionResult.cancel(true);
        })).stateFunction((holder, cache, slot) -> cache.getRecipeCreatorCache().getCauldronCache().isCanCookInWater()).register();
        btnB.toggle("can_cook_in_lava").enabledState(s -> s.subKey("enabled").icon(Material.LAVA_BUCKET).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setCanCookInLava(false);
            return ButtonInteractionResult.cancel(true);
        })).disabledState(s -> s.subKey("disabled").icon(Material.BUCKET).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getCauldronCache().setCanCookInLava(true);
            return ButtonInteractionResult.cancel(true);
        })).stateFunction((holder, cache, slot) -> cache.getRecipeCreatorCache().getCauldronCache().isCanCookInLava()).register();
        btnB.action("fluid_level").state(s -> s.icon(Material.GLASS_BOTTLE)
                .render((holder, cache, btn, slot, itemStack) -> CallbackButtonRender.Result.of(Placeholder.unparsed("level", String.valueOf(cache.getRecipeCreatorCache().getCauldronCache().getFluidLevel()))))
                .action((holder, cache, btn, slot, details) -> {
                    RecipeCacheCauldron cacheCauldron = cache.getRecipeCreatorCache().getCauldronCache();
                    cacheCauldron.setFluidLevel(cacheCauldron.getFluidLevel() >= 3 ? 0 : cacheCauldron.getFluidLevel() + 1);
                    return ButtonInteractionResult.cancel(true);
                })).register();

        btnB.chatInput("xp").state(s -> s.icon(Material.EXPERIENCE_BOTTLE).render((holder, cache, btn, slot, itemStack) -> CallbackButtonRender.Result.of(Placeholder.unparsed("xp", String.valueOf(cache.getRecipeCreatorCache().getCauldronCache().getXp()))))).inputAction((handler, player, msg, args) -> readNumberFromArgs(args[0], handler, (xp, cache) -> cache.getRecipeCreatorCache().getCauldronCache().setXp(xp))).register();
        btnB.chatInput("cookingTime").state(s -> s.icon(Material.CLOCK).render((holder, cache, btn, slot, itemStack) -> CallbackButtonRender.Result.of(Placeholder.unparsed("time", String.valueOf(cache.getRecipeCreatorCache().getCauldronCache().getCookingTime()))))).inputAction((handler, player, msg, args) -> readNumberFromArgs(args[0], handler, (time, cache) -> cache.getRecipeCreatorCache().getCauldronCache().setCookingTime(time))).register();
    }

    private boolean readNumberFromArgs(String arg, GuiHandler<CCCache> handler, BiConsumer<Integer, CCCache> action) {
        int value;
        try {
            value = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            sendMessage(handler, getCluster().translatedMsgKey("valid_number"));
            return true;
        }
        action.accept(value, handler.getCustomCache());
        return false;
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, BACK);
        CCCache cache = update.getGuiHandler().getCustomCache();
        var cauldronRecipe = cache.getRecipeCreatorCache().getCauldronCache();

        update.setButton(1, ClusterRecipeCreator.HIDDEN);
        update.setButton(3, ClusterRecipeCreator.CONDITIONS);
        update.setButton(5, ClusterRecipeCreator.PRIORITY);
        update.setButton(7, ClusterRecipeCreator.EXACT_META);

        update.setButton(9, "recipe.ingredient_3");
        update.setButton(11, "recipe.ingredient_4");
        update.setButton(13, "recipe.ingredient_5");

        update.setButton(19, "recipe.ingredient_2");
        update.setButton(21, "recipe.ingredient_1");

        update.setButton(29, "recipe.ingredient_0");

        update.setButton(38, "cauldron");

        update.setButton(45, "can_cook_in_lava");
        update.setButton(46, "can_cook_in_water");
        update.setButton(47, "fluid_level");
        update.setButton(48, "campfire");
        update.setButton(49, "soul_campfire");
        update.setButton(50, "signal_fire");

        update.setButton(23, "cookingTime");
        update.setButton(32, "xp");

        update.setButton(25, "recipe.result");
        update.setButton(26, "additional_result_0");
        update.setButton(34, "additional_result_1");
        update.setButton(35, "additional_result_2");

        update.setButton(51, ClusterRecipeCreator.GROUP);
        if (cauldronRecipe.isSaved()) {
            update.setButton(52, ClusterRecipeCreator.SAVE);
        }
        update.setButton(53, ClusterRecipeCreator.SAVE_AS);
    }

}
