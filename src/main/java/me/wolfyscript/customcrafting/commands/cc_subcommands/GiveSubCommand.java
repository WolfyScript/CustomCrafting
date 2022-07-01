/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.commands.cc_subcommands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
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
import java.util.stream.Collectors;

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
        if (ChatUtils.checkPerm(sender, "customcrafting.cmd.give")) {
            if (args.length >= 2) {
                String giveTarget = args[0];
                Player target = switch (giveTarget) {
                    case "@s" -> sender instanceof Player player ? player : null;
                    default -> Bukkit.getPlayer(giveTarget);
                };
                if (target == null) {
                    Pair<String, String> playerValue = new Pair<>("%target%", giveTarget);
                    if (sender instanceof Player) {
                        api.getChat().sendMessage((Player) sender, "$commands.give.invalid_target$", playerValue);
                    } else {
                        api.getConsole().log(Level.INFO, "$commands.give.invalid_target$", giveTarget);
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
                    var customItem = api.getRegistries().getCustomItems().get(NamespacedKeyUtils.fromInternal(namespacedKey));
                    if (customItem != null) {
                        Pair<String, String> playerValue = new Pair<>("%PLAYER%", target.getDisplayName());
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
                                api.getConsole().log(Level.INFO, "$commands.give.success_amount$", args[2], args[1], target.getDisplayName());
                            }
                        } else {
                            if (sender instanceof Player) {
                                api.getChat().sendMessage((Player) sender, "$commands.give.success$", playerValue, itemValue);
                            } else {
                                api.getConsole().log(Level.INFO, "$commands.give.success$", args[1], target.getDisplayName());
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
            } else {
                if (sender instanceof Player) {
                    api.getChat().sendMessage((Player) sender, "$commands.give.invalid_usage$");
                }
            }

        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] strings) {
        List<String> results = new ArrayList<>();
        if (strings.length > 0) {
            StringUtil.copyPartialMatches(
                    strings[strings.length - 1],
                    switch (strings.length) {
                        case 1 -> {
                            List<String> players = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()); //Player completion
                            if (sender instanceof Player) {
                                players.add("@s");
                            }
                            yield players;
                        }
                        case 2 ->
                                api.getRegistries().getCustomItems().keySet().stream().map(namespacedKey -> NamespacedKeyUtils.toInternal(namespacedKey).toString()).toList(); //Item completion
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
