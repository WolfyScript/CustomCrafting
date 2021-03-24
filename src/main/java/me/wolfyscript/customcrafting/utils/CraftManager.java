package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.RandomCollection;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CraftManager {

    private final Map<UUID, CraftingData> preCraftedRecipes = new HashMap<>();
    private final CustomCrafting customCrafting;
    private final DataHandler dataHandler;

    public CraftManager(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.dataHandler = customCrafting.getDataHandler();
    }

    public ItemStack preCheckRecipe(ItemStack[] matrix, Player player, Inventory inventory, boolean elite, boolean advanced) {
        remove(player.getUniqueId());
        if (customCrafting.getConfigHandler().getConfig().isLockedDown()) {
            return null;
        }
        List<List<ItemStack>> ingredients = dataHandler.getIngredients(matrix);
        Block targetBlock = inventory.getLocation() != null ? inventory.getLocation().getBlock() : player.getTargetBlockExact(5);
        CustomItem customItem = Registry.RECIPES.getSimilar(ingredients, elite, advanced).map(recipe -> checkRecipe(recipe, matrix, ingredients, player, targetBlock, inventory, dataHandler)).filter(Objects::nonNull).findFirst().orElse(null);
        return customItem == null ? null : customItem.create();
    }

    public CustomItem checkRecipe(CraftingRecipe<?> recipe, ItemStack[] matrix, List<List<ItemStack>> ingredients, Player player, Block block, Inventory inventory, DataHandler dataHandler) {
        if (dataHandler.getDisabledRecipes().contains(recipe.getNamespacedKey())) {
            return null; //No longer call Event if recipe is disabled!
        }
        CraftingData craftingData = recipe.getConditions().checkConditions(recipe, new Conditions.Data(player, block, player.getOpenInventory())) ? recipe.check(ingredients) : null;
        CustomPreCraftEvent customPreCraftEvent = new CustomPreCraftEvent(craftingData == null, recipe, inventory, ingredients);
        Bukkit.getPluginManager().callEvent(customPreCraftEvent); //The event is still called even if the recipe is invalid! This will allow other plugins to manipulate their own recipes or use their own checks!
        if (!customPreCraftEvent.isCancelled()) {
            put(player.getUniqueId(), craftingData);
            return customPreCraftEvent.getResult().getItem(player, matrix).orElse(new CustomItem(Material.AIR));

        }
        return null;
    }

    public void consumeRecipe(ItemStack result, ItemStack[] matrix, InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        InventoryView inventoryView = event.getView();
        if (inventory != null && !ItemUtils.isAirOrNull(result) && has(event.getWhoClicked().getUniqueId())) {
            CraftingData craftingData = preCraftedRecipes.get(event.getWhoClicked().getUniqueId());
            CraftingRecipe<?> recipe = craftingData.getRecipe();
            if (recipe != null && !ItemUtils.isAirOrNull(result)) {
                Player player = (Player) event.getWhoClicked();
                Bukkit.getScheduler().runTaskAsynchronously(customCrafting, () -> {
                    CCPlayerData playerStore = PlayerUtil.getStore(player);
                    playerStore.increaseRecipeCrafts(recipe.getNamespacedKey(), 1);
                    playerStore.increaseTotalCrafts(1);
                    CustomItem customItem = NamespacedKeyUtils.getCustomItem(inventory.getLocation());
                    if (customItem != null && customItem.getNamespacedKey().equals(CustomCrafting.ADVANCED_CRAFTING_TABLE)) {
                        playerStore.increaseAdvancedCrafts(1);
                    } else {
                        playerStore.increaseNormalCrafts(1);
                    }
                });
                recipe.getResult().executeExtensions(inventory.getLocation() == null ? event.getWhoClicked().getLocation() : inventory.getLocation(), inventory.getLocation() != null, (Player) event.getWhoClicked());
                if (event.isShiftClick()) {
                    List<List<ItemStack>> ingredients = dataHandler.getIngredients(matrix);
                    int possible = Math.min(InventoryUtils.getInventorySpace(inventoryView.getBottomInventory(), result) / result.getAmount(), recipe.getAmountCraftable(ingredients, craftingData));
                    if (possible > 0) {
                        RandomCollection<CustomItem> results = recipe.getResult().getRandomChoices(player, matrix);
                        recipe.removeMatrix(ingredients, inventory, possible, craftingData);
                        for (int i = 0; i < possible; i++) {
                            inventoryView.getBottomInventory().addItem(results.next().create());
                        }
                    }
                } else if (!event.isShiftClick()) {
                    ItemStack cursor = inventoryView.getCursor();
                    if (ItemUtils.isAirOrNull(cursor) || (result.isSimilar(cursor) && cursor.getAmount() + result.getAmount() <= cursor.getMaxStackSize())) {
                        recipe.removeMatrix(dataHandler.getIngredients(matrix), inventory, 1, craftingData);
                        Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                            if (ItemUtils.isAirOrNull(cursor)) {
                                inventoryView.setCursor(result);
                            } else {
                                cursor.setAmount(cursor.getAmount() + result.getAmount());
                            }
                        }, 2);
                        recipe.getResult().removeCachedItem(player);
                    }
                }
            }
            remove(event.getWhoClicked().getUniqueId());
        }
    }

    public void put(UUID uuid, CraftingData craftingData) {
        preCraftedRecipes.put(uuid, craftingData);
    }

    public void remove(UUID uuid) {
        preCraftedRecipes.remove(uuid);
    }

    public boolean has(UUID uuid) {
        return preCraftedRecipes.containsKey(uuid);
    }
}