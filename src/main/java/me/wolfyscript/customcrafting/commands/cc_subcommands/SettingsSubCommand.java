package me.wolfyscript.customcrafting.commands.cc_subcommands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SettingsSubCommand extends AbstractSubCommand {

    private final List<String> SETTINGS = Arrays.asList("pretty_printing", "advanced_workbench");

    public SettingsSubCommand(CustomCrafting customCrafting) {
        super("settings", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        WolfyUtilities api = CustomCrafting.getApi();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.settings")) {
                if (args.length > 1) {
                    switch (args[0]) {
                        case "pretty_printing":
                            if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")) {
                                customCrafting.getConfigHandler().getConfig().setPrettyPrinting(Boolean.valueOf(args[1].toLowerCase(Locale.ROOT)));
                                api.getChat().sendPlayerMessage(p, "&aSet &epretty printing &ato &e" + args[1].toLowerCase(Locale.ROOT));
                            }
                            break;
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] strings) {
        List<String> results = new ArrayList<>();
        if (strings.length == 1) {
            StringUtil.copyPartialMatches(strings[0], SETTINGS, results);
        } else if (strings.length == 2) {
            switch (strings[1]) {
                case "advanced_workbench":
                case "pretty_printing":
                    StringUtil.copyPartialMatches(strings[1], Arrays.asList("true", "false"), results);
                    break;
            }
        }
        Collections.sort(results);
        return results;
    }
}
