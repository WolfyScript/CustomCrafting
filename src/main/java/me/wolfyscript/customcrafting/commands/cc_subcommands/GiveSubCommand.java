package me.wolfyscript.customcrafting.commands.cc_subcommands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class GiveSubCommand extends AbstractSubCommand {

    public GiveSubCommand(CustomCrafting customCrafting) {
        super("give", new ArrayList<>(), customCrafting);
    }

    private static final List<String> NUMBERS = new ArrayList<>();

    static {
        for (int i = 0; i < 65; i++) {
            NUMBERS.add(String.valueOf(i));
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        WolfyUtilities api = customCrafting.getApi();
        if (args.length >= 2 && ChatUtils.checkPerm(sender, "customcrafting.cmd.give")) {
            var target = Bukkit.getPlayer(args[0]);
            Pair<String, String> playerValue = new Pair<>("%PLAYER%", args[0]);
            if (target == null) {
                if (sender instanceof Player) {
                    api.getChat().sendMessage((Player) sender, "$commands.give.player_offline$", playerValue);
                } else {
                    api.getConsole().log(Level.INFO, "$commands.give.player_offline$", args[0]);
                }
                return true;
            }
            var namespacedKey = NamespacedKey.of(args[1]);
            Pair<String, String> itemValue = new Pair<>("%ITEM%", args[1]);
            //not required values ---------------------------------------
            var amount = 1;
            if (args.length > 2) {
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException ex) {
                    if (sender instanceof Player) {
                        api.getChat().sendMessage((Player) sender, "$commands.give.invalid_amount$");
                    } else {
                        api.getConsole().info("$commands.give.invalid_amount$");
                    }
                    return true;
                }
            }
            Pair<String, String> amountValue = new Pair<>("%AMOUNT%", String.valueOf(amount));
            var dropItems = true;
            if (args.length > 3) {
                dropItems = Boolean.parseBoolean(args[3]);
            }
            //------------------------------------------------------------
            if (namespacedKey != null) {
                var customItem = Registry.CUSTOM_ITEMS.get(NamespacedKeyUtils.fromInternal(namespacedKey));
                if (customItem != null) {
                    var itemStack = customItem.create(amount);
                    if (InventoryUtils.hasInventorySpace(target, itemStack)) {
                        target.getInventory().addItem(itemStack);
                    } else if (dropItems && target.getLocation().getWorld() != null) {
                        target.getLocation().getWorld().dropItem(target.getLocation(), itemStack);
                    } else {
                        if (sender instanceof Player) {
                            api.getChat().sendMessage((Player) sender, "$commands.give.no_inv_space$", itemValue);
                        } else {
                            api.getConsole().log(Level.INFO, "$commands.give.no_inv_space$", args[1]);
                        }
                        return true;
                    }
                    if (amount > 1) {
                        if (sender instanceof Player) {
                            api.getChat().sendMessage((Player) sender, "$commands.give.success_amount$", amountValue, itemValue, playerValue);
                        } else {
                            api.getConsole().log(Level.INFO, "$commands.give.success_amount$", args[2], args[1], args[0]);
                        }
                    } else {
                        if (sender instanceof Player) {
                            api.getChat().sendMessage((Player) sender, "$commands.give.success$", playerValue, itemValue);
                        } else {
                            api.getConsole().log(Level.INFO, "$commands.give.success$", args[1], args[0]);
                        }
                    }
                    return true;
                }
            }
            if (sender instanceof Player) {
                api.getChat().sendMessage((Player) sender, "$commands.give.invalid_item$", itemValue);
            } else {
                api.getConsole().log(Level.INFO, "$commands.give.invalid_item$", args[1]);
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] strings) {
        List<String> results = new ArrayList<>();
        if (strings.length > 0) {
            StringUtil.copyPartialMatches(
                    strings[strings.length - 1],
                    switch (strings.length) {
                        case 1 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(); //Player completion
                        case 2 -> Registry.CUSTOM_ITEMS.keySet().stream().map(namespacedKey -> NamespacedKeyUtils.toInternal(namespacedKey).toString()).toList(); //Item completion
                        case 3 -> NUMBERS;
                        case 4 -> Arrays.asList("true", "false"); //Drop Items
                        default -> new ArrayList<String>();
                    },
                    results
            );
        }
        Collections.sort(results);
        return results;
    }
}
