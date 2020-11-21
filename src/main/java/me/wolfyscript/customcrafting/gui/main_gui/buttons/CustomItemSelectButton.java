package me.wolfyscript.customcrafting.gui.main_gui.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomItemSelectButton extends ActionButton {

    public CustomItemSelectButton(NamespacedKey namespacedKey) {
        super("item_" + namespacedKey.toString().replace(":", "__"), new ButtonState("custom_item_error", Material.STONE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (!CustomItems.hasCustomItem(namespacedKey) || ItemUtils.isAirOrNull(CustomItems.getCustomItem(namespacedKey))) {
                return true;
            }
            WolfyUtilities api = CustomCrafting.getApi();

            CustomItem customItem = CustomItems.getCustomItem(namespacedKey);
            if (event.isRightClick()) {
                if (items.isRecipeItem()) {
                    if (items.isSaved()) {
                        items.setItem(customItem);
                    }
                } else {
                    items.setItem(false, customItem);
                    api.getInventoryAPI().getGuiWindow("none", "item_editor").sendMessage(player, "item_editable");
                }
                guiHandler.changeToInv("item_creator", "main_menu");
            } else if (event.isLeftClick()) {
                if (items.isRecipeItem()) {
                    cache.applyItem(customItem);
                    api.getInventoryAPI().getGuiWindow("none", "item_editor").sendMessage(player, "item_applied");
                    if (!cache.getSetting().equals(Setting.ITEMS)) {
                        guiHandler.openCluster("recipe_creator");
                    } else {
                        guiHandler.openPreviousInv();
                    }
                    return false;
                } else if (ChatUtils.checkPerm(player, "customcrafting.cmd.give")) {
                    ItemStack itemStack = customItem.create();
                    int amount = event.isShiftClick() ? itemStack.getMaxStackSize() : 1;
                    itemStack.setAmount(amount);
                    if (InventoryUtils.hasInventorySpace(player, itemStack)) {
                        player.getInventory().addItem(itemStack);
                    } else {
                        player.getLocation().getWorld().dropItem(player.getLocation(), itemStack);
                    }
                    if (event.isShiftClick()) {
                        api.sendPlayerMessage(player, "$commands.give.success_amount$", new String[]{"%PLAYER%", player.getDisplayName()}, new String[]{"%ITEM%", namespacedKey.toString()}, new String[]{"%AMOUNT%", String.valueOf(amount)});
                    } else {
                        api.sendPlayerMessage(player, "$commands.give.success$", new String[]{"%PLAYER%", player.getDisplayName()}, new String[]{"%ITEM%", namespacedKey.toString()});
                    }
                }
            }
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            CustomItem customItem = CustomItems.getCustomItems().get(namespacedKey);
            if (!ItemUtils.isAirOrNull(customItem)) {
                ItemBuilder itemB = new ItemBuilder(customItem.create());
                itemB.addLoreLine("");
                itemB.addLoreLine("§8" + namespacedKey.toString());
                CustomCrafting.getApi().getLanguageAPI().replaceKey("inventories.none.item_list.items.custom_item.lore").forEach(s -> itemB.addLoreLine(WolfyUtilities.translateColorCodes(s)));
                return itemB.create();
            }
            ItemBuilder itemB = new ItemBuilder(itemStack);
            itemB.addLoreLine("");
            itemB.addLoreLine("§8" + namespacedKey.toString());
            itemB.addLoreLine("§c");
            return itemB.create();
        }));
    }

}
