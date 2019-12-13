package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.chat.ClickEvent;
import me.wolfyscript.utilities.api.utils.chat.HoverEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class ChatUtils {

    private static WolfyUtilities api = CustomCrafting.getApi();

    public static boolean checkPerm(CommandSender sender, String perm) {
        return checkPerm(sender, perm, true);
    }

    public static boolean checkPerm(CommandSender sender, String perm, boolean sendMessage) {
        if (WolfyUtilities.hasPermission(sender, perm)) {
            return true;
        }
        if (sendMessage) {
            if (sender instanceof Player) {
                api.sendPlayerMessage((Player) sender, "$msg.denied_perm$", new String[]{"%PERM%", perm});
            } else {
                sender.sendMessage(api.getCONSOLE_PREFIX() + api.getLanguageAPI().getActiveLanguage().replaceKeys("$msg.denied_perm$").replace("%PERM%", perm).replace("&", "§"));
            }
        }
        return false;
    }

    public static void sendRecipeListExpanded(Player player) {
        PlayerCache cache = CustomCrafting.getPlayerCache(player);
        for (int i = 0; i < 20; i++) {
            player.sendMessage(" ");
        }
        api.sendActionMessage(player, new ClickData("&3« Back", (wolfyUtilities, player1) -> wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster(), true));
        api.sendPlayerMessage(player, "&7----------------------------------------------------------");
        api.sendActionMessage(player, new ClickData("§7[§c-§7]", (wolfyUtilities1, p) -> {
            for (int i = 0; i < 20; i++) {
                player.sendMessage(" ");
            }
            api.sendActionMessage(player, new ClickData("&3« Back", (wolfyUtilities, player1) -> wolfyUtilities.getInventoryAPI().getGuiHandler(player1).openCluster()));
            api.sendActionMessage(p, new ClickData("§7[§a+§7]", (wolfyUtilities, player1) -> sendRecipeListExpanded(player1), true), new ClickData(" Recipe List", null));
            api.sendPlayerMessage(player, "none", "recipe_editor","input");
        }, true), new ClickData(" Recipes:", null));

        ArrayList<CustomRecipe> customRecipes = new ArrayList<>();
        switch (cache.getSetting()) {
            case WORKBENCH:
                customRecipes.addAll(CustomCrafting.getRecipeHandler().getAdvancedCraftingRecipes());
                break;
            case ELITE_WORKBENCH:
                customRecipes.addAll(CustomCrafting.getRecipeHandler().getEliteCraftingRecipes());
                break;
            case FURNACE:
                customRecipes.addAll(CustomCrafting.getRecipeHandler().getFurnaceRecipes());
                break;
            case ANVIL:
                customRecipes.addAll(CustomCrafting.getRecipeHandler().getAnvilRecipes());
                break;
            case STONECUTTER:
                customRecipes.addAll(CustomCrafting.getRecipeHandler().getStonecutterRecipes());
                break;
            case SMOKER:
                customRecipes.addAll(CustomCrafting.getRecipeHandler().getSmokerRecipes());
                break;
            case BLAST_FURNACE:
                customRecipes.addAll(CustomCrafting.getRecipeHandler().getBlastRecipes());
                break;
            case CAMPFIRE:
                customRecipes.addAll(CustomCrafting.getRecipeHandler().getCampfireRecipes());
                break;
            case CAULDRON:
                customRecipes.addAll(CustomCrafting.getRecipeHandler().getCauldronRecipes());
        }

        int currentPage = cache.getChatLists().getCurrentPageRecipes();
        int maxPages = ((customRecipes.size() % 16) > 0 ? 1 : 0) + customRecipes.size() / 16;

        for (int i = (currentPage - 1) * 16; i < (currentPage - 1) * 16 + 16 && i < customRecipes.size(); i++) {
            CustomRecipe recipe = customRecipes.get(i);
            api.sendActionMessage(player, new ClickData(" - ", null), new ClickData(recipe.getId(), null, new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, recipe.getId().split(":")[0] + " " + recipe.getId().split(":")[1]), new HoverEvent(recipe.getResult())));
        }

        api.sendActionMessage(player, new ClickData("§7[§6« previous§7]", (wolfyUtilities1, p) -> {
            if (currentPage > 1) {
                cache.getChatLists().setCurrentPageRecipes(cache.getChatLists().getCurrentPageRecipes() - 1);
            }
            sendRecipeListExpanded(p);
        }), new ClickData("  §a" + currentPage + "§7/§6" + maxPages + "  ", null), new ClickData("§7[§6next »§7]", (wolfyUtilities1, p) -> {
            if (currentPage < maxPages) {
                cache.getChatLists().setCurrentPageRecipes(cache.getChatLists().getCurrentPageRecipes() + 1);
            }
            sendRecipeListExpanded(p);
        }));
        api.sendPlayerMessage(player, "none", "recipe_editor", "input");
    }

    public static void sendLoreManager(Player player) {
        ItemMeta itemMeta = CustomCrafting.getPlayerCache(player).getItems().getItem().getItemMeta();
        for (int i = 0; i < 15; i++) {
            player.sendMessage("");
        }
        api.sendPlayerMessage(player, "-------------------[&cRemove Lore&7]------------------");
        api.sendPlayerMessage(player, "");
        List<String> lore;
        if (itemMeta != null && itemMeta.hasLore()) {
            lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();

            int i = 0;
            for (String line : lore) {
                int finalI = i;
                api.sendActionMessage(player, new ClickData("§7[§4-§7] ", (wolfyUtilities, player1) -> {
                    lore.remove(finalI);
                    itemMeta.setLore(lore);
                    CustomCrafting.getPlayerCache(player).getItems().getItem().setItemMeta(itemMeta);
                    sendLoreManager(player1);
                }, true), new ClickData("" + line, null));
                i++;
            }
        } else {
            api.sendPlayerMessage(player, "&l&cNo Lore set yet!");
        }
        api.sendPlayerMessage(player, "");
        api.sendPlayerMessage(player, "-------------------------------------------------");
        api.sendActionMessage(player, new ClickData("                        §7[§3Back to ItemCreator§7]", (wolfyUtilities, player1) -> api.getInventoryAPI().getGuiHandler(player1).openCluster(), true));
    }

    public static void sendAttributeModifierManager(Player player) {
        PlayerCache cache = CustomCrafting.getPlayerCache(player);
        Items items = cache.getItems();
        ItemMeta itemMeta = items.getItem().getItemMeta();
        for (int i = 0; i < 15; i++) {
            player.sendMessage("");
        }
        api.sendPlayerMessage(player, "-----------------[&cRemove Modifier&7]-----------------");
        api.sendPlayerMessage(player, "");
        if (itemMeta.hasAttributeModifiers()) {
            Collection<AttributeModifier> modifiers = itemMeta.getAttributeModifiers(Attribute.valueOf(cache.getSubSetting().substring("attribute.".length()).toUpperCase(Locale.ROOT)));
            if (modifiers != null) {
                api.sendPlayerMessage(player, "        §e§oName   §b§oAmount  §6§oEquipment-Slot  §3§oMode  §7§oUUID");
                api.sendPlayerMessage(player, "");
                for (AttributeModifier modifier : modifiers) {
                    api.sendActionMessage(player,
                            new ClickData("§7[§c-§7] ", (wolfyUtilities, player1) -> {
                                itemMeta.removeAttributeModifier(Attribute.valueOf(CustomCrafting.getPlayerCache(player1).getSubSetting().substring("attribute.".length()).toUpperCase(Locale.ROOT)), modifier);
                                CustomCrafting.getPlayerCache(player).getItems().getItem().setItemMeta(itemMeta);
                                sendAttributeModifierManager(player1);
                            }, new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, "§c" + modifier.getName())),
                            new ClickData("§e" + modifier.getName() + "  §b" + modifier.getAmount() + "  §6" + (modifier.getSlot() == null ? "ANY" : modifier.getSlot()) + "  §3" + modifier.getOperation(), null),
                            new ClickData("  ", null),
                            new ClickData("§7[UUID]", null, new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, "§7[§3Click to copy§7]\n" + modifier.getUniqueId()), new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, "" + modifier.getUniqueId()))
                    );
                }
            } else {
                api.sendPlayerMessage(player, "&cNo attributes set yet!");
            }
        }
        api.sendPlayerMessage(player, "");
        api.sendPlayerMessage(player, "-------------------------------------------------");
        api.sendActionMessage(player, new ClickData("                     §7[§3Back to ItemCreator§7]", (wolfyUtilities, player1) -> {
            for (int i = 0; i < 15; i++) {
                player.sendMessage("");
            }
            api.getInventoryAPI().getGuiHandler(player1).openCluster();
        }, true));
    }


    public static void sendRecipeItemLoadingError(String namespace, String key, String type, Exception ex) {
        api.sendConsoleMessage("-------------------------------------------------");
        api.sendConsoleMessage("Error loading Contents for: " + namespace + ":" + key);
        api.sendConsoleMessage("    Type: " + type);
        api.sendConsoleMessage("    Message: " + ex.getMessage());
        if (ex.getCause() != null) {
            api.sendConsoleMessage("    Cause: " + ex.getCause().getMessage());
        }
        api.sendConsoleMessage("You should check the config for empty settings ");
        api.sendConsoleMessage("e.g. No set Result or Source Item!");
        api.sendConsoleMessage("------------------[StackTrace]-------------------");
        ex.printStackTrace();
        if (ex.getCause() != null) {
            api.sendConsoleMessage("Caused StackTrace: ");
            ex.getCause().printStackTrace();
        }
        api.sendConsoleMessage("------------------[StackTrace]-------------------");
    }
}
