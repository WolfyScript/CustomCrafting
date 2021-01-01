package me.wolfyscript.customcrafting.commands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.cc_subcommands.*;
import me.wolfyscript.customcrafting.data.CCCache;
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
        super("customcrafting", "The main command of CustomCrafting", "/customcrafting <label>", cc.getConfigHandler().getConfig().getCustomCraftingAlias());
        this.customCrafting = cc;
        registerSubCommand(new DarkModeSubCommand(cc));
        registerSubCommand(new DataBaseSubCommand(cc));
        registerSubCommand(new DebugSubCommand(cc));
        registerSubCommand(new GiveSubCommand(cc));
        registerSubCommand(new HelpSubCommand(cc));
        registerSubCommand(new InfoSubCommand(cc));
        registerSubCommand(new KnowledgeSubCommand(cc));
        registerSubCommand(new LockDownSubCommand(cc));
        registerSubCommand(new ReloadSubCommand(cc));
        registerSubCommand(new SettingsSubCommand(cc));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
        WolfyUtilities api = CustomCrafting.getApi();
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
            if (cluster != null && !cluster.getId().equals("recipe_book") && !cluster.getId().equals("crafting")) {
                invAPI.getGuiHandler(p).openCluster();
            } else {
                invAPI.openCluster(p, "none");
            }
        }
    }
}
