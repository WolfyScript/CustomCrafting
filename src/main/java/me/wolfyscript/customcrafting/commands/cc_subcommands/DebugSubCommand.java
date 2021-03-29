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

public class DebugSubCommand extends AbstractSubCommand {

    public DebugSubCommand(CustomCrafting customCrafting) {
        super("debug", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] var4) {
        if (sender instanceof Player) {
            WolfyUtilities api = customCrafting.getApi();
            Player p = (Player) sender;
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.debug")) {
                customCrafting.getConfigHandler().getConfig().set("debug", !api.hasDebuggingMode());
                api.getChat().sendMessage(p, "Set Debug to: " + api.hasDebuggingMode());
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] var4) {
        return null;
    }
}
