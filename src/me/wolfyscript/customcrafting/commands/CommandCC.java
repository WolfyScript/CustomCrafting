package me.wolfyscript.customcrafting.commands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandCC implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (args.length == 0) {
                printInfo(p);
            } else {
                if (args[0].equalsIgnoreCase("studio")) {
                    CustomCrafting.getApi().getInventoryAPI().openGui(p);
                } else if (args[0].equalsIgnoreCase("info")) {
                    printInfo(p);

                } else if (args[0].equalsIgnoreCase("help")) {
                    printHelp(p);
                } else if (args[0].equalsIgnoreCase("clear")) {
                    if (CustomCrafting.hasPlayerCache(p)) {
                        CustomCrafting.renewPlayerCache(p);
                    }
                }
                //TODO RELOAD

            }
        }
        return true;
    }

    public void printInfo(Player p) {
        WolfyUtilities api = CustomCrafting.getApi();
        api.sendPlayerMessage(p, "~*~*~*~*&8[&3&lCustomCrafting&8]&7~*~*~*~*~");
        api.sendPlayerMessage(p, "");
        api.sendPlayerMessage(p, "      &n     by &b&n&lWolfyScript&7&n      ");
        api.sendPlayerMessage(p, "        ------------------");
        api.sendPlayerMessage(p, "");
        api.sendPlayerMessage(p, "             &nVersion:&r&b " + CustomCrafting.getInst().getDescription().getVersion());
        api.sendPlayerMessage(p, "");
        api.sendPlayerMessage(p, "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");
        api.sendPlayerMessage(p, "&3type &6\"/cc help\" &3for help!");
    }

    public void printHelp(Player p) {
        WolfyUtilities api = CustomCrafting.getApi();
        api.sendPlayerMessage(p, "~*~*~*~*&8[&3&lCustomCrafting&8]&7~*~*~*~*~");
        api.sendPlayerMessage(p, "");
        api.sendPlayerMessage(p, "            §3/cc §6<label>");
        api.sendPlayerMessage(p, "");
        api.sendPlayerMessage(p, "  §6help    §7-  §3Displays list of commands");
        api.sendPlayerMessage(p, "  §6clear   §7-  §3Resets the player cache (§cClears Studio contents!§3)");
        api.sendPlayerMessage(p, "  §6studio  §7-  §3Opens the Studio");
        api.sendPlayerMessage(p, "  §6info    §7-  §3Displays info about this plugin");
        api.sendPlayerMessage(p, "  §6vanilla §7-  §3Displays info about this plugin");
        api.sendPlayerMessage(p, "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");
    }

    private final List<String> COMPLETIONS = Arrays.asList("help", "clear", "studio", "info", "vanilla");

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> results = new ArrayList<>();
        StringUtil.copyPartialMatches(strings[0], COMPLETIONS, results);
        Collections.sort(results);
        return results;
    }
}
