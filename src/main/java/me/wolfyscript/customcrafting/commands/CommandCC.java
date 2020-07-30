package me.wolfyscript.customcrafting.commands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.cc_subcommands.*;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandCC extends Command {

    private final CustomCrafting customCrafting;

    private final HashMap<String, AbstractSubCommand> subCommands = new HashMap<>();

    public CommandCC(CustomCrafting cc) {
        super("customcrafting", "The main command of CustomCrafting", "/customcrafting <label>", cc.getConfigHandler().getConfig().getCustomCraftingAlias());
        this.customCrafting = cc;
        registerSubCommand(new ClearSubCommand(cc));
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

    private void registerSubCommand(AbstractSubCommand subCommand) {
        subCommands.put(subCommand.getLabel(), subCommand);
        subCommand.getAlias().forEach(s -> subCommands.putIfAbsent(s, subCommand));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
        WolfyUtilities api = CustomCrafting.getApi();
        if (args.length > 0) {
            if (subCommands.containsKey(args[0])) {
                return subCommands.get(args[0]).onCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
            } else {
                subCommands.get("help").onCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        } else if (sender instanceof Player) {
            openGUI((Player) sender, api.getInventoryAPI());
        }
        return true;
    }

    public void openGUI(Player p, InventoryAPI invAPI) {
        if (ChatUtils.checkPerm(p, "customcrafting.cmd.studio", false)) {
            if (!invAPI.getGuiHandler(p).getCurrentGuiCluster().isEmpty() && !invAPI.getGuiHandler(p).getCurrentGuiCluster().equals("recipe_book") && !invAPI.getGuiHandler(p).getCurrentGuiCluster().equals("crafting")) {
                invAPI.getGuiHandler(p).openCluster();
            } else {
                invAPI.openCluster(p, "none");
            }
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] strings) throws IllegalArgumentException {
        List<String> results = new ArrayList<>();
        if (strings.length > 1) {
            if (subCommands.containsKey(strings[0])) {
                return subCommands.get(strings[0]).onTabComplete(sender, strings[0], Arrays.copyOfRange(strings, 1, strings.length));
            } else {
                return subCommands.get("help").onTabComplete(sender, strings[0], Arrays.copyOfRange(strings, 1, strings.length));
            }
        } else if (sender instanceof Player) {
            StringUtil.copyPartialMatches(strings[0], subCommands.keySet(), results);
        }
        Collections.sort(results);
        return results;
    }
}
