package me.wolfyscript.customcrafting.commands;

import me.wolfyscript.customcrafting.CustomCrafting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCC implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(s.equalsIgnoreCase("cc")){
            if(sender instanceof Player){
                Player player = (Player) sender;
                CustomCrafting.getApi().getInventoryAPI().openGui(player);
            }
        }

        return true;
    }


}
