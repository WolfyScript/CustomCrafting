package me.wolfyscript.customcrafting.commands.cc_subcommands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.handlers.DataBaseHandler;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.util.Registry;
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
            WolfyUtilities api = customCrafting.getApi();
            Player p = (Player) sender;
            Chat chat = api.getChat();
            //give <player> <namespace:key> [amount]
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.database")) {
                if (!customCrafting.hasDataBaseHandler()) {
                    chat.sendMessage(p, "&4No Database found!");
                    return true;
                }
                if (args.length >= 1) {
                    switch (args[0]) {
                        case "export_recipes":
                            chat.sendMessage(p, "Exporting recipes to Database...");
                            new Thread(() -> {
                                DataBaseHandler dataBaseHandler = customCrafting.getDataBaseHandler();
                                me.wolfyscript.customcrafting.Registry.RECIPES.values().forEach(dataBaseHandler::updateRecipe);
                                chat.sendConsoleMessage("Successfully exported recipes to database");
                            }).start();
                            break;
                        case "export_items":
                            chat.sendMessage(p, "Exporting custom items to Database...");
                            new Thread(() -> {
                                DataBaseHandler dataBaseHandler = customCrafting.getDataBaseHandler();
                                Registry.CUSTOM_ITEMS.forEach(item -> dataBaseHandler.updateItem(item.getNamespacedKey(), item));
                                chat.sendConsoleMessage("Successfully exported custom items to database");
                            }).start();
                            break;
                        default:
                            //No option
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] strings) {
        List<String> results = strings.length == 1 ? StringUtil.copyPartialMatches(strings[0], DATABASE, new ArrayList<>()) : new ArrayList<>();
        Collections.sort(results);
        return results;
    }
}
