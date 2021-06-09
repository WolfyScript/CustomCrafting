package me.wolfyscript.customcrafting.commands.cc_subcommands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DarkModeSubCommand extends AbstractSubCommand {

    public DarkModeSubCommand(CustomCrafting customCrafting) {
        super("darkmode", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] var4) {
        if (sender instanceof Player p) {
            PlayerUtil.getStore(p).setDarkMode(!PlayerUtil.getStore(p).isDarkMode());
            WolfyUtilities api = customCrafting.getApi();
            if (PlayerUtil.getStore(p).isDarkMode()) {
                api.getChat().sendMessage(p, "$commands.darkmode.enabled$");
            } else {
                api.getChat().sendMessage(p, "$commands.darkmode.disabled$");
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] var4) {
        return null;
    }
}
