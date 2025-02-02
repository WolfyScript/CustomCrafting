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

package me.wolfyscript.customcrafting.listeners.crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.utils.CraftManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.plugin.RegisteredListener;

public class CraftListener implements Listener {

    private final CustomCrafting customCrafting;
    private final CraftManager craftManager;

    public CraftListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.craftManager = customCrafting.getCraftManager();

        if(customCrafting.getConfigHandler().getConfig().isIaFix()) {
            HandlerList handlerList = PrepareItemCraftEvent.getHandlerList();
            for (RegisteredListener rl : handlerList.getRegisteredListeners()) {
                if (rl.getListener().getClass().getCanonicalName().contains("itemsadder")) {
                    if (rl.getPriority() == EventPriority.MONITOR) {
                        RegisteredListener newRl = new RegisteredListener(rl.getListener(), rl.getExecutor(), EventPriority.HIGHEST, rl.getPlugin(), rl.isIgnoringCancelled());
                        handlerList.unregister(rl);
                        handlerList.register(newRl);
                    }
                }
            }
        }
        Bukkit.getPluginManager().registerEvents(new EventBasedCraftRecipeHandler(customCrafting, craftManager), customCrafting);
    }

    @EventHandler
    public void onAdvancedWorkbench(CustomPreCraftEvent event) {
        if (!event.isCancelled() && event.getRecipe().getNamespacedKey().equals(CustomCrafting.ADVANCED_CRAFTING_TABLE) && !customCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled()) {
            event.setCancelled(true);
        }
    }
}
