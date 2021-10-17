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

package me.wolfyscript.customcrafting.commands.recipes;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.chat.ClickEvent;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DeleteSubCommand extends AbstractSubCommand {

    public DeleteSubCommand(CustomCrafting customCrafting) {
        super("delete", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player player && ChatUtils.checkPerm(player, "customcrafting.cmd.recipes.delete") && args.length > 0) {
            WolfyUtilities api = customCrafting.getApi();
            NamespacedKey key = NamespacedKey.of(args[0]);
            if (key != null) {
                CustomRecipe<?> customRecipe = CCRegistry.RECIPES.get(key);
                if (customRecipe != null) {
                    api.getChat().sendMessage(player, "$msg.gui.recipe_editor.delete.confirm$", new Pair<>("%RECIPE%", customRecipe.getNamespacedKey().toString()));
                    api.getChat().sendActionMessage(player, new ClickData("$msg.gui.recipe_editor.delete.confirmed$", (wolfyUtilities, player1) -> Bukkit.getScheduler().runTask(customCrafting, () -> customRecipe.delete(player))), new ClickData("$msg.gui.recipe_editor.delete.declined$", (wolfyUtilities, player1) -> api.getChat().sendMessage(player1, "§cCancelled"), new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/recipes delete ")));
                } else {
                    api.getChat().sendMessage((Player) sender, "$msg.gui.recipe_editor.not_existing$", new Pair<>("%RECIPE%", args[0] + ":" + args[1]));
                }
            }
        }
        return true;
    }

    @Override
    protected @Nullable
    List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        List<String> recipes = CCRegistry.RECIPES.keySet().stream().map(NamespacedKey::toString).toList();
        StringUtil.copyPartialMatches(args[args.length - 1], recipes, results);
        return results;
    }
}
