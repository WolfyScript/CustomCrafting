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

package me.wolfyscript.customcrafting.commands.cc_subcommands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.lib.net.kyori.adventure.text.format.NamedTextColor;
import me.wolfyscript.lib.net.kyori.adventure.text.format.TextDecoration;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InfoSubCommand extends AbstractSubCommand {

    public InfoSubCommand(CustomCrafting customCrafting) {
        super("info", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] var4) {
        if (sender instanceof Player p && ChatUtils.checkPerm(p, "customcrafting.cmd.info")) {
            printInfo(p);
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] var4) {
        return null;
    }

    public void printInfo(Player p) {
        WolfyUtilities api = customCrafting.getApi();
        var chat = api.getChat();
        chat.sendMessage(p, Component.text("———————— ", NamedTextColor.GRAY).append(customCrafting.getColoredTitle()).append(Component.text(" ————————")));
        api.getChat().sendMessages(p, Component.empty(),
                Component.text("    Author: ", NamedTextColor.GRAY).append(Component.text(String.join(", ", customCrafting.getDescription().getAuthors()), null, TextDecoration.BOLD)),
                Component.empty(),
                Component.text("    Version: ", NamedTextColor.GRAY).append(Component.text(customCrafting.getDescription().getVersion(), null, TextDecoration.BOLD)),
                Component.empty(),
                Component.text("———————————————————————————", NamedTextColor.GRAY));
    }
}
