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

public class InfoSubCommand extends AbstractSubCommand {

    public InfoSubCommand(CustomCrafting customCrafting) {
        super("info", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] var4) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.info")) {
                printInfo(p);
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] var4) {
        return null;
    }

    public void printInfo(Player p) {
        WolfyUtilities api = CustomCrafting.getApi();
        api.getChat().sendMessages(p, "~*~*~*~*&8[&3&lCustomCrafting&8]&7~*~*~*~*~",
                "",
                "      &n     by &b&n&lWolfyScript&7&n      ",
                "        ------------------",
                "",
                "             &nVersion:&r&b " + customCrafting.getDescription().getVersion(),
                "",
                "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");
    }
}
