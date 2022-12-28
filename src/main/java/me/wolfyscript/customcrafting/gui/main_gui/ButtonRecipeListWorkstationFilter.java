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

package me.wolfyscript.customcrafting.gui.main_gui;

import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import com.wolfyscript.utilities.common.gui.GUIClickInteractionDetails;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ButtonRecipeListWorkstationFilter {

    static final String KEY = "workstation_filter";

    private static final LinkedHashMap<RecipeType<?>, ItemStack> FILTERS = new LinkedHashMap<>();
    private static final List<RecipeType<?>> FILTER_KEYS;

    static {
        FILTERS.put(null, new ItemStack(Material.COMPASS));
        FILTERS.put(RecipeType.CRAFTING_SHAPED, new ItemStack(Material.CRAFTING_TABLE));
        FILTERS.put(RecipeType.CRAFTING_SHAPELESS, new ItemStack(Material.CRAFTING_TABLE));
        FILTERS.put(RecipeType.FURNACE, new ItemStack(Material.FURNACE));
        FILTERS.put(RecipeType.BLAST_FURNACE, new ItemStack(Material.BLAST_FURNACE));
        FILTERS.put(RecipeType.SMOKER, new ItemStack(Material.SMOKER));
        FILTERS.put(RecipeType.CAMPFIRE, new ItemStack(Material.CAMPFIRE));
        FILTERS.put(RecipeType.SMITHING, new ItemStack(Material.SMITHING_TABLE));
        FILTERS.put(RecipeType.STONECUTTER, new ItemStack(Material.STONECUTTER));
        FILTERS.put(RecipeType.ELITE_CRAFTING_SHAPED, new ItemStack(Material.CRAFTING_TABLE));
        FILTERS.put(RecipeType.ELITE_CRAFTING_SHAPELESS, new ItemStack(Material.CRAFTING_TABLE));
        FILTER_KEYS = new ArrayList<>(FILTERS.keySet());
    }

    static void register(GuiMenuComponent.ButtonBuilder<CCCache> buttonBuilder) {
        buttonBuilder.action(KEY).state(state -> state.icon(Material.COMPASS).action((holder, cache, btn, slot, details) -> {
            var currentType = cache.getRecipeList().getFilterType();
            if (details instanceof GUIClickInteractionDetails clickEvent) {
                var nextIndex = FILTER_KEYS.indexOf(currentType);
                if (clickEvent.isLeftClick()) {
                    if (++nextIndex >= FILTER_KEYS.size()) {
                        nextIndex = 0;
                    }
                } else if (--nextIndex < 0) {
                    nextIndex = FILTER_KEYS.size() - 1;
                }
                cache.getRecipeList().setFilterType(FILTER_KEYS.get(nextIndex));
            }
            return ButtonInteractionResult.cancel(true);
        }).render((holder, cache, btn, slot, itemStack) -> {
            RecipeType<?> type = cache.getRecipeList().getFilterType();
            itemStack.setType(FILTERS.get(type).getType());
            return CallbackButtonRender.Result.of(itemStack, Placeholder.parsed("type", type != null ? type.getType().toString().replace("CRAFTING", "").replace("_CRAFTING", "").replace("_", " ") : "ALL"));
        })).register();
    }


}
