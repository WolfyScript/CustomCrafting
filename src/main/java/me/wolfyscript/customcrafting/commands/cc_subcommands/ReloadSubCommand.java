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
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeBook;
import me.wolfyscript.customcrafting.gui.recipebook.MenuRecipeOverview;
import me.wolfyscript.customcrafting.registry.RegistryRecipes;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.lib.net.kyori.adventure.text.TextComponent;
import me.wolfyscript.lib.net.kyori.adventure.text.format.NamedTextColor;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.registry.RegistryCustomItem;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReloadSubCommand extends AbstractSubCommand {

    public ReloadSubCommand(CustomCrafting customCrafting) {
        super("reload", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] var4) {
        WolfyUtilities api = customCrafting.getApi();
        if (sender instanceof Player p && !ChatUtils.checkPerm(p, "customcrafting.cmd.reload")) {
            return true;
        }
        var invAPI = api.getInventoryAPI(CCCache.class);
        var configHandler = customCrafting.getConfigHandler();
        var dataHandler = customCrafting.getDataHandler();
        sendMessage(sender, Component.text("Unloading Languages...", NamedTextColor.YELLOW));
        customCrafting.getApi().getLanguageAPI().unregisterLanguages();
        sendMessage(sender, Component.text("Unloading Recipes...", NamedTextColor.YELLOW));
        //Unregister recipes
        RegistryRecipes registryRecipes = customCrafting.getRegistries().getRecipes();
        registryRecipes.get(NamespacedKeyUtils.NAMESPACE).forEach(customRecipe -> registryRecipes.remove(customRecipe.getNamespacedKey()));
        sendMessage(sender, Component.text("Unloading Items...", NamedTextColor.YELLOW));
        //Unregister items
        RegistryCustomItem registryCustomItem = customCrafting.getApi().getRegistries().getCustomItems();
        registryCustomItem.get(NamespacedKeyUtils.NAMESPACE).forEach(customItem -> registryCustomItem.remove(customItem.getNamespacedKey()));
        //Load new data
        sendMessage(sender, Component.text("Reloading Config...", NamedTextColor.YELLOW));
        configHandler.load();
        sendMessage(sender, Component.text("Loading Recipe & Items...", NamedTextColor.YELLOW));
        dataHandler.load();
        configHandler.getRecipeBookConfig().index(customCrafting);
        sendMessage(sender, Component.text("Reloading GUIs", NamedTextColor.YELLOW));
        ((MenuRecipeOverview) invAPI.getGuiWindow(ClusterRecipeBook.RECIPE_BOOK)).reset();
        invAPI.reset();
        sendMessage(sender, Component.text("Reload done!", NamedTextColor.GREEN));
        return true;
    }

    private void sendMessage(CommandSender sender, TextComponent message) {
        if (sender instanceof Player player) {
            customCrafting.getApi().getChat().sendMessage(player, message);
        } else {
            customCrafting.getLogger().info(message.content());
        }
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] var4) {
        return null;
    }
}
