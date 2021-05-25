package me.wolfyscript.customcrafting.gui.lists.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.ItemCreatorCluster;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.chat.ChatColor;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomItemSelectButton extends ActionButton<CCCache> {

    public CustomItemSelectButton(CustomCrafting customCrafting, NamespacedKey namespacedKey) {
        super("item_" + namespacedKey.toString("__"), new ButtonState<>("custom_item_error", Material.STONE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (!Registry.CUSTOM_ITEMS.has(namespacedKey) || ItemUtils.isAirOrNull(Registry.CUSTOM_ITEMS.get(namespacedKey))) {
                return true;
            }
            WolfyUtilities api = customCrafting.getApi();
            CustomItem customItem = Registry.CUSTOM_ITEMS.get(namespacedKey);
            if (event instanceof InventoryClickEvent) {
                if (((InventoryClickEvent) event).isRightClick()) {
                    if (((InventoryClickEvent) event).isShiftClick()) {
                        api.getChat().sendKey(player, MainCluster.ITEM_LIST, "delete.confirm", new Pair<>("%item%", customItem.getNamespacedKey().toString()));
                        api.getChat().sendActionMessage(player, new ClickData("$inventories.none.item_list.messages.delete.confirmed$", (wolfyUtilities, player1) -> {
                            guiHandler.openCluster();
                            Bukkit.getScheduler().runTaskAsynchronously(customCrafting, () -> ItemLoader.deleteItem(namespacedKey, player));
                        }), new ClickData("$inventories.none.item_list.messages.delete.declined$", (wolfyUtilities, player2) -> guiHandler.openCluster()));
                    } else if (customItem != null) {
                        items.setItem(items.isRecipeItem(), customItem.clone());
                        api.getInventoryAPI().getGuiWindow(RecipeCreatorCluster.ITEM_EDITOR).sendMessage(player, "item_editable");
                        guiHandler.openWindow(ItemCreatorCluster.MAIN_MENU);
                    }
                } else if (((InventoryClickEvent) event).isLeftClick()) {
                    if (cache.getSetting().equals(Setting.RECIPE_CREATOR)) {
                        cache.applyItem(customItem);
                        api.getInventoryAPI().getGuiWindow(RecipeCreatorCluster.ITEM_EDITOR).sendMessage(player, "item_applied");
                        List<? extends GuiWindow<?>> history = guiHandler.getClusterHistory().get(guiHandler.getCluster());
                        history.remove(history.size() - 1);
                        guiHandler.openCluster(RecipeCreatorCluster.KEY);
                    } else if (ChatUtils.checkPerm(player, "customcrafting.cmd.give")) {
                        ItemStack itemStack = customItem.create();
                        int amount = ((InventoryClickEvent) event).isShiftClick() ? itemStack.getMaxStackSize() : 1;
                        itemStack.setAmount(amount);
                        if (InventoryUtils.hasInventorySpace(player, itemStack)) {
                            player.getInventory().addItem(itemStack);
                        } else {
                            player.getLocation().getWorld().dropItem(player.getLocation(), itemStack);
                        }
                        if (((InventoryClickEvent) event).isShiftClick()) {
                            api.getChat().sendMessage(player, "$commands.give.success_amount$", new Pair<>("%PLAYER%", player.getDisplayName()), new Pair<>("%ITEM%", namespacedKey.toString()), new Pair<>("%AMOUNT%", String.valueOf(amount)));
                        } else {
                            api.getChat().sendMessage(player, "$commands.give.success$", new Pair<>("%PLAYER%", player.getDisplayName()), new Pair<>("%ITEM%", namespacedKey.toString()));
                        }
                    }
                }
            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            CustomItem customItem = Registry.CUSTOM_ITEMS.get(namespacedKey);
            if (!ItemUtils.isAirOrNull(customItem)) {
                ItemBuilder itemB = new ItemBuilder(customItem.create());
                itemB.addLoreLine("");
                itemB.addLoreLine("§8" + namespacedKey);
                CustomCrafting.inst().getApi().getLanguageAPI().replaceKey("inventories.none.item_list.items.custom_item.lore").forEach(s -> itemB.addLoreLine(ChatColor.convert(s)));
                return itemB.create();
            }
            ItemBuilder itemB = new ItemBuilder(itemStack);
            itemB.addLoreLine("");
            itemB.addLoreLine("§8" + namespacedKey);
            itemB.addLoreLine("§c");
            return itemB.create();
        }));
    }

}
