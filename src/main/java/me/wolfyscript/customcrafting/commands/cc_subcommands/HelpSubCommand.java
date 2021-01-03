package me.wolfyscript.customcrafting.commands.cc_subcommands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HelpSubCommand extends AbstractSubCommand {

    public HelpSubCommand(CustomCrafting customCrafting) {
        super("help", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] var4) {
        WolfyUtilities api = CustomCrafting.getApi();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.help")) {
                printHelp(p);
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] var4) {
        return null;
    }

    public void printHelp(Player p) {
        WolfyUtilities api = CustomCrafting.getApi();
        api.getChat().sendMessage(p, "~*~*~*~*&8[&3&lCustomCrafting&8]&7~*~*~*~*~");
        List<String> help = api.getLanguageAPI().replaceKey("commands.help");
        for (String line : help) {
            api.getChat().sendMessage(p, line);
        }
        api.getChat().sendMessage(p, "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");
    }
}
