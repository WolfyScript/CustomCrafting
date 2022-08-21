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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CauldronPreCookEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final CustomCrafting customCrafting;
    private boolean cancelled;
    private int cookingTime;
    private final Block cauldron;
    private final Player player;
    private CustomRecipeCauldron recipe;

    public CauldronPreCookEvent(CustomCrafting customCrafting, CustomRecipeCauldron recipe, Player player, Block cauldron) {
        this.customCrafting = customCrafting;
        this.recipe = recipe;
        this.cookingTime = recipe.getCookingTime();
        this.player = player;
        this.cauldron = cauldron;
    }

    public CustomCrafting getCustomCrafting() {
        return customCrafting;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    @Deprecated
    public boolean dropItems() {
        return false;
    }

    @Deprecated
    public void setDropItems(boolean dropItems) { }

    public CustomRecipeCauldron getRecipe() {
        return recipe;
    }

    public void setRecipe(CustomRecipeCauldron recipe) {
        this.recipe = recipe;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getCauldron() {
        return cauldron;
    }
}
