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

public class LockDownSubCommand extends AbstractSubCommand {

    public LockDownSubCommand(CustomCrafting customCrafting) {
        super("lockdown", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] var4) {
        WolfyUtilities api = customCrafting.getApi();
        if (ChatUtils.checkPerm(sender, "customcrafting.cmd.lockdown")) {
            customCrafting.getConfigHandler().getConfig().toggleLockDown();
            if (sender instanceof Player) {
                if (customCrafting.getConfigHandler().getConfig().isLockedDown()) {
                    api.getChat().sendMessage((Player) sender, "$commands.lockdown.enabled$");
                } else {
                    api.getChat().sendMessage((Player) sender, "$commands.lockdown.disabled$");
                }
            } else {
                if (customCrafting.getConfigHandler().getConfig().isLockedDown()) {
                    api.getChat().sendConsoleMessage("$commands.lockdown.enabled$");
                } else {
                    api.getChat().sendConsoleMessage("$commands.lockdown.disabled$");
                }
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] var4) {
        return null;
    }
}
