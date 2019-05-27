package me.wolfyscript.customcrafting.items;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.items.ItemConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.CookingData;
import me.wolfyscript.customcrafting.data.cache.Stonecutter;
import me.wolfyscript.customcrafting.data.cache.Workbench;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemUtils {

    public static CustomItem getCustomItem(ItemStack itemStack) {
        String id = "";
        ItemStack clearedItem = itemStack.clone();
        if (isIDItem(itemStack) && itemStack.getItemMeta().hasLore()) {
            ItemMeta clearedMeta = clearedItem.getItemMeta();
            List<String> clearedLore = clearedMeta.getLore();
            List<String> lore = itemStack.getItemMeta().getLore();
            for (int i = 0; i < lore.size(); i++) {
                String row = lore.get(i);
                if (row.startsWith("§7[§3§lID_ITEM§r§7]")) {
                    id = lore.get(i + 1).substring("§3".length());
                    clearedLore.remove(i - 1);
                    clearedLore.remove((int) i);
                    clearedLore.remove(row);
                }
            }
            clearedMeta.setLore(clearedLore);
            if (WolfyUtilities.unhideString(clearedMeta.getDisplayName()).contains("%NO_NAME%")) {
                clearedMeta.setDisplayName(null);
            } else {
                clearedMeta.setDisplayName(clearedMeta.getDisplayName().replace(WolfyUtilities.hideString(":id_item"), ""));
            }
            clearedItem.setItemMeta(clearedMeta);
            if (id.isEmpty()) {
                return new CustomItem(clearedItem);
            }
        }
        if (id.isEmpty()) {
            return new CustomItem(itemStack.clone());
        }
        CustomItem customItem = CustomCrafting.getRecipeHandler().getCustomItem(id);
        if(clearedItem.getAmount() != customItem.getAmount()){
            customItem.setAmount(clearedItem.getAmount());
        }
        return customItem;
    }

    public static boolean isIDItem(ItemStack itemStack) {
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            String name = WolfyUtilities.unhideString(itemStack.getItemMeta().getDisplayName());
            return name.endsWith(":id_item");
        }
        return false;
    }

    public static void saveItem(PlayerCache cache, String id, CustomItem customItem) {
        ItemConfig config = new ItemConfig(CustomCrafting.getApi().getConfigAPI(), id.split(":")[0], id.split(":")[1]);
        config.setCustomItem(customItem);
        if (CustomCrafting.getRecipeHandler().getCustomItem(id) != null) {
            CustomCrafting.getRecipeHandler().removeCustomItem(id);
        }
        CustomItem customItem1 = new CustomItem(config);
        cache.getItems().setItem(customItem1);
        CustomCrafting.getRecipeHandler().addCustomItem(customItem1);
    }

    public static void applyItem(CustomItem item, PlayerCache cache) {
        switch (cache.getSetting()) {
            case CRAFT_RECIPE:
                Workbench workbench = cache.getWorkbench();
                if (cache.getItems().getType().equals("result")) {
                    workbench.setResult(item);
                } else {
                    workbench.setIngredient(cache.getItems().getCraftSlot(), item);
                }
                break;
            case STONECUTTER:
                Stonecutter stonecutter = cache.getStonecutter();
                if (cache.getItems().getType().equals("result")) {
                    stonecutter.setResult(item);
                } else {
                    stonecutter.setSource(item);
                }
                break;
            case CAMPFIRE:
            case SMOKER:
            case BLAST_FURNACE:
            case FURNACE_RECIPE:
                CookingData furnace = cache.getCookingData();
                if (cache.getItems().getType().equals("result")) {
                    furnace.setResult(item);
                } else {
                    furnace.setSource(item);
                }

        }
    }

    public static boolean isEmpty(List<CustomItem> list) {
        for (CustomItem customItem : list) {
            if (!customItem.getType().equals(Material.AIR)) {
                return false;
            }
        }
        return true;
    }

    public static int getInventorySpace(Player p, ItemStack item) {
        return getInventorySpace(p.getInventory(), item);
    }

    public static int getInventorySpace(Inventory inventory, ItemStack item) {
        int free = 0;
        for (ItemStack i : inventory.getStorageContents()) {
            if (i == null || i.getType().equals(Material.AIR)) {
                free += item.getMaxStackSize();
            } else if (i.isSimilar(item)) {
                free += item.getMaxStackSize() - i.getAmount();
            }
        }
        return free;
    }

    public static boolean hasInventorySpace(Inventory inventory, ItemStack itemStack){
        return getInventorySpace(inventory, itemStack) >= itemStack.getAmount();
    }

    public static boolean hasInventorySpace(Player p, ItemStack item) {
        return getInventorySpace(p, item) >= item.getAmount();
    }

    public static boolean hasEmptySpaces(Player p, int count) {
        int empty = 0;
        for (ItemStack i : p.getInventory()) {
            if (i == null) {
                empty++;
            }
        }
        return empty >= count;
    }

}
