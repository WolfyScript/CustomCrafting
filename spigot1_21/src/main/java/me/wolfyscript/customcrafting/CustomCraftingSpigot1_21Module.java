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

package me.wolfyscript.customcrafting;

import me.wolfyscript.customcrafting.listeners.crafting.CrafterListener;
import org.bukkit.Bukkit;

public class CustomCraftingSpigot1_21Module implements CustomCraftingSpigotAPIModule {

    private final CustomCrafting customCrafting;

    public CustomCraftingSpigot1_21Module(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    public void load() {
        // Do something in the future
    }

    @Override
    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new CrafterListener(customCrafting), customCrafting);
    }

}
