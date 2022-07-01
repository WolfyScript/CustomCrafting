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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.chat.ClickEvent;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteSubCommand extends AbstractSubCommand {

    public DeleteSubCommand(CustomCrafting customCrafting) {
        super("delete", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player player && ChatUtils.checkPerm(player, "customcrafting.cmd.recipes_delete") && args.length > 0) {
            WolfyUtilities api = customCrafting.getApi();
            NamespacedKey key = NamespacedKey.of(args[0]);
            if (key != null) {
                CustomRecipe<?> customRecipe = customCrafting.getRegistries().getRecipes().get(key);
                if (customRecipe != null) {
                    api.getChat().sendMessage(player, "$commands.recipes.delete.confirm$", new Pair<>("%recipe%", customRecipe.getNamespacedKey().toString()));
                    api.getChat().sendActionMessage(player,
                            new ClickData("$commands.recipes.delete.confirmation$",
                                    (wolfyUtilities, player1) -> Bukkit.getScheduler().runTask(customCrafting, () -> customRecipe.delete(player)),
                                    true
                            ),
                            new ClickData(
                                    "$commands.recipes.delete.cancel$",
                                    (wolfyUtilities, player1) -> {
                                    },
                                    true,
                                    new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/recipes delete ")
                            )
                    );
                } else {
                    api.getChat().sendMessage((Player) sender, "$commands.recipes.invalid_recipe$", new Pair<>("%recipe%", args[0]));
                }
            }
        }
        return true;
    }

    @Override
    protected @Nullable
    List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] args) {
        return NamespacedKeyUtils.getPartialMatches(args[args.length - 1], customCrafting.getRegistries().getRecipes().keySet()).stream().map(NamespacedKey::toString).collect(Collectors.toList());
    }
}
