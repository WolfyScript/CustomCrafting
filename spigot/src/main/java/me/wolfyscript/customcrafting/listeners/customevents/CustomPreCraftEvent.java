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

package me.wolfyscript.customcrafting.listeners.customevents;

import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.utils.CraftManager;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomPreCraftEvent extends CustomCraftEvent {

    private static final HandlerList handlers = new HandlerList();
    private me.wolfyscript.customcrafting.recipes.items.Result result;
    private List<List<ItemStack>> ingredients;
    private final CraftManager.MatrixData matrix;

    public CustomPreCraftEvent(CraftingRecipe<?, ?> craftingRecipe, Player player, Inventory inventory, CraftManager.MatrixData matrix) {
        super(craftingRecipe, player, inventory);
        this.result = craftingRecipe.getResult();
        this.ingredients = new ArrayList<>();
        this.matrix = matrix;
    }

    public @NotNull me.wolfyscript.customcrafting.recipes.items.Result getResult() {
        return result;
    }

    public void setResult(@NotNull me.wolfyscript.customcrafting.recipes.items.Result result) {
        this.result = result;
    }

    @Deprecated
    public List<List<ItemStack>> getIngredients() {
        return ingredients;
    }

    @Deprecated
    public void setIngredients(List<List<ItemStack>> ingredients) {
        this.ingredients = ingredients;
    }

    public CraftManager.MatrixData getMatrixData() {
        return matrix;
    }

    @Override
    public String getEventName() {
        return super.getEventName();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
