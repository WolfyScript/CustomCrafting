/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.utils;

import com.google.common.base.Functions;
import com.wolfyscript.utilities.bukkit.nms.inventory.NMSInventoryUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.CraftDelayCondition;
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.RandomCollection;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.version.ServerVersion;
import me.wolfyscript.utilities.util.version.WUVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public final class CraftManager {

    private long lastLockDownWarning = 0;
    private final Map<UUID, CraftingData> preCraftedRecipes = new HashMap<>();
    private final Map<InventoryView, MatrixData> currentMatrixData = new HashMap<>();
    private final CustomCrafting customCrafting;
    private final boolean storeCurrentRecipeViaNMS;

    public CraftManager(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        storeCurrentRecipeViaNMS = ServerVersion.getWUVersion().isAfterOrEq(WUVersion.of(4, 16, 9, 0));
    }

    public MatrixData getMatrixData(InventoryView view, CraftingInventory craftingInventory) {
        return currentMatrixData.computeIfAbsent(view, inventory1 -> MatrixData.of(craftingInventory.getMatrix()));
    }

    public void clearCurrentMatrixData(InventoryView view) {
        currentMatrixData.remove(view);
    }

    /**
     * Checks for a possible {@link CraftingRecipe} and returns the result ItemStack of the {@link CraftingRecipe} that is valid.
     *
     * @param matrix The matrix of the crafting grid.
     * @param data   The context data, that is used for conditions, stats, etc.
     * @param types  The types of crafting containers to include.
     * @return The result ItemStack of the valid {@link CraftingRecipe}.
     */
    public Optional<CraftingData> checkCraftingMatrix(ItemStack[] matrix, Conditions.Data data, RecipeType.Container.CraftingContainer<?>... types) {
        data.player().ifPresent(player -> remove(player.getUniqueId()));
        if (customCrafting.getConfigHandler().getConfig().isLockedDown()) {
            if (System.currentTimeMillis() > lastLockDownWarning + (1000 * 60 * 60)) {
                customCrafting.getLogger().warning("Lockdown is enabled! All custom recipes are blocked!");
                lastLockDownWarning = System.currentTimeMillis();
            }
            return Optional.empty();
        }
        var matrixData = MatrixData.of(matrix);
        return customCrafting.getRegistries().getRecipes().get(types)
                .sorted() // Possibility for parallel stream when enough recipes are registered to amortize the overhead. (Things like the PreCraftEvent might interfere. TODO: Experimental Feature)
                .map(recipe -> tryRecipe(recipe, matrixData, data))
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(Functions.identity());
    }

    /**
     * Checks one single {@link CraftingRecipe} and returns the result.
     *
     * @param recipe The {@link CraftingRecipe} to check.
     * @return Optional consisting of the {@link CraftingData}, or empty if the recipe doesn't match.
     */
    public Optional<CraftingData> tryRecipe(CraftingRecipe<?, ?> recipe, MatrixData matrixData, Conditions.Data data) {
        if (!recipe.checkConditions(data))
            return Optional.empty(); //No longer call Event if recipe is disabled or invalid!
        var craftingData = recipe.check(matrixData);
        if (craftingData == null) return Optional.empty();

        var inventory = data.inventoryView().map(InventoryView::getTopInventory).orElse(null);
        var customPreCraftEvent = new CustomPreCraftEvent(recipe, data.getPlayer(), inventory, matrixData);
        Bukkit.getPluginManager().callEvent(customPreCraftEvent);
        if (customPreCraftEvent.isCancelled()) return Optional.empty();

        Result result = customPreCraftEvent.getResult();
        craftingData.setResult(result);
        if (storeCurrentRecipeViaNMS) {
            NMSInventoryUtils.setCurrentRecipe(inventory, recipe.getNamespacedKey());
        }
        data.player().ifPresent(player1 -> put(player1.getUniqueId(), craftingData));
        return Optional.of(craftingData);
    }

    public int collectResult(InventoryClickEvent event, CraftingData craftingData, Player player) {
        if (event.getClickedInventory() == null) return 0;
        CraftingRecipe<?, ?> recipe = craftingData.getRecipe();
        if (recipe != null) {
            Result recipeResult = craftingData.getResult();
            editStatistics(player, event.getClickedInventory(), recipe);
            setPlayerCraftTime(player, recipe);
            remove(event.getWhoClicked().getUniqueId());
            return calculateClick(player, event, craftingData, recipe, recipeResult);
        }
        remove(event.getWhoClicked().getUniqueId());
        return 0;
    }

    private void editStatistics(Player player, Inventory inventory, CraftingRecipe<?, ?> recipe) {
        CCPlayerData playerStore = PlayerUtil.getStore(player);
        playerStore.increaseRecipeCrafts(recipe.getNamespacedKey(), 1);
        playerStore.increaseTotalCrafts(1);
        var customItem = NamespacedKeyUtils.getCustomItem(inventory.getLocation());
        if (customItem != null && customItem.getNamespacedKey().equals(CustomCrafting.ADVANCED_CRAFTING_TABLE)) {
            playerStore.increaseAdvancedCrafts(1);
        } else {
            playerStore.increaseNormalCrafts(1);
        }
    }

    private void setPlayerCraftTime(Player player, CraftingRecipe<?, ?> recipe) {
        CraftDelayCondition condition = recipe.getConditions().getByType(CraftDelayCondition.class);
        if (condition != null && condition.getOption().equals(Conditions.Option.EXACT)) {
            condition.setPlayerCraftTime(player);
        }
    }

    private int calculateClick(Player player, InventoryClickEvent event, CraftingData craftingData, CraftingRecipe<?, ?> recipe, Result recipeResult) {
        var result = recipeResult.item(craftingData, player, null);
        var inventory = event.getClickedInventory();
        int possible = event.isShiftClick() ? Math.min(InventoryUtils.getInventorySpace(player.getInventory(), result) / result.getAmount(), recipe.getAmountCraftable(craftingData)) : 1;
        recipeResult.executeExtensions(inventory.getLocation() == null ? event.getWhoClicked().getLocation() : inventory.getLocation(), inventory.getLocation() != null, (Player) event.getWhoClicked(), possible);
        if (event.isShiftClick()) {
            if (possible > 0) {
                RandomCollection<StackReference> results = recipeResult.randomChoices(player);
                for (int i = 0; i < possible; i++) {
                    var reference = results.next();
                    if (reference != null) {
                        var item = recipeResult.item(craftingData, reference, player, null);
                        if (InventoryUtils.hasInventorySpace(player, item)) {
                            player.getInventory().addItem(item);
                        } else {
                            var loc = player.getLocation();
                            loc.getWorld().dropItem(loc, item);
                        }
                    }
                }
            }
            return possible;
        }
        ItemStack cursor = event.getCursor();
        if (ItemUtils.isAirOrNull(cursor) || (result.isSimilar(cursor) && cursor.getAmount() + result.getAmount() <= cursor.getMaxStackSize())) {
            if (ItemUtils.isAirOrNull(cursor)) {
                event.setCursor(result);
            } else {
                cursor.setAmount(cursor.getAmount() + result.getAmount());
            }
            recipeResult.removeCachedItem(player);
        } else return 0;
        return possible;
    }

    /**
     * Sets the current active {@link CraftingData} for the player.
     *
     * @param uuid         The {@link UUID} of the player.
     * @param craftingData The {@link CraftingData} of the latest check.
     */
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

    /**
     * Gets the current data that is available under the specified uuid.
     *
     * @param uuid The uuid to get the data for.
     * @return An Optional of the available data for the specified uuid.
     */
    public Optional<CraftingData> get(UUID uuid) {
        return Optional.ofNullable(preCraftedRecipes.get(uuid));
    }

    private static int gridSize(ItemStack[] ingredients) {
        return switch (ingredients.length) {
            case 4 -> 2;
            case 9 -> 3;
            case 16 -> 4;
            case 25 -> 5;
            case 36 -> 6;
            default -> (int) Math.sqrt(ingredients.length);
        };
    }

    /**
     * Generates the {@link MatrixData} from the specified ingredient array.
     * This is quite resource intensive and should not be called too much.
     * <p>
     * Run it once for each inventory change and then use the generated value, till the next inventory update.
     *
     * @param ingredients The ingredients to generate the data for.
     * @return The newly generated MatrixData representing the shape and stripped ingredients.
     */
    @Deprecated
    public MatrixData getIngredients(ItemStack[] ingredients) {
        return MatrixData.of(ingredients);
    }

    /**
     * This object contains all necessary data of the crafting matrix like the width and height of the ingredients' area. <br>
     * <p>
     * The contained matrix is already stripped down to the smallest possible dimensions.<br>
     * Which means that it can be smaller than the actual grid!<br>
     */
    public static class MatrixData {

        private final ItemStack[] matrix;
        private final ItemStack[] originalMatrix;
        private final int gridSize;
        private final int height;
        private final int width;
        private final int offsetY;
        private final int offsetX;
        private final long strippedSize;
        private final ItemStack[] items;

        @Deprecated
        public MatrixData(ItemStack[] matrix, int height, int width) {
            this(matrix, matrix, height, width, 3, 0, 0);
        }

        public MatrixData(ItemStack[] originalMatrix, ItemStack[] matrix, int height, int width, int gridSize, int offsetX, int offsetY) {
            this.originalMatrix = originalMatrix;
            this.matrix = matrix;
            this.height = height;
            this.width = width;
            this.gridSize = gridSize;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.items = Arrays.stream(matrix).filter(itemStack -> !ItemUtils.isAirOrNull(itemStack)).toArray(ItemStack[]::new);
            this.strippedSize = this.items.length;
        }

        /**
         * @return The height of the matrix.
         */
        public int getHeight() {
            return height;
        }

        /**
         * @return The width of the matrix.
         */
        public int getWidth() {
            return width;
        }

        /**
         * The original grid size of the matrix.
         *
         * @return The grid dimension.
         */
        public int getGridSize() {
            return gridSize;
        }

        /**
         * The offset specifies by how much the shape is shifted in the recipe.<br>
         * This is used to place the items into the correct inventory slot for shaped recipes.<br>
         * Shapeless recipes ignore these.
         *
         * @return The x offset (from the left) of the shape in the matrix.
         */
        public int getOffsetX() {
            return offsetX;
        }

        /**
         * The offset specifies by how much the shape is shifted in the recipe.<br>
         * This is used to place the items into the correct inventory slot for shaped recipes.<br>
         * Shapeless recipes ignore these.
         *
         * @return The y offset (from the top) of the shape in the matrix.
         */
        public int getOffsetY() {
            return offsetY;
        }

        /**
         * @return The matrix size with all empty air/null values removed.
         */
        public long getStrippedSize() {
            return strippedSize;
        }

        /**
         * @return The non-null/air items from the matrix in order of appearance.
         */
        public ItemStack[] getItems() {
            return items;
        }

        /**
         * @return The matrix of the crafting grid. Stripped to the smallest possible dimensions.
         */
        public ItemStack[] getMatrix() {
            return matrix;
        }

        public ItemStack[] getOriginalMatrix() {
            return originalMatrix;
        }

        public static MatrixData of(ItemStack[] ingredients) {
            int gridSize = gridSize(ingredients);
            ItemStack[][] itemMatrix = new ItemStack[gridSize][gridSize];
            for (int y = 0; y < gridSize; y++) {
                itemMatrix[y] = Arrays.copyOfRange(ingredients, y * gridSize, gridSize + y * gridSize);
            }
            int top = 0;
            int bottom = gridSize;
            int left = 0;
            int right = gridSize;
            topCheck:
            for (; top < gridSize; top++) {
                for (ItemStack stack : itemMatrix[top]) {
                    if (stack != null) break topCheck;
                }
            }
            bottomCheck:
            for (; bottom > 0; bottom--) {
                for (ItemStack stack : itemMatrix[bottom - 1]) {
                    if (stack != null) break bottomCheck;
                }
            }
            if (top > bottom)
                return new MatrixData(ingredients, new ItemStack[0], 0, 0, gridSize, 0, 0); // No item inside the grid, do not do further calculations!
            itemMatrix = Arrays.copyOfRange(itemMatrix, top, bottom);
            leftCheck:
            for (; left < gridSize; left++) {
                for (ItemStack[] row : itemMatrix) {
                    if (row[left] != null) break leftCheck;
                }
            }
            rightCheck:
            for (; right > 0; right--) {
                for (ItemStack[] row : itemMatrix) {
                    if (row[right - 1] != null) break rightCheck;
                }
            }
            var width = right > left ? right - left : 0; // It should already be caught by the (top > bottom) check, but just make sure.
            var flatList = new ItemStack[itemMatrix.length * width];
            for (int i = 0; i < itemMatrix.length; i++) {
                System.arraycopy(itemMatrix[i], left, flatList, i*width, width);
            }
            return new MatrixData(ingredients, flatList, itemMatrix.length, width, gridSize, left, top);
        }

        @Override
        public String toString() {
            return "MatrixData{" +
                    "matrix=" + Arrays.toString(matrix) +
                    ", height=" + height +
                    ", width=" + width +
                    '}';
        }
    }

}