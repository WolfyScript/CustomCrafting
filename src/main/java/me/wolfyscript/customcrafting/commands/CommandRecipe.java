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

import java.util.ArrayList;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.recipes.DeleteSubCommand;
import me.wolfyscript.customcrafting.commands.recipes.EditSubCommand;
import me.wolfyscript.customcrafting.commands.recipes.RecipeLookupCommand;
import me.wolfyscript.customcrafting.commands.recipes.SaveSubCommand;
import me.wolfyscript.customcrafting.commands.recipes.ToggleSubCommand;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandRecipe extends IndexCommand {

    public CommandRecipe(CustomCrafting customCrafting) {
        super("recipes", "", "/recipes", new ArrayList<>());
        registerSubCommand(new EditSubCommand(customCrafting));
        registerSubCommand(new DeleteSubCommand(customCrafting));
        registerSubCommand(new ToggleSubCommand(customCrafting));
        registerSubCommand(new SaveSubCommand(customCrafting));
        registerSubCommand(new RecipeLookupCommand(customCrafting));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0 && sender instanceof Player player && ChatUtils.checkPerm(player, "customcrafting.cmd.recipes")) {
            PlayerUtil.openRecipeBook(player);
        }
        return super.execute(sender, s, args);
    }
}
