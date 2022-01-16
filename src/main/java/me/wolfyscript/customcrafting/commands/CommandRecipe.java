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
import me.wolfyscript.customcrafting.commands.recipes.DeleteSubCommand;
import me.wolfyscript.customcrafting.commands.recipes.EditSubCommand;
import me.wolfyscript.customcrafting.commands.recipes.RecipeLookupCommand;
import me.wolfyscript.customcrafting.commands.recipes.SaveSubCommand;
import me.wolfyscript.customcrafting.commands.recipes.ToggleSubCommand;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeBook;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CommandRecipe extends IndexCommand {

    private final CustomCrafting customCrafting;

    public CommandRecipe(CustomCrafting customCrafting) {
        super("recipes", "", "/recipes", new ArrayList<>());
        this.customCrafting = customCrafting;
        registerSubCommand(new EditSubCommand(customCrafting));
        registerSubCommand(new DeleteSubCommand(customCrafting));
        registerSubCommand(new ToggleSubCommand(customCrafting));
        registerSubCommand(new SaveSubCommand(customCrafting));
        registerSubCommand(new RecipeLookupCommand(customCrafting));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0 && sender instanceof Player) {
            WolfyUtilities api = customCrafting.getApi();
            Player p = (Player) sender;
            InventoryAPI<CCCache> invAPI = api.getInventoryAPI(CCCache.class);
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.recipes")) {
                var categories = customCrafting.getDataHandler().getCategories();
                if (categories.getSortedCategories().size() > 1) {
                    invAPI.openCluster(p, ClusterRecipeBook.KEY);
                } else if (!categories.getSortedCategories().isEmpty()) {
                    invAPI.getGuiHandler(p).getCustomCache().getKnowledgeBook().setCategory(categories.getCategory(0));
                    invAPI.openGui(p, ClusterRecipeBook.RECIPE_BOOK);
                }
            }
        }
        return super.execute(sender, s, args);
    }
}
