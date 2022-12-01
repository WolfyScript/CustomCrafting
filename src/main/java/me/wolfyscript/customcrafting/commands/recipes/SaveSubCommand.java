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

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SaveSubCommand extends AbstractSubCommand {

    public SaveSubCommand(CustomCrafting customCrafting) {
        super("save", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        WolfyUtilsBukkit api = customCrafting.getApi();
        if (sender instanceof Player && ChatUtils.checkPerm(sender, "customcrafting.cmd.recipes_save")) {
            api.getRegistries().getCustomItems().entrySet().forEach(entry -> {
                api.getConsole().info("Saving item: " + entry.getKey().toString());
                ItemLoader.saveItem(entry.getKey(), entry.getValue());
            });
            customCrafting.getRegistries().getRecipes().values().forEach(recipe -> {
                api.getConsole().info("Saving recipe: " + recipe.getNamespacedKey());
                recipe.save();
            });
            sender.sendMessage("§eAll recipes are resaved! See the console log for errors.");
            sender.sendMessage("§cNotice that some recipes must be recreated due incompatibility! These are: ");
            sender.sendMessage("§c- recipes that caused errors when saving (their config is corrupted from now on)");
            sender.sendMessage("§c- recipes that don't work when the server is restarted");
            sender.sendMessage("§eYou can get or ask for further information on the discord!");
        }
        return true;
    }

    @Override
    protected @Nullable
    List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] args) {
        List<String> recipes = customCrafting.getDataHandler().getBukkitNamespacedKeys();
        recipes.addAll(customCrafting.getRegistries().getRecipes().get(CraftingRecipe.class).stream().map(recipe -> recipe.getNamespacedKey().toString()).collect(Collectors.toSet()));
        return StringUtil.copyPartialMatches(args[args.length - 1], recipes, new ArrayList<>());
    }
}
