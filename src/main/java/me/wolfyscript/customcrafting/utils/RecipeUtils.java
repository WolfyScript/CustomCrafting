package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.RandomCollection;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RecipeUtils {

    private final Map<UUID, CraftingData> preCraftedRecipes = new HashMap<>();
    private final Map<UUID, Map<NamespacedKey, CustomItem>> preCraftedItems = new HashMap<>();
    private final CustomCrafting customCrafting;

    public RecipeUtils(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    public ItemStack preCheckRecipe(ItemStack[] matrix, Player player, boolean isRepair, Inventory inventory, boolean elite, boolean advanced) {
        remove(player.getUniqueId());
        if (customCrafting.getConfigHandler().getConfig().isLockedDown()) {
            return null;
        }
        DataHandler dataHandler = customCrafting.getRecipeHandler();
        List<List<ItemStack>> ingredients = dataHandler.getIngredients(matrix);
        CustomItem customItem = dataHandler.getSimilarRecipesStream(ingredients, elite, advanced).map(recipe -> checkRecipe(recipe, ingredients, player, inventory, dataHandler, isRepair)).filter(Objects::nonNull).findFirst().orElse(null);
        return customItem == null ? null : customItem.create();
    }

    public CustomItem checkRecipe(CraftingRecipe<?> recipe, List<List<ItemStack>> matrix, Player player, Inventory inventory, DataHandler dataHandler, boolean isRepair) {
        if (dataHandler.getDisabledRecipes().contains(recipe.getNamespacedKey())) {
            return null; //No longer call Event if recipe is disabled!
        }
        CraftingData craftingData = recipe.getConditions().checkConditions(recipe, new Conditions.Data(player, player.getTargetBlock(null, 5), player.getOpenInventory())) ? recipe.check(matrix) : null;
        CustomPreCraftEvent customPreCraftEvent = new CustomPreCraftEvent(craftingData == null, isRepair, recipe, inventory, matrix);
        Bukkit.getPluginManager().callEvent(customPreCraftEvent); //The event is still called even if the recipe is invalid! This will allow other plugins to manipulate their own recipes or use their own checks!
        if (!customPreCraftEvent.isCancelled()) {
            put(player.getUniqueId(), craftingData);
            Map<NamespacedKey, CustomItem> preCraftedItem = getPreCraftedItems(player.getUniqueId());
            if (preCraftedItem.get(recipe.getNamespacedKey()) == null) {
                RandomCollection<CustomItem> items = customPreCraftEvent.getResult().parallelStream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).collect(RandomCollection.getCollector((rdmCollection, customItem) -> rdmCollection.add(customItem.getRarityPercentage(), customItem.clone())));
                if (!items.isEmpty()) {
                    CustomItem result = items.next();
                    preCraftedItem.put(recipe.getNamespacedKey(), result);
                    return result;
                }
            } else {
                return preCraftedItem.get(recipe.getNamespacedKey());
            }
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
                //TODO: Make as much as possible async and prevent items from bugging when crafting.
                Player player = (Player) event.getWhoClicked();
                Bukkit.getScheduler().runTaskAsynchronously(customCrafting, () -> {
                    CCPlayerData playerStore = PlayerUtil.getStore(player);
                    playerStore.increaseRecipeCrafts(recipe.getNamespacedKey(), 1);
                    playerStore.increaseTotalCrafts(1);
                    CustomItem customItem = WorldUtils.getWorldCustomItemStore().getCustomItem(inventory.getLocation());
                    if (customItem != null && customItem.getNamespacedKey().equals(CustomCrafting.ADVANCED_CRAFTING_TABLE)) {
                        playerStore.increaseAdvancedCrafts(1);
                    } else {
                        playerStore.increaseNormalCrafts(1);
                    }
                });
                if (event.isShiftClick()) {
                    List<List<ItemStack>> ingredients = customCrafting.getRecipeHandler().getIngredients(matrix);
                    int possible = Math.min(InventoryUtils.getInventorySpace(inventoryView.getBottomInventory(), result) / result.getAmount(), recipe.getAmountCraftable(ingredients, craftingData));
                    if (possible > 0) {
                        recipe.removeMatrix(ingredients, inventory, possible, craftingData);
                        RandomCollection<CustomItem> results = recipe.getResults().parallelStream().filter(cI -> !cI.hasPermission() || player.hasPermission(cI.getPermission())).collect(RandomCollection.getCollector((rdmC, cI) -> rdmC.add(cI.getRarityPercentage(), cI.clone())));
                        for (int i = 0; i < possible; i++) {
                            inventoryView.getBottomInventory().addItem(results.next().create());
                        }
                    }
                } else if (!event.isShiftClick()) {
                    ItemStack cursor = inventoryView.getCursor();
                    if (ItemUtils.isAirOrNull(cursor) || (result.isSimilar(cursor) && cursor.getAmount() + result.getAmount() <= cursor.getMaxStackSize())) {
                        recipe.removeMatrix(customCrafting.getRecipeHandler().getIngredients(matrix), inventory, 1, craftingData);
                        if (ItemUtils.isAirOrNull(cursor)) {
                            inventoryView.setCursor(result);
                        } else {
                            cursor.setAmount(cursor.getAmount() + result.getAmount());
                        }
                        getPreCraftedItems(player.getUniqueId()).remove(recipe.getNamespacedKey());
                    }
                }
            }
            remove(event.getWhoClicked().getUniqueId());
        }
    }

    private Map<NamespacedKey, CustomItem> getPreCraftedItems(UUID uuid) {
        if (preCraftedItems.containsKey(uuid)) {
            return preCraftedItems.get(uuid);
        }
        preCraftedItems.put(uuid, new HashMap<>());
        return preCraftedItems.get(uuid);
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