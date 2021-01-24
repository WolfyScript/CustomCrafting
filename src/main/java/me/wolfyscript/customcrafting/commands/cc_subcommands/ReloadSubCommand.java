package me.wolfyscript.customcrafting.commands.cc_subcommands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReloadSubCommand extends AbstractSubCommand {

    public ReloadSubCommand(CustomCrafting customCrafting) {
        super("reload", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] var4) {
        WolfyUtilities api = CustomCrafting.getApi();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.reload")) {
                api.getChat().sendMessage(p, "&eReloading Items and Recipes!");
                if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_15)) {
                    //Reload Recipes
                    DataHandler dataHandler = customCrafting.getRecipeHandler();
                    dataHandler.saveData();
                    try {
                        dataHandler.load(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    api.getChat().sendMessage(p, "&aReload Complete");
                    return true;
                }
                api.getChat().sendMessage(p, "&cThis command is only available in 1.15+");
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] var4) {
        return null;
    }
}
