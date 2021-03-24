package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.CraftRecipeMCRegistry;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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
    private final WolfyUtilities api;

    public CraftListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.craftManager = customCrafting.getCraftManager();
        this.api = WolfyUtilities.get(customCrafting);
    }

    @EventHandler
    public void onAdvancedWorkbench(CustomPreCraftEvent event) {
        if (!event.isCancelled() && event.getRecipe().getNamespacedKey().equals(CustomCrafting.ADVANCED_CRAFTING_TABLE)) {
            if (!customCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof CraftingInventory)) return;
        CraftingInventory inventory = (CraftingInventory) event.getClickedInventory();
        if (event.getSlot() == 0) {
            ItemStack resultItem = inventory.getResult();
            if (ItemUtils.isAirOrNull(resultItem) || (!ItemUtils.isAirOrNull(event.getCursor()) && !event.getCursor().isSimilar(resultItem) && !event.isShiftClick())) {
                event.setCancelled(true);
                return;
            }
            if (craftManager.has(event.getWhoClicked().getUniqueId())) {
                //inventory.setResult(ItemUtils.AIR);
                event.setResult(Event.Result.DENY);
                ItemStack[] matrix = inventory.getMatrix();
                craftManager.consumeRecipe(resultItem, matrix, event);
                //TODO: Items still bug a bit when crafting.
                //((Player) event.getWhoClicked()).updateInventory(); //This helps, but the bug is not gone completely. Assumption: The crafting logic of vanilla minecraft is called afterwards and bugs the items, because there is no actual recipe registered.
                Bukkit.getScheduler().runTask(customCrafting, () -> inventory.setMatrix(matrix)); // Setting the matrix 1 tick later overrides the bugged items. Setting it directly will also cause the newly set items to bug.
                craftManager.remove(event.getWhoClicked().getUniqueId());
            }
        } else if ((event.getAction().equals(InventoryAction.PLACE_ALL) || event.getAction().equals(InventoryAction.PLACE_ONE) || event.getAction().equals(InventoryAction.PLACE_SOME)) && inventory.getItem(event.getSlot()) != null) {
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                PrepareItemCraftEvent event1 = new PrepareItemCraftEvent(inventory, event.getView(), false);
                Bukkit.getPluginManager().callEvent(event1);
            });
        }
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
            //api.sendDebugMessage("Detected recipe: " + ((Keyed) e.getRecipe()).getKey());
            //Check for custom recipe that overrides the vanilla recipe
            NamespacedKey namespacedKey = NamespacedKey.of(((Keyed) e.getRecipe()).getKey());
            ICraftingRecipe recipe = Registry.RECIPES.getAdvancedCrafting(namespacedKey);
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
            //player.updateInventory();
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
            NamespacedKey key = NamespacedKeyUtils.toInternal(NamespacedKey.fromBukkit(event.getRecipe()));
            if (!customCrafting.getDataHandler().getDisabledRecipes().contains(key)) {
                ICustomRecipe<?, ?> customRecipe = Registry.RECIPES.get(key);
                if (customRecipe instanceof CraftingRecipe) {
                    if (customCrafting.getConfigHandler().getConfig().isMCRegistry(CraftRecipeMCRegistry.LIMITED)) {
                        event.setCancelled(true);
                        return;
                    }
                    if (customRecipe.isHidden() || (customRecipe.getConditions().getByID("permission") != null && !customRecipe.getConditions().getByID("permission").check(customRecipe, new Conditions.Data(event.getPlayer(), null, null)))) {
                        event.setCancelled(true);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}
