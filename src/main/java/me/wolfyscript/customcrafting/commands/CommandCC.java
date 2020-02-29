package me.wolfyscript.customcrafting.commands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.handlers.InventoryHandler;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.*;

public class CommandCC implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        WolfyUtilities api = CustomCrafting.getApi();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            InventoryAPI invAPI = api.getInventoryAPI();
            if (args.length == 0) {
                openGUI(p, invAPI);
            } else {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "lockdown":
                        if (ChatUtils.checkPerm(p, "customcrafting.cmd.lockdown")) {
                            CustomCrafting.getConfigHandler().getConfig().toggleLockDown();
                            if (CustomCrafting.getConfigHandler().getConfig().isLockedDown()) {
                                api.sendPlayerMessage(p, "$commands.lockdown.enabled$");
                            } else {
                                api.sendPlayerMessage(p, "$commands.lockdown.disabled$");
                            }
                        }
                        break;
                    case "darkmode":
                        CustomCrafting.getPlayerStatistics(p).setDarkMode(!CustomCrafting.getPlayerStatistics(p).getDarkMode());
                        if (CustomCrafting.getPlayerStatistics(p).getDarkMode()) {
                            api.sendPlayerMessage(p, "$commands.darkmode.enabled$");
                        } else {
                            api.sendPlayerMessage(p, "$commands.darkmode.disabled$");
                        }
                        break;
                    case "studio":
                        openGUI(p, invAPI);
                        break;
                    case "crafting":
                        if (ChatUtils.checkPerm(p, "customcrafting.cmd.crafting")) {
                            invAPI.openCluster(p, "crafting");
                        }
                        break;
                    case "info":
                        if (ChatUtils.checkPerm(p, "customcrafting.cmd.info")) {
                            printInfo(p);
                        }
                        break;
                    case "help":
                        if (ChatUtils.checkPerm(p, "customcrafting.cmd.help")) {
                            printHelp(p);
                        }
                        break;
                    case "clear":
                        if (ChatUtils.checkPerm(p, "customcrafting.cmd.clear")) {
                            CustomCrafting.renewPlayerStatistics(p);
                        }
                        break;
                    case "reload":
                        if (ChatUtils.checkPerm(p, "customcrafting.cmd.reload")) {
                            CustomCrafting.getApi().getInventoryAPI().reset();
                            CustomCrafting.getApi().getLanguageAPI().unregisterLanguages();
                            CustomCrafting.getConfigHandler().getConfig().save();
                            CustomCrafting.getRecipeHandler().onSave();
                            CustomCrafting.getConfigHandler().load();
                            InventoryHandler invHandler = new InventoryHandler(api);
                            invHandler.init();
                            CustomCrafting.getApi().sendPlayerMessage(p, "§aReload complete! Reloaded GUIs and languages");
                        }
                        break;
                    case "knowledge":
                        if (ChatUtils.checkPerm(p, "customcrafting.cmd.knowledge")) {
                            invAPI.openCluster(p, "recipe_book");
                        }
                        break;
                    case "give":
                        //   /cc give <player> <namespace:key> [amount]
                        if (ChatUtils.checkPerm(p, "customcrafting.cmd.give")) {
                            if (args.length >= 3) {
                                Player target = Bukkit.getPlayer(args[1]);
                                if (target == null) {
                                    api.sendPlayerMessage(p, "$commands.give.player_offline$", new String[]{"%PLAYER%", args[1]});
                                    return true;
                                }
                                String namespacekey = args[2];
                                int amount = 1;
                                if (args.length > 3) {
                                    try {
                                        amount = Integer.parseInt(args[3]);
                                    } catch (NumberFormatException ex) {
                                        api.sendPlayerMessage(p, "$commands.give.invalid_amount$");
                                    }
                                }
                                CustomItem customItem = CustomItems.getCustomItem(namespacekey);
                                if (customItem != null) {
                                    if (InventoryUtils.hasInventorySpace(target, customItem)) {
                                        ItemStack itemStack = customItem.getItemStack();
                                        itemStack.setAmount(amount);
                                        target.getInventory().addItem(itemStack);
                                        if (amount > 1) {
                                            api.sendPlayerMessage(p, "$commands.give.success_amount$", new String[]{"%PLAYER%", args[1]}, new String[]{"%ITEM%", args[2]}, new String[]{"%AMOUNT%", args[3]});
                                        } else {
                                            api.sendPlayerMessage(p, "$commands.give.success$", new String[]{"%PLAYER%", args[1]}, new String[]{"%ITEM%", args[2]});
                                        }
                                    } else {
                                        api.sendPlayerMessage(p, "$commands.give.no_inv_space$");
                                    }
                                } else {
                                    api.sendPlayerMessage(p, "$commands.give.invalid_item$", new String[]{"%ITEM%", args[2]});
                                }
                            }
                        }
                        break;
                    case "debug":
                        if (ChatUtils.checkPerm(p, "customcrafting.cmd.debug")) {
                            CustomCrafting.getConfigHandler().getConfig().set("debug", !api.hasDebuggingMode());
                            api.sendPlayerMessage(p, "Set Debug to: " + api.hasDebuggingMode());
                        }
                        break;
                    case "settings":
                        if (ChatUtils.checkPerm(p, "customcrafting.cmd.settings")) {
                            if (args.length > 2) {
                                switch (args[1]) {
                                    case "pretty_printing":
                                        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
                                            CustomCrafting.getConfigHandler().getConfig().setPrettyPrinting(Boolean.valueOf(args[2].toLowerCase(Locale.ROOT)));
                                            api.sendPlayerMessage(p, "&aSet &epretty printing &ato &e" + args[2].toLowerCase(Locale.ROOT));
                                        }
                                        break;
                                }
                            }
                        }
                        break;
                    case "database":
                        if (ChatUtils.checkPerm(p, "customcrafting.cmd.database")) {
                            if (args.length >= 2) {
                                switch (args[1]) {
                                    case "export_data":
                                        if (CustomCrafting.hasDataBaseHandler()) {
                                            api.sendPlayerMessage(p, "Exporting json configs to Database.");
                                            Thread thread = new Thread(() -> CustomCrafting.getRecipeHandler().migrateConfigsToDB(CustomCrafting.getDataBaseHandler()));
                                            thread.run();
                                        } else {
                                            api.sendPlayerMessage(p, "&4No Database found!");
                                        }
                                        break;
                                }
                            }
                        }
                        break;
                    case "hide_ads":
                        if (ChatUtils.checkPerm(p, "customcrafting.cmd.hide_ads")) {
                            CustomCrafting.getConfigHandler().getConfig().setHideAds(!CustomCrafting.getConfigHandler().getConfig().hideAds());
                            api.sendPlayerMessage(p, "Set Hide Ads to: " + CustomCrafting.getConfigHandler().getConfig().hideAds());
                        }
                        break;
                }
            }
        } else {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("give")) {
                    //   /cc give <player> <namespace:key> [amount]
                    if (args.length >= 3) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            api.sendConsoleMessage("$commands.give.player_offline$", args[1]);
                            return true;
                        }
                        String namespacekey = args[2];
                        int amount = 1;
                        if (args.length > 3) {
                            try {
                                amount = Integer.parseInt(args[3]);
                            } catch (NumberFormatException ex) {
                                api.sendConsoleMessage("$commands.give.invalid_amount$");
                            }
                        }
                        CustomItem customItem = CustomItems.getCustomItem(namespacekey);
                        if (customItem != null) {
                            if (InventoryUtils.hasInventorySpace(target, customItem)) {
                                ItemStack itemStack = new ItemStack(customItem);
                                itemStack.setAmount(amount);
                                target.getInventory().addItem(itemStack);
                                if (amount > 1) {
                                    api.sendConsoleMessage("$commands.give.success_amount$", args[3], args[2], args[1]);
                                } else {
                                    api.sendConsoleMessage("$commands.give.success$", args[2], args[1]);
                                }
                            } else {
                                api.sendConsoleMessage("$commands.give.no_inv_space$");
                            }
                        } else {
                            api.sendConsoleMessage("$commands.give.invalid_item$", args[2]);
                        }
                    }

                } else if (args[0].equalsIgnoreCase("lockdown")) {
                    CustomCrafting.getConfigHandler().getConfig().toggleLockDown();
                    if (CustomCrafting.getConfigHandler().getConfig().isLockedDown()) {
                        api.sendConsoleMessage("$commands.lockdown.enabled$");
                    } else {
                        api.sendConsoleMessage("$commands.lockdown.disabled$");
                    }
                }
            }
        }
        return true;
    }

    public void openGUI(Player p, InventoryAPI invAPI) {
        if (ChatUtils.checkPerm(p, "customcrafting.cmd.studio", false)) {
            if (!invAPI.getGuiHandler(p).getCurrentGuiCluster().isEmpty() && !invAPI.getGuiHandler(p).getCurrentGuiCluster().equals("recipe_book") && !invAPI.getGuiHandler(p).getCurrentGuiCluster().equals("crafting")) {
                invAPI.getGuiHandler(p).openCluster();
            } else {
                invAPI.openCluster(p, "none");
            }
        }
    }

    public void printInfo(Player p) {
        WolfyUtilities api = CustomCrafting.getApi();
        api.sendPlayerMessage(p, "~*~*~*~*&8[&3&lCustomCrafting&8]&7~*~*~*~*~");
        api.sendPlayerMessage(p, "");
        api.sendPlayerMessage(p, "      &n     by &b&n&lWolfyScript&7&n      ");
        api.sendPlayerMessage(p, "        ------------------");
        api.sendPlayerMessage(p, "");
        api.sendPlayerMessage(p, "             &nVersion:&r&b " + CustomCrafting.getInst().getDescription().getVersion());
        api.sendPlayerMessage(p, "");
        api.sendPlayerMessage(p, "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");
        api.sendPlayerMessage(p, "$msg.commands.info$");
    }

    public void printHelp(Player p) {
        WolfyUtilities api = CustomCrafting.getApi();
        api.sendPlayerMessage(p, "~*~*~*~*&8[&3&lCustomCrafting&8]&7~*~*~*~*~");
        Language lang = api.getLanguageAPI().getActiveLanguage();
        List<String> help = lang.replaceKey("commands.help");
        for (String line : help) {
            api.sendPlayerMessage(p, line);
        }
        api.sendPlayerMessage(p, "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");
    }

    private final List<String> COMMANDS = Arrays.asList("help", "clear", "info", "studio", "give", "lockdown", "darkmode", "knowledge", "settings", "database", "reload", "addAdvWorkbench");
    private final List<String> SETTINGS = Arrays.asList("pretty_printing", "advanced_workbench");
    private final List<String> DATABASE = Arrays.asList("export_data");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
        List<String> results = new ArrayList<>();
        if (strings.length > 1) {
            if (strings[0].equalsIgnoreCase("give")) {
                switch (strings.length) {
                    case 2:
                        //Player completion
                        ArrayList<String> players = new ArrayList<>();
                        Bukkit.getOnlinePlayers().forEach(o -> players.add(o.getName()));
                        StringUtil.copyPartialMatches(strings[1], players, results);
                        break;
                    case 3:
                        //Item completion
                        List<String> items = new ArrayList<>();
                        CustomItems.getCustomItems().forEach(customItem -> items.add(customItem.getId()));
                        StringUtil.copyPartialMatches(strings[2], items, results);
                        break;
                }
            } else if (strings[0].equalsIgnoreCase("settings")) {
                if (strings.length == 2) {
                    StringUtil.copyPartialMatches(strings[1], SETTINGS, results);
                } else if (strings.length == 3) {
                    switch (strings[1]) {
                        case "advanced_workbench":
                        case "pretty_printing":
                            StringUtil.copyPartialMatches(strings[2], Arrays.asList("true", "false"), results);
                            break;
                    }
                }
            } else if (strings[0].equalsIgnoreCase("database")) {
                if (strings.length == 2) {
                    StringUtil.copyPartialMatches(strings[1], DATABASE, results);
                }
            }
        } else {
            StringUtil.copyPartialMatches(strings[0], COMMANDS, results);
        }

        Collections.sort(results);
        return results;
    }
}
