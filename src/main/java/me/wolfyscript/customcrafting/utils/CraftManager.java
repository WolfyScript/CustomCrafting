package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.CraftDelayCondition;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CraftManager {

    private final Map<UUID, CraftingData> preCraftedRecipes = new HashMap<>();
    private final CustomCrafting customCrafting;
    private final DataHandler dataHandler;

    public CraftManager(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.dataHandler = customCrafting.getDataHandler();
    }

    /**
     * Checks for a possible {@link CraftingRecipe} and returns the result ItemStack of the {@link CraftingRecipe} that is valid.
     *
     * @param matrix    The matrix of the crafting grid.
     * @param player    The player that executed the craft.
     * @param inventory The inventory this craft was called from.
     * @param elite     If the workstation is an Elite Crafting Table.
     * @param advanced  If the workstation is an Advanced Crafting Table.
     * @return The result ItemStack of the valid {@link CraftingRecipe}.
     */
    public ItemStack preCheckRecipe(ItemStack[] matrix, Player player, Inventory inventory, boolean elite, boolean advanced) {
        remove(player.getUniqueId());
        if (customCrafting.getConfigHandler().getConfig().isLockedDown()) {
            return null;
        }
        List<List<ItemStack>> ingredients = dataHandler.getIngredients(matrix);
        Block targetBlock = inventory.getLocation() != null ? inventory.getLocation().getBlock() : player.getTargetBlockExact(5);
        return Registry.RECIPES.getSimilar(ingredients, elite, advanced).map(recipe -> checkRecipe(recipe, matrix, ingredients, player, targetBlock, inventory, dataHandler)).filter(Objects::nonNull).findFirst().map(CustomItem::create).orElse(null);
    }

    /**
     * Checks one single {@link CraftingRecipe} and returns the {@link CustomItem} if it's valid.
     *
     * @param recipe      The {@link CraftingRecipe} to check.
     * @param matrix      The matrix of the crafting grid.
     * @param ingredients The ingredients of the matrix without surrounding empty columns/rows (via {@link DataHandler#getIngredients(ItemStack[])}).
     * @param player      The player that crafts it.
     * @param block       The block of the workstation or players inventory.
     * @param inventory   The inventory of the workstation or player.
     * @param dataHandler The {@link DataHandler} from {@link CustomCrafting#getDataHandler()}
     * @return The result {@link CustomItem} if the {@link CraftingRecipe} is valid. Else null.
     */
    @Nullable
    public CustomItem checkRecipe(CraftingRecipe<?> recipe, ItemStack[] matrix, List<List<ItemStack>> ingredients, Player player, Block block, Inventory inventory, DataHandler dataHandler) {
        if (!dataHandler.getDisabledRecipes().contains(recipe.getNamespacedKey())) {
            CraftingData craftingData = recipe.checkConditions(new Conditions.Data(player, block, player.getOpenInventory())) ? recipe.check(matrix, ingredients) : null;
            if (craftingData != null) {
                CustomPreCraftEvent customPreCraftEvent = new CustomPreCraftEvent(recipe, inventory, ingredients);
                Bukkit.getPluginManager().callEvent(customPreCraftEvent);
                if (!customPreCraftEvent.isCancelled()) {
                    Result<?> result = customPreCraftEvent.getResult().get(matrix);
                    craftingData.setResult(result);
                    put(player.getUniqueId(), craftingData);
                    return result.getItem(player).orElse(new CustomItem(Material.AIR));
                }
            }
        }
        return null; //No longer call Event if recipe is disabled or invalid!
    }

    /**
     * Consumes the active Recipe from the matrix and sets the correct item to the cursor.
     *
     * @param result The result {@link ItemStack} from the inventory.
     * @param matrix The matrix of the crafting grid. <strong>The {@link ItemStack}s of the matrix will be edited directly! It will not add new instances!</strong>
     * @param event  The {@link InventoryClickEvent} that caused this click.
     */
    public void consumeRecipe(ItemStack result, ItemStack[] matrix, InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory != null && !ItemUtils.isAirOrNull(result) && has(event.getWhoClicked().getUniqueId())) {
            CraftingData craftingData = preCraftedRecipes.get(event.getWhoClicked().getUniqueId());
            CraftingRecipe<?> recipe = craftingData.getRecipe();
            if (recipe != null && !ItemUtils.isAirOrNull(result)) {
                Result<?> recipeResult = craftingData.getResult();
                Player player = (Player) event.getWhoClicked();
                editStatistics(player, inventory, recipe);
                setPlayerCraftTime(player, recipe);
                recipeResult.executeExtensions(inventory.getLocation() == null ? event.getWhoClicked().getLocation() : inventory.getLocation(), inventory.getLocation() != null, (Player) event.getWhoClicked());
                calculateClick(player, event, craftingData, recipe, matrix, recipeResult, result);
            }
            remove(event.getWhoClicked().getUniqueId());
        }
    }

    private void editStatistics(Player player, Inventory inventory, CraftingRecipe<?> recipe) {
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
    }

    private void setPlayerCraftTime(Player player, CraftingRecipe<?> recipe) {
        Condition condition = recipe.getConditions().getByID("craft_delay");
        if (condition instanceof CraftDelayCondition && condition.getOption().equals(Conditions.Option.EXACT)) {
            ((CraftDelayCondition) condition).setPlayerCraftTime(player);
        }
    }

    private void calculateClick(Player player, InventoryClickEvent event, CraftingData craftingData, CraftingRecipe<?> recipe, ItemStack[] matrix, Result<?> recipeResult, ItemStack result) {
        List<List<ItemStack>> ingredients = dataHandler.getIngredients(matrix);
        int possible = event.isShiftClick() ? Math.min(InventoryUtils.getInventorySpace(player.getInventory(), result) / result.getAmount(), recipe.getAmountCraftable(ingredients, craftingData)) : 1;
        recipe.removeMatrix(ingredients, event.getClickedInventory(), possible, craftingData);
        if (event.isShiftClick()) {
            if (possible > 0) {
                RandomCollection<CustomItem> results = recipeResult.getRandomChoices(player);
                for (int i = 0; i < possible; i++) {
                    CustomItem customItem = results.next();
                    if (customItem != null) {
                        player.getInventory().addItem(customItem.create());
                    }
                }
            }
            return;
        }
        ItemStack cursor = event.getCursor();
        if (ItemUtils.isAirOrNull(cursor) || (result.isSimilar(cursor) && cursor.getAmount() + result.getAmount() <= cursor.getMaxStackSize())) {
            if (ItemUtils.isAirOrNull(cursor)) {
                event.setCursor(result);
            } else {
                cursor.setAmount(cursor.getAmount() + result.getAmount());
            }
            recipeResult.removeCachedItem(player);
        }
    }

    public void put(UUID uuid, CraftingData craftingData) {
        preCraftedRecipes.put(uuid, craftingData);
    }

    /**
     * Removes the active CustomRecipe of the specified player.
     *
     * @param uuid The UUID of the player.
     */
    public void remove(UUID uuid) {
        preCraftedRecipes.remove(uuid);
    }

    /**
     * @param uuid The uuid of the player.
     * @return If the player has an active CustomRecipe.
     */
    public boolean has(UUID uuid) {
        return preCraftedRecipes.containsKey(uuid);
    }
}