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
import me.wolfyscript.customcrafting.commands.cc_subcommands.DarkModeSubCommand;
import me.wolfyscript.customcrafting.commands.cc_subcommands.DataBaseSubCommand;
import me.wolfyscript.customcrafting.commands.cc_subcommands.DebugSubCommand;
import me.wolfyscript.customcrafting.commands.cc_subcommands.GiveSubCommand;
import me.wolfyscript.customcrafting.commands.cc_subcommands.HelpSubCommand;
import me.wolfyscript.customcrafting.commands.cc_subcommands.InfoSubCommand;
import me.wolfyscript.customcrafting.commands.cc_subcommands.LockDownSubCommand;
import me.wolfyscript.customcrafting.commands.cc_subcommands.ReloadSubCommand;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.cauldron.CauldronWorkstationCluster;
import me.wolfyscript.customcrafting.gui.elite_crafting.EliteCraftingCluster;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeBook;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeView;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CommandCC extends IndexCommand {

    private final CustomCrafting customCrafting;

    public CommandCC(CustomCrafting cc) {
        super("customcrafting", "The main command of CustomCrafting", "/customcrafting [<option>]", cc.getConfigHandler().getConfig().getCustomCraftingAlias());
        this.customCrafting = cc;
        registerSubCommand(new DarkModeSubCommand(cc));
        registerSubCommand(new DataBaseSubCommand(cc));
        registerSubCommand(new DebugSubCommand(cc));
        registerSubCommand(new GiveSubCommand(cc));
        registerSubCommand(new HelpSubCommand(cc));
        registerSubCommand(new InfoSubCommand(cc));
        registerSubCommand(new LockDownSubCommand(cc));
        registerSubCommand(new ReloadSubCommand(cc));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
        WolfyUtilities api = customCrafting.getApi();
        if (args.length > 0) {
            if (!super.execute(sender, s, args)) {
                getSubCommands().get("help").onCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        } else if (sender instanceof Player) {
            openGUI((Player) sender, api.getInventoryAPI(CCCache.class));
        }
        return true;
    }

    public void openGUI(Player p, InventoryAPI<CCCache> invAPI) {
        if (ChatUtils.checkPerm(p, "customcrafting.cmd.studio", true)) {
            GuiCluster<CCCache> cluster = invAPI.getGuiHandler(p).getCluster();
            if (cluster == null) {
                invAPI.openCluster(p, ClusterMain.KEY);
                return;
            }
            switch (cluster.getId()) {
                case ClusterRecipeBook.KEY, ClusterRecipeView.KEY, EliteCraftingCluster.KEY, CauldronWorkstationCluster.KEY -> invAPI.openCluster(p, ClusterMain.KEY);
                default -> invAPI.getGuiHandler(p).openCluster();
            }
        }
    }
}
