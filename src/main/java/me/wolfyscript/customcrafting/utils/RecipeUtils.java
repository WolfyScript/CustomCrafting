package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.listeners.customevents.CustomCraftEvent;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.RandomCollection;
import me.wolfyscript.utilities.util.RandomUtils;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        RecipeHandler recipeHandler = customCrafting.getRecipeHandler();
        List<List<ItemStack>> ingredients = recipeHandler.getIngredients(matrix);
        Stream<CraftingRecipe<?>> stream = recipeHandler.getSimilarRecipes(ingredients, elite, advanced).stream().sorted(Comparator.comparing(ICustomRecipe::getPriority));
        CustomItem customItem = stream.map(recipe -> checkRecipe(recipe, ingredients, player, inventory, recipeHandler, isRepair, () -> recipe.check(ingredients))).filter(Objects::nonNull).findFirst().orElse(null);
        return customItem == null ? null : customItem.create();
    }

    public CustomItem checkRecipe(CraftingRecipe<?> recipe, List<List<ItemStack>> matrix, Player player, Inventory inventory, RecipeHandler recipeHandler, boolean isRepair, Supplier<CraftingData> craftingDataSupplier) {
        CustomPreCraftEvent customPreCraftEvent = new CustomPreCraftEvent(isRepair, recipe, inventory, matrix);
        customPreCraftEvent.setCancelled(true);
        CraftingData craftingData = null;
        if (!recipeHandler.getDisabledRecipes().contains(recipe.getNamespacedKey().toString()) && recipe.getConditions().checkConditions(recipe, new Conditions.Data(player, player.getTargetBlock(null, 5), player.getOpenInventory()))) {
            craftingData = craftingDataSupplier.get();
            if (craftingData != null) {
                customPreCraftEvent.setCancelled(false);
            }
        }
        //The event is still called even if the recipe is invalid! This will allow other plugins to manipulate their own recipes or use their own checks!
        Bukkit.getPluginManager().callEvent(customPreCraftEvent);
        if (!customPreCraftEvent.isCancelled()) {
            //api.sendDebugMessage("Recipe \"" + customPreCraftEvent.getRecipe().getNamespacedKey().toString() + "\" detected!");
            put(player.getUniqueId(), craftingData);
            Map<NamespacedKey, CustomItem> preCraftedItem = preCraftedItems.getOrDefault(player.getUniqueId(), new HashMap<>());
            CustomItem result = new CustomItem(Material.AIR);
            if (preCraftedItem.get(recipe.getNamespacedKey()) == null) {
                RandomCollection<CustomItem> items = customPreCraftEvent.getResult().parallelStream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).collect(RandomCollection.getCollector((rdmCollection, customItem) -> rdmCollection.add(customItem.getRarityPercentage(), customItem.clone())));
                if (!items.isEmpty()) {
                    result = items.next();
                    preCraftedItem.put(recipe.getNamespacedKey(), result);
                    preCraftedItems.put(player.getUniqueId(), preCraftedItem);
                }
            } else {
                result = preCraftedItem.get(recipe.getNamespacedKey());
            }
            return result;
        }
        return null;
    }

    public void consumeRecipe(ItemStack result, ItemStack[] matrix, InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        InventoryView inventoryView = event.getView();
        if (!ItemUtils.isAirOrNull(result) && has(event.getWhoClicked().getUniqueId())) {
            CraftingData craftingData = preCraftedRecipes.get(event.getWhoClicked().getUniqueId());
            CraftingRecipe<?> recipe = craftingData.getRecipe();
            if (recipe != null) {
                CustomCraftEvent customCraftEvent = new CustomCraftEvent(recipe, inventory);
                if (!customCraftEvent.isCancelled() && event.getCurrentItem() != null) {
                    List<List<ItemStack>> ingredients = customCrafting.getRecipeHandler().getIngredients(matrix);
                    Player player = (Player) event.getWhoClicked();
                    {//---------COMMANDS AND STATISTICS-------------
                        CCPlayerData playerStore = PlayerUtil.getStore(player);
                        playerStore.increaseRecipeCrafts(customCraftEvent.getRecipe().getNamespacedKey(), 1);
                        playerStore.increaseTotalCrafts(1);
                        CustomItem customItem = WorldUtils.getWorldCustomItemStore().getCustomItem(player.getTargetBlock(null, 5).getLocation());
                        if (customItem != null && customItem.getNamespacedKey().equals(CustomCrafting.ADVANCED_CRAFTING_TABLE)) {
                            playerStore.increaseAdvancedCrafts(1);
                        } else {
                            playerStore.increaseNormalCrafts(1);
                        }
                    }//----------------------------------------------
                    //CALCULATE AMOUNTS CRAFTABLE AND REMOVE THEM!
                    int amount = recipe.getAmountCraftable(ingredients, craftingData);
                    List<CustomItem> results = recipe.getResults().parallelStream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).collect(Collectors.toList());
                    if (results.size() < 2 && event.isShiftClick()) {
                        if (result.getAmount() > 0) {
                            int possible = Math.min(InventoryUtils.getInventorySpace(inventoryView.getBottomInventory(), result) / result.getAmount(), amount);
                            if (possible > 0) {
                                recipe.removeMatrix(ingredients, inventory, possible, craftingData);
                            }
                            event.setCurrentItem(new ItemStack(Material.AIR));
                            for (int i = 0; i < possible; i++) {
                                inventoryView.getBottomInventory().addItem(result);
                                result = !results.isEmpty() ? results.get(RandomUtils.random.nextInt(customCraftEvent.getResult().size())).create() : new ItemStack(Material.AIR);
                            }
                        }
                    } else if (!event.isShiftClick()) {
                        ItemStack cursor = inventoryView.getCursor();
                        if (ItemUtils.isAirOrNull(cursor) || cursor.getAmount() + result.getAmount() < cursor.getMaxStackSize()) {
                            Map<NamespacedKey, CustomItem> preCraftedItem = preCraftedItems.getOrDefault(player.getUniqueId(), new HashMap<>());
                            recipe.removeMatrix(ingredients, inventory, 1, craftingData);
                            if (!ItemUtils.isAirOrNull(cursor) && result.isSimilar(cursor)) {
                                cursor.setAmount(cursor.getAmount() + result.getAmount());
                            } else if (ItemUtils.isAirOrNull(cursor)) {
                                inventoryView.setCursor(result);
                            }
                            preCraftedItem.put(recipe.getNamespacedKey(), null);
                            preCraftedItems.put(player.getUniqueId(), preCraftedItem);
                        }
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