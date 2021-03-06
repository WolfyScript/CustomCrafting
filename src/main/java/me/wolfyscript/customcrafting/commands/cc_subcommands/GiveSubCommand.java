package me.wolfyscript.customcrafting.commands.cc_subcommands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GiveSubCommand extends AbstractSubCommand {

    public GiveSubCommand(CustomCrafting customCrafting) {
        super("give", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        WolfyUtilities api = CustomCrafting.getApi();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            //give <player> <namespace:key> [amount]
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.give")) {
                if (args.length >= 2) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        api.getChat().sendMessage(p, "$commands.give.player_offline$", new Pair<>("%PLAYER%", args[0]));
                        return true;
                    }
                    int amount = 1;
                    if (args.length > 2) {
                        try {
                            amount = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ex) {
                            api.getChat().sendMessage(p, "$commands.give.invalid_amount$");
                        }
                    }
                    NamespacedKey namespacedKey = NamespacedKeyUtils.fromInternal(NamespacedKey.of(args[1]));
                    CustomItem customItem = Registry.CUSTOM_ITEMS.get(namespacedKey);
                    if (customItem != null) {
                        ItemStack itemStack = customItem.create(amount);
                        if (InventoryUtils.hasInventorySpace(target, itemStack)) {
                            target.getInventory().addItem(itemStack);
                        } else {
                            target.getLocation().getWorld().dropItem(target.getLocation(), itemStack);
                        }
                        if (amount > 1) {
                            api.getChat().sendMessage(p, "$commands.give.success_amount$", new Pair<>("%PLAYER%", args[0]), new Pair<>("%ITEM%", args[1]), new Pair<>("%AMOUNT%", args[2]));
                        } else {
                            api.getChat().sendMessage(p, "$commands.give.success$", new Pair<>("%PLAYER%", args[0]), new Pair<>("%ITEM%", args[1]));
                        }
                    } else {
                        api.getChat().sendMessage(p, "$commands.give.invalid_item$", new Pair<>("%ITEM%", args[1]));
                    }
                }
            }
        } else {
            if (args.length >= 2) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    api.getChat().sendConsoleMessage("$commands.give.player_offline$", args[0]);
                    return true;
                }
                NamespacedKey namespacekey = NamespacedKey.of(args[1]);
                int amount = 1;
                if (args.length > 2) {
                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch (NumberFormatException ex) {
                        api.getChat().sendConsoleMessage("$commands.give.invalid_amount$");
                    }
                }
                CustomItem customItem = Registry.CUSTOM_ITEMS.get(namespacekey);
                if (customItem != null) {
                    if (InventoryUtils.hasInventorySpace(target, customItem.getItemStack())) {
                        ItemStack itemStack = customItem.create(amount);
                        target.getInventory().addItem(itemStack);
                        if (amount > 1) {
                            api.getChat().sendConsoleMessage("$commands.give.success_amount$", args[2], args[1], args[0]);
                        } else {
                            api.getChat().sendConsoleMessage("$commands.give.success$", args[1], args[0]);
                        }
                    } else {
                        api.getChat().sendConsoleMessage("$commands.give.no_inv_space$");
                    }
                } else {
                    api.getChat().sendConsoleMessage("$commands.give.invalid_item$", args[1]);
                }
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] strings) {
        List<String> results = new ArrayList<>();
        if (strings.length > 0) {
            switch (strings.length) {
                case 1:
                    //Player completion
                    StringUtil.copyPartialMatches(strings[0], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), results);
                    break;
                case 2:
                    //Item completion
                    StringUtil.copyPartialMatches(strings[1], Registry.CUSTOM_ITEMS.keySet().stream().map(NamespacedKey::toString).collect(Collectors.toList()), results);
                    break;
            }
        }
        Collections.sort(results);
        return results;
    }
}
