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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DataBaseSubCommand extends AbstractSubCommand {

    private final List<String> DATABASE = Arrays.asList("export");

    public DataBaseSubCommand(CustomCrafting customCrafting) {
        super("database", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player) {
            WolfyUtilities api = CustomCrafting.getApi();
            Player p = (Player) sender;
            //give <player> <namespace:key> [amount]
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.database")) {
                if (args.length >= 1) {
                    switch (args[0]) {
                        case "export":
                            if (CustomCrafting.hasDataBaseHandler()) {
                                api.sendPlayerMessage(p, "Exporting json configs to Database.");
                                new Thread(() -> customCrafting.getRecipeHandler().migrateConfigsToDB(CustomCrafting.getDataBaseHandler())).run();
                            } else {
                                api.sendPlayerMessage(p, "&4No Database found!");
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
            StringUtil.copyPartialMatches(strings[0], DATABASE, results);
        }
        Collections.sort(results);
        return results;
    }
}
