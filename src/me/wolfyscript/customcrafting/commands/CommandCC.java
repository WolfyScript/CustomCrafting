package me.wolfyscript.customcrafting.commands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCC implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(s.equalsIgnoreCase("cc") || s.equalsIgnoreCase("customcrafting")){
            if(sender instanceof Player){
                Player p = (Player) sender;

                if(args.length == 0){
                    printInfo(p);
                }else {
                    if(args[0].equalsIgnoreCase("studio")){
                        CustomCrafting.getApi().getInventoryAPI().openGui(p);
                    }else if(args[0].equalsIgnoreCase("info")){
                        printInfo(p);

                    }else if(args[0].equalsIgnoreCase("help")){
                        printHelp(p);
                    }else if(args[0].equalsIgnoreCase("clear")){
                        if(CustomCrafting.hasPlayerCache(p)){
                            CustomCrafting.renewPlayerCache(p);
                        }
                    }

                }
            }
        }
        return true;
    }

    public void printInfo(Player p){
        WolfyUtilities api = CustomCrafting.getApi();
        api.sendPlayerMessage(p, "~*~*~*~*&8[&3&lCustomCrafting&8]&7~*~*~*~*~");
        api.sendPlayerMessage(p, "");
        api.sendPlayerMessage(p, "      &n     by &b&n&lWolfyScript&7&n      ");
        api.sendPlayerMessage(p, "        ------------------");
        api.sendPlayerMessage(p, "");
        api.sendPlayerMessage(p, "             &nVersion:&r&b "+CustomCrafting.getInst().getDescription().getVersion());
        api.sendPlayerMessage(p, "");
        api.sendPlayerMessage(p, "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");
        api.sendPlayerMessage(p, "&3type &6\"/cc help\" &3for help!");
    }

    public void printHelp(Player p){
        WolfyUtilities api = CustomCrafting.getApi();
        api.sendPlayerMessage(p, "~*~*~*~*&8[&3&lCustomCrafting&8]&7~*~*~*~*~");
        api.sendPlayerMessage(p, "");
        api.sendPlayerMessage(p, "            §3/cc §6<label>");
        api.sendPlayerMessage(p, "");
        api.sendPlayerMessage(p, "  §6help    §7-  §3Displays list of commands");
        api.sendPlayerMessage(p, "  §6clear   §7-  §3Resets the player cache (§cClears Studio contents!§3)");
        api.sendPlayerMessage(p, "  §6studio  §7-  §3Opens the Studio");
        api.sendPlayerMessage(p, "  §6info    §7-  §3Displays info about this plugin");
        api.sendPlayerMessage(p, "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");

    }


}
