package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.types.ICraftingRecipe;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Stream;

public class CraftListener implements Listener {

    private final CustomCrafting customCrafting;
    private final CraftManager craftManager;

    public CraftListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.craftManager = customCrafting.getCraftManager();
    }

    @EventHandler
    public void onAdvancedWorkbench(CustomPreCraftEvent event) {
        if (!event.isCancelled() && event.getRecipe().getNamespacedKey().equals(CustomCrafting.ADVANCED_CRAFTING_TABLE) && !customCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCraft(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof CraftingInventory)) return;
        CraftingInventory inventory = (CraftingInventory) event.getClickedInventory();
        if (event.getSlot() == 0) {
            ItemStack resultItem = inventory.getResult();
            ItemStack cursor = event.getCursor();
            if (ItemUtils.isAirOrNull(resultItem) || (!ItemUtils.isAirOrNull(cursor) && !cursor.isSimilar(resultItem) && !event.isShiftClick())) {
                event.setCancelled(true);
                return;
            }
            if (craftManager.has(event.getWhoClicked().getUniqueId())) {
                event.setCancelled(true);
                if (event.isShiftClick() || ItemUtils.isAirOrNull(cursor) || cursor.getAmount() + resultItem.getAmount() <= cursor.getMaxStackSize()) {
                    ItemStack[] matrix = inventory.getMatrix();
                    craftManager.consumeRecipe(resultItem, matrix, event);
                    inventory.setMatrix(matrix);
                    ((Player) event.getWhoClicked()).updateInventory();
                    inventory.setResult(new ItemStack(Material.AIR));
                    callPreCraftEvent(inventory, event);
                }
            }
        } else if ((event.getAction().equals(InventoryAction.PLACE_ALL) || event.getAction().equals(InventoryAction.PLACE_ONE) || event.getAction().equals(InventoryAction.PLACE_SOME)) && inventory.getItem(event.getSlot()) != null) {
            callPreCraftEvent(inventory, event);
        }
    }

    public void callPreCraftEvent(CraftingInventory inventory, InventoryClickEvent event) {
        Bukkit.getScheduler().runTask(customCrafting, () -> {
            PrepareItemCraftEvent event1 = new PrepareItemCraftEvent(inventory, event.getView(), false);
            Bukkit.getPluginManager().callEvent(event1);
            ((Player) event.getWhoClicked()).updateInventory();
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreCraft(PrepareItemCraftEvent e) {
        Player player = (Player) e.getView().getPlayer();
        try {
            DataHandler dataHandler = customCrafting.getDataHandler();
            ItemStack[] matrix = e.getInventory().getMatrix();
            ItemStack result = craftManager.preCheckRecipe(matrix, player, e.getInventory(), false, true);
            if (!ItemUtils.isAirOrNull(result)) {
                e.getInventory().setResult(result);
                return;
            }
            //No valid custom recipes found
            if (!(e.getRecipe() instanceof Keyed)) return;
            //Vanilla Recipe is available.
            //Check for custom recipe that overrides the vanilla recipe
            NamespacedKey namespacedKey = NamespacedKey.fromBukkit(((Keyed) e.getRecipe()).getKey());
            ICraftingRecipe recipe = Registry.RECIPES.getAdvancedCrafting(NamespacedKeyUtils.toInternal(namespacedKey));
            if (dataHandler.getDisabledRecipes().contains(namespacedKey) || recipe != null) {
                //Recipe is disabled or it is a custom recipe!
                e.getInventory().setResult(ItemUtils.AIR);
                return;
            }
            //Check for items that are not allowed in vanilla recipes.
            //If one is found, then cancel the recipe.
            if (Stream.of(matrix).parallel().map(CustomItem::getByItemStack).anyMatch(i -> i != null && i.isBlockVanillaRecipes())) {
                e.getInventory().setResult(ItemUtils.AIR);
            }
            //At this point the vanilla recipe is valid and can be crafted
            player.updateInventory();
        } catch (Exception ex) {
            CustomCrafting.inst().getLogger().severe("-------- WHAT HAPPENED? Please report! --------");
            ex.printStackTrace();
            CustomCrafting.inst().getLogger().severe("-------- WHAT HAPPENED? Please report! --------");
            craftManager.remove(player.getUniqueId());
            e.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onRecipeDiscover(PlayerRecipeDiscoverEvent event) {
        if (event.getRecipe().getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
            event.setCancelled(true);
        }
    }
}
