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

package me.wolfyscript.customcrafting.commands;

import me.wolfyscript.customcrafting.CustomCrafting;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractSubCommand {

    protected final CustomCrafting customCrafting;
    protected final WolfyUtilsBukkit api;
    private final String label;
    private final List<String> alias;

    protected AbstractSubCommand(String label, List<String> alias, CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.api = customCrafting.getApi();
        this.label = label;
        this.alias = alias;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getAlias() {
        return alias;
    }

    protected abstract boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args);

    @Nullable
    protected abstract List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args);
}
