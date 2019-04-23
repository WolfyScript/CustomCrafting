package me.wolfyscript.customcrafting.commands;

import com.sun.istack.internal.NotNull;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.items.ItemUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.spigotmc.SpigotCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
                if (args[0].equalsIgnoreCase("studio")) {
                    openGUI(p, invAPI);
                } else if (args[0].equalsIgnoreCase("info")) {
                    if (checkPerm(p, "customcrafting.cmd.info")) {
                        printInfo(p);
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    if (checkPerm(p, "customcrafting.cmd.help")) {
                        printHelp(p);
                    }
                } else if (args[0].equalsIgnoreCase("clear")) {
                    if (checkPerm(p, "customcrafting.cmd.clear")) {
                        CustomCrafting.renewPlayerCache(p);
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (checkPerm(p, "customcrafting.cmd.reload")) {
                        //TODO RELOAD
                        CustomCrafting.getApi().sendPlayerMessage(p, "§cYeah you found it! Unfortunately it's not implemented yet! :(");
                    }
                } else if (args[0].equalsIgnoreCase("knowledge")) {
                    invAPI.openGui(p);
                    invAPI.getGuiHandler(p).changeToInv("recipe_book");
                } else if (args[0].equalsIgnoreCase("give")) {
                    //   /cc give <player> <namespace:key> [amount]
                    if (checkPerm(p, "customcrafting.cmd.give")) {
                        if (args.length >= 3) {

                            Player target = Bukkit.getPlayer(args[1]);
                            if (target == null) {
                                api.sendPlayerMessage(p, "$msg.commands.give.player_offline$", new String[]{"%PLAYER%", args[1]});
                                return true;
                            }
                            String namespacekey = args[2];
                            int amount = 1;
                            if (args.length > 3) {
                                try {
                                    amount = Integer.parseInt(args[3]);
                                } catch (NumberFormatException ex) {
                                    api.sendPlayerMessage(p, "$msg.commands.give.invalid_amount$");
                                }
                            }
                            CustomItem customItem = CustomCrafting.getRecipeHandler().getCustomItem(namespacekey);
                            if (customItem != null) {
                                if (ItemUtils.hasInventorySpace(target, customItem)) {
                                    ItemStack itemStack = new ItemStack(customItem);
                                    itemStack.setAmount(amount);
                                    target.getInventory().addItem(itemStack);
                                    if (amount > 1) {
                                        api.sendPlayerMessage(p, "$msg.commands.give.success_amount$", new String[]{"%PLAYER%", args[1]}, new String[]{"%ITEM%", args[2]}, new String[]{"%AMOUNT%", args[3]});
                                    } else {
                                        api.sendPlayerMessage(p, "$msg.commands.give.success$", new String[]{"%PLAYER%", args[1]}, new String[]{"%ITEM%", args[2]});
                                    }
                                } else {
                                    api.sendPlayerMessage(p, "$msg.commands.give.no_inv_space$");
                                }
                            } else {
                                api.sendPlayerMessage(p, "$msg.commands.give.invalid_item$", new String[]{"%ITEM%", args[2]});
                            }
                        }
                    }
                }
            }
        } else {
            if (args[0].equalsIgnoreCase("give")) {
                //   /cc give <player> <namespace:key> [amount]
                if (args.length >= 3) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        api.sendConsoleMessage("$msg.commands.give.player_offline$", args[1]);
                        return true;
                    }
                    String namespacekey = args[2];
                    int amount = 1;
                    if (args.length > 3) {
                        try {
                            amount = Integer.parseInt(args[3]);
                        } catch (NumberFormatException ex) {
                            api.sendConsoleMessage("$msg.commands.give.invalid_amount$");
                        }
                    }
                    CustomItem customItem = CustomCrafting.getRecipeHandler().getCustomItem(namespacekey);
                    if (customItem != null) {
                        if (ItemUtils.hasInventorySpace(target, customItem)) {
                            ItemStack itemStack = new ItemStack(customItem);
                            itemStack.setAmount(amount);
                            target.getInventory().addItem(itemStack);
                            if (amount > 1) {
                                api.sendConsoleMessage("$msg.commands.give.success_amount$", args[3], args[2], args[1]);
                            } else {
                                api.sendConsoleMessage("$msg.commands.give.success$",args[2], args[1]);
                            }
                        } else {
                            api.sendConsoleMessage("$msg.commands.give.no_inv_space$");
                        }
                    } else {
                        api.sendConsoleMessage("$msg.commands.give.invalid_item$", args[2]);
                    }
                }

            }
        }
        return true;
    }

    public void openGUI(Player p, InventoryAPI invAPI) {
        if (checkPerm(p, "customcrafting.cmd.studio")) {
            invAPI.openGui(p);
            if (invAPI.getGuiHandler(p).getCurrentInv() != null && invAPI.getGuiHandler(p).getCurrentInv().getNamespace().equals("recipe_book")) {
                invAPI.getGuiHandler(p).changeToInv("main_menu");
            }
        }
    }

    public boolean checkPerm(Player player, String perm) {
        WolfyUtilities api = CustomCrafting.getApi();
        if (WolfyUtilities.hasPermission(player, perm)) {
            return true;
        }
        api.sendPlayerMessage(player, "$msg.denied_perm$", new String[]{"%PERM%", perm});
        return false;
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
        List<String> help = lang.replaceKey("msg.commands.help");
        for (String line : help) {
            api.sendPlayerMessage(p, line);
        }
        api.sendPlayerMessage(p, "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~");
    }

    private final List<String> COMMANDS = Arrays.asList("help", "clear", "info", "studio", "give");

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
                        CustomCrafting.getRecipeHandler().getCustomItems().forEach(customItem -> items.add(customItem.getId()));
                        StringUtil.copyPartialMatches(strings[2], items, results);
                        break;
                }
            }
        } else {
            StringUtil.copyPartialMatches(strings[0], COMMANDS, results);
        }

        Collections.sort(results);
        return results;
    }
}
