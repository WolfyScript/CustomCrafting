package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.chat.ClickEvent;
import me.wolfyscript.utilities.api.chat.HoverEvent;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class ChatUtils {

    private static final WolfyUtilities api = CustomCrafting.inst().getApi();
    private final CustomCrafting customCrafting;

    public ChatUtils(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    public static boolean checkPerm(CommandSender sender, String perm) {
        return checkPerm(sender, perm, true);
    }

    public static boolean checkPerm(CommandSender sender, String perm, boolean sendMessage) {
        if (api.getPermissions().hasPermission(sender, perm)) {
            return true;
        }
        if (sendMessage) {
            if (sender instanceof Player player) {
                api.getChat().sendMessage(player, "$msg.denied_perm$", new Pair<>("%PERM%", perm));
            } else {
                api.getConsole().severe(api.getLanguageAPI().replaceKeys("$msg.denied_perm$").replace("%PERM%", perm).replace("&", "§"));
            }
        }
        return false;
    }

    /**
     * @param player The player that inputted the values.
     * @param s      The input as a String
     * @param args   The input as separate arguments (split by spaces)
     * @return The {@link NamespacedKey} used internally in CustomCrafting in the format <b><i>namespace</i>:<i>key</i></b>
     */
    public static NamespacedKey getInternalNamespacedKey(Player player, String s, String[] args) {
        try {
            if (args.length > 1) {
                return new NamespacedKey(args[0], args[1]);
            }
            return s.contains(":") ? NamespacedKey.of(s) : null;
        } catch (IllegalArgumentException e) {
            api.getLanguageAPI().replaceKey("msg.player.invalid_namespacedkey").forEach(s1 -> api.getChat().sendMessage(player, s1));
        }
        return null;
    }

    /**
     * @param player The player that inputted the values.
     * @param s      The input as a String
     * @param args   The input as separate arguments (split by spaces)
     * @return The {@link NamespacedKey} used in WolfyUtilities, to identify which plugin it's from, in the format <b>customcrafting:<i>namespace</i>/<i>key</i></b>
     */
    public static NamespacedKey getNamespacedKey(Player player, String s, String[] args) {
        return NamespacedKeyUtils.fromInternal(getInternalNamespacedKey(player, s, args));
    }

    public static void sendCategoryDescription(Player player) {
        List<String> description = ((CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache()).getRecipeBookEditor().getCategorySetting().getDescription();
        for (int i = 0; i < 15; i++) {
            player.sendMessage("");
        }
        api.getChat().sendMessage(player, "------------------[&cEdit Description&7]-----------------");
        api.getChat().sendMessage(player, "");
        if (!description.isEmpty()) {
            int i = 0;
            for (String line : description) {
                int finalI = i;
                api.getChat().sendActionMessage(player, new ClickData("§7[§4-§7] ", (wolfyUtilities, player1) -> {
                    description.remove(finalI);
                    sendCategoryDescription(player1);
                }, true), new ClickData(line, null));
                i++;
            }
        } else {
            api.getChat().sendMessage(player, "&l&cNo Description set yet!");
        }
        api.getChat().sendMessage(player, "");
        api.getChat().sendMessage(player, "-------------------------------------------------");
        api.getChat().sendActionMessage(player, new ClickData("                    §7[§3Back to Recipe Book Editor§7]", (wolfyUtilities, player1) -> api.getInventoryAPI().getGuiHandler(player1).openCluster(), true));
    }

    public static void sendLoreManager(Player player) {
        var itemMeta = ((CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache()).getItems().getItem().getItemMeta();
        for (int i = 0; i < 15; i++) {
            player.sendMessage("");
        }
        api.getChat().sendMessage(player, "-------------------[&cRemove Lore&7]------------------");
        api.getChat().sendMessage(player, "");
        List<String> lore;
        if (itemMeta != null && itemMeta.hasLore()) {
            lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();

            int i = 0;
            for (String line : lore) {
                int finalI = i;
                api.getChat().sendActionMessage(player, new ClickData("§7[§4-§7] ", (wolfyUtilities, player1) -> {
                    lore.remove(finalI);
                    itemMeta.setLore(lore);
                    ((CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                    sendLoreManager(player1);
                }, true), new ClickData(line, null));
                i++;
            }
        } else {
            api.getChat().sendMessage(player, "&l&cNo Lore set yet!");
        }
        api.getChat().sendMessage(player, "");
        api.getChat().sendMessage(player, "-------------------------------------------------");
        api.getChat().sendActionMessage(player, new ClickData("                        §7[§3Back to ItemCreator§7]", (wolfyUtilities, player1) -> api.getInventoryAPI().getGuiHandler(player1).openCluster(), true));
    }

    public static void sendAttributeModifierManager(Player player) {
        CCCache cache = ((CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache());
        var items = cache.getItems();
        var itemMeta = items.getItem().getItemMeta();
        for (int i = 0; i < 15; i++) {
            player.sendMessage("");
        }
        api.getChat().sendMessage(player, "-----------------[&cRemove Modifier&7]-----------------");
        api.getChat().sendMessage(player, "");
        if (itemMeta.hasAttributeModifiers()) {
            Collection<AttributeModifier> modifiers = itemMeta.getAttributeModifiers(Attribute.valueOf(cache.getSubSetting().substring("attribute.".length()).toUpperCase(Locale.ROOT)));
            if (modifiers != null) {
                api.getChat().sendMessage(player, "        §e§oName   §b§oAmount  §6§oEquipment-Slot  §3§oMode  §7§oUUID");
                api.getChat().sendMessage(player, "");
                for (AttributeModifier modifier : modifiers) {
                    api.getChat().sendActionMessage(player,
                            new ClickData("§7[§c-§7] ", (wolfyUtilities, player1) -> {
                                CCCache cache1 = (CCCache) api.getInventoryAPI().getGuiHandler(player).getCustomCache();
                                itemMeta.removeAttributeModifier(Attribute.valueOf(cache1.getSubSetting().substring("attribute.".length()).toUpperCase(Locale.ROOT)), modifier);
                                cache1.getItems().getItem().setItemMeta(itemMeta);
                                sendAttributeModifierManager(player1);
                            }, new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, "§c" + modifier.getName())),
                            new ClickData("§e" + modifier.getName() + "  §b" + modifier.getAmount() + "  §6" + (modifier.getSlot() == null ? "ANY" : modifier.getSlot()) + "  §3" + modifier.getOperation(), null),
                            new ClickData("  ", null),
                            new ClickData("§7[UUID]", null, new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, "§7[§3Click to copy§7]\n" + modifier.getUniqueId()), new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, "" + modifier.getUniqueId()))
                    );
                }
            } else {
                api.getChat().sendMessage(player, "&cNo attributes set yet!");
            }
        }
        api.getChat().sendMessage(player, "");
        api.getChat().sendMessage(player, "-------------------------------------------------");
        api.getChat().sendActionMessage(player, new ClickData("                     §7[§3Back to ItemCreator§7]", (wolfyUtilities, player1) -> {
            for (int i = 0; i < 15; i++) {
                player.sendMessage("");
            }
            api.getInventoryAPI().getGuiHandler(player1).openCluster();
        }, true));
    }

    public static void sendRecipeItemLoadingError(String namespace, String key, String type, Exception ex) {
        api.getConsole().severe("-------------------------------------------------");
        api.getConsole().severe("Error loading Contents for: " + namespace + ":" + key);
        api.getConsole().severe("    Type: " + type);
        if (ex.getMessage() != null) {
            api.getConsole().severe("    Message: " + ex.getMessage());
        }
        if (ex.getCause() != null) {
            api.getConsole().severe("    Cause: " + ex.getCause().getMessage());
        }
        api.getConsole().severe("Please check the config of the recipe.");
        if (CustomCrafting.inst().getConfigHandler().getConfig().isPrintingStacktrace()) {
            api.getConsole().severe("------------------[StackTrace]-------------------");
            ex.printStackTrace();
            if (ex.getCause() != null) {
                api.getConsole().severe("Caused StackTrace: ");
                ex.getCause().printStackTrace();
            }
        } else {
            api.getConsole().severe("For more info enable stacktraces in the config ('data.print_stacktrace')");
        }
        api.getConsole().severe("-------------------------------------------------");
    }
}
