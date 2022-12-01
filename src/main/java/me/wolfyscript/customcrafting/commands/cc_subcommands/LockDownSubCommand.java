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

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import java.util.ArrayList;
import java.util.List;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LockDownSubCommand extends AbstractSubCommand {

    public LockDownSubCommand(CustomCrafting customCrafting) {
        super("lockdown", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] var4) {
        WolfyUtilsBukkit api = customCrafting.getApi();
        if (ChatUtils.checkPerm(sender, "customcrafting.cmd.lockdown")) {
            customCrafting.getConfigHandler().getConfig().toggleLockDown();
            if (sender instanceof Player) {
                if (customCrafting.getConfigHandler().getConfig().isLockedDown()) {
                    api.getChat().sendMessage((Player) sender, "$commands.lockdown.enabled$");
                } else {
                    api.getChat().sendMessage((Player) sender, "$commands.lockdown.disabled$");
                }
            } else {
                if (customCrafting.getConfigHandler().getConfig().isLockedDown()) {
                    api.getConsole().info("$commands.lockdown.enabled$");
                } else {
                    api.getConsole().info("$commands.lockdown.disabled$");
                }
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] var4) {
        return null;
    }
}
