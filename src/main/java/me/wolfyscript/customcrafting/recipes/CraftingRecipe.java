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

package me.wolfyscript.customcrafting.recipes;

import com.wolfyscript.utilities.bukkit.nms.item.crafting.FunctionalRecipeBuilderCrafting;
import java.util.ArrayList;
import java.util.Optional;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.gui.recipebook.ButtonContainerIngredient;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeBook;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.conditions.AdvancedWorkbenchCondition;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

public abstract class CraftingRecipe<C extends CraftingRecipe<C, S>, S extends CraftingRecipeSettings<S>> extends CustomRecipe<C> {

    public static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    protected static final String INGREDIENTS_KEY = "ingredients";

    protected List<Ingredient> ingredients;

    protected final int maxGridDimension;
    protected final int maxIngredients;

    private final S settings;

    protected CraftingRecipe(NamespacedKey namespacedKey, JsonNode node, int gridSize, Class<S> settingsType) {
        super(namespacedKey, node);
        this.ingredients = List.of();
        this.maxGridDimension = gridSize;
        this.maxIngredients = maxGridDimension * maxGridDimension;
        this.settings = Objects.requireNonNullElseGet(mapper.convertValue(node.path("settings"), settingsType), () -> {
            try {
                return settingsType.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                return null;
            }
        });
    }

    protected CraftingRecipe(NamespacedKey key, CustomCrafting customCrafting, int gridSize, S settings) {
        super(key, customCrafting);
        this.ingredients = List.of();
        this.maxGridDimension = gridSize;
        this.maxIngredients = maxGridDimension * maxGridDimension;
        this.settings = settings;
    }

    protected CraftingRecipe(CraftingRecipe<C, S> craftingRecipe) {
        super(craftingRecipe);
        this.ingredients = craftingRecipe.ingredients != null ? craftingRecipe.ingredients.stream().map(Ingredient::clone).toList() : List.of();
        this.maxGridDimension = craftingRecipe.maxGridDimension;
        this.maxIngredients = craftingRecipe.maxIngredients;
        this.settings = craftingRecipe.settings.clone();
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return ingredients.get(slot);
    }

    @JsonIgnore
    public abstract boolean isShapeless();

    public abstract boolean fitsDimensions(CraftManager.MatrixData matrixData);

    public abstract CraftingData check(CraftManager.MatrixData matrixData);

    /**
     * @return The type specific settings. {@link me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings}, {@link me.wolfyscript.customcrafting.recipes.settings.EliteRecipeSettings}
     */
    public S getSettings() {
        return settings;
    }

    /**
     * @return The max grid dimensions of the crafting recipe type.
     */
    @JsonIgnore
    public int getMaxGridDimension() {
        return maxGridDimension;
    }

    /**
     * @return The maximum ingredients of the crafting recipe type. Usually the maxGridDimension squared (9, 16, 25, 36).
     */
    @JsonIgnore
    public int getMaxIngredients() {
        return maxIngredients;
    }

    /**
     * This method returns the ingredients of the recipe. The list is unmodifiable!<br>
     * <br>
     * <b>Shaped recipe:</b><br>
     * The returned list contains the flattened ingredients, they are created from the already shrunken shape, so the list might be smaller than 9 or 36 in case of elite crafting recipes.
     * Slots that do not have an associated ingredient in the shape are filled with empty {@link Ingredient} objects.
     *
     * @return An unmodifiable list of the ingredients.
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void removeMatrix(Inventory inventory, int totalAmount, CraftingData craftingData) {
        removeMatrix(null, inventory, totalAmount, craftingData);
    }

    public void removeMatrix(@Nullable Player player, @Nullable Inventory inventory, int totalAmount, CraftingData craftingData) {
        craftingData.getIndexedBySlot().forEach((slot, data) -> {
            var item = data.customItem();
            if (item != null) {
                item.remove(data.itemStack(), totalAmount, inventory, player, player != null ? player.getLocation() : null, data.ingredient().isReplaceWithRemains());
            }
        });
    }

    public ItemStack[] shrinkMatrix(@Nullable Player player, @Nullable Inventory inventory, int totalAmount, CraftingData craftingData, int gridDimension) {
        ItemStack[] matrix = new ItemStack[gridDimension * gridDimension];
        craftingData.getIndexedBySlot().forEach((slot, data) -> {
            matrix[slot] = data.customItem().shrink(data.itemStack(), totalAmount, data.ingredient().isReplaceWithRemains(), inventory, player, null);
        });
        return matrix;
    }

    public int getAmountCraftable(CraftingData craftingData) {
        int totalAmount = -1;
        for (IngredientData value : craftingData.getIndexedBySlot().values()) {
            var item = value.customItem();
            if (item != null) {
                var input = value.itemStack();
                if (input != null) {
                    int possible = input.getAmount() / item.getAmount();
                    if (possible < totalAmount || totalAmount == -1) {
                        totalAmount = possible;
                    }
                }
            }
        }
        return totalAmount;
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        if (!ingredients.isEmpty()) {
            ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(maxIngredients))).setVariants(guiHandler, this.getResult());
            for (int i = 0; i < maxIngredients && i < ingredients.size(); i++) {
                var ingredient = ingredients.get(i);
                if (ingredient != null) {
                    ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(i))).setVariants(guiHandler, ingredient);
                }
            }
        }
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        if (!ingredients.isEmpty()) {
            var cluster = guiWindow.getCluster();
            if (RecipeType.Container.CRAFTING.isInstance(this) && getConditions().has(AdvancedWorkbenchCondition.KEY)) {
                var glass = ClusterMain.GLASS_PURPLE;
                for (int i = 0; i < 9; i++) {
                    event.setButton(i, glass);
                }
                for (int i = 36; i < 54; i++) {
                    event.setButton(i, glass);
                }
            }
            List<Condition<?>> conditions = getConditions().getValues().stream().filter(condition -> !condition.getNamespacedKey().equals(AdvancedWorkbenchCondition.KEY) && !condition.getNamespacedKey().equals("permission")).toList();
            int startSlot = 9 / (conditions.size() + 1);
            int slot = 0;
            for (Condition<?> condition : conditions) {
                event.setButton(36 + startSlot + slot, new NamespacedKey(ClusterRecipeBook.KEY, "conditions." + condition.getNamespacedKey().toString("__")));
                slot += 2;
            }
            boolean elite = RecipeType.Container.ELITE_CRAFTING.isInstance(this);
            event.setButton(elite ? 24 : 23, new NamespacedKey(ClusterRecipeBook.KEY, isShapeless() ? "workbench.shapeless_on" : "workbench.shapeless_off"));
            startSlot = elite ? 0 : 10;
            for (int i = 0; i < maxIngredients; i++) {
                event.setButton(startSlot + i + (i / maxGridDimension) * (9 - maxGridDimension), ButtonContainerIngredient.key(cluster, i));
            }
            event.setButton(25, ButtonContainerIngredient.key(cluster, maxIngredients));
        }
    }

    protected void applySettingsToFunctionalRecipe(FunctionalRecipeBuilderCrafting builder) {
        builder.setGroup(group);
        final CraftManager craftManager = customCrafting.getCraftManager();
        builder.setRecipeMatcher((inventory, world) -> {
            if (!isDisabled() && inventory.getHolder() instanceof Player player) {
                if (checkConditions(Conditions.Data.of(player, inventory.getLocation() != null ? inventory.getLocation().getBlock() : player.getLocation().getBlock(), player.getOpenInventory()))) {
                    var matrixData = craftManager.getMatrixData(player.getOpenInventory(), inventory);
                    CraftingData craftingData = check(matrixData);
                    if (craftingData != null) {
                        var customPreCraftEvent = new CustomPreCraftEvent(this, player, inventory, matrixData);
                        Bukkit.getPluginManager().callEvent(customPreCraftEvent);
                        if (!customPreCraftEvent.isCancelled()) {
                            Result result = customPreCraftEvent.getResult();
                            craftingData.setResult(result);
                            craftManager.put(player.getUniqueId(), craftingData);
                            return true;
                        }
                    }
                }
                craftManager.remove(player.getUniqueId());
            }
            return false;
        });
        builder.setRecipeAssembler(inventory -> {
            if (inventory.getHolder() instanceof Player player)
                return Optional.ofNullable(getResult().getItem(player).orElse(new CustomItem(Material.AIR)).create());
            return Optional.of(new ItemStack(Material.AIR));
        });
        builder.setRemainingItemsFunction(inventory -> {
            if (!isDisabled() && inventory.getHolder() instanceof Player player) {
                craftManager.get(player.getUniqueId()).ifPresent(craftingData -> {
                    for (int i = 0; i < inventory.getMatrix().length; i++) {
                        IngredientData ingredientData = craftingData.getIndexedBySlot().get(i);
                        if (ingredientData != null) {
                            inventory.setItem(i+1, ingredientData.customItem().shrink(inventory.getMatrix()[i], 1, ingredientData.ingredient().isReplaceWithRemains(), inventory, player, player.getLocation()));
                        }
                    }
                });
            }
            return Optional.of(new ArrayList<>());
        });
    }

    @Deprecated
    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeObjectField(KEY_RESULT, result);
    }

    @Override
    public void writeToBuf(MCByteBuf byteBuf) {
        super.writeToBuf(byteBuf);
        byteBuf.writeInt(maxGridDimension);
        byteBuf.writeCollection(ingredients, (buf, ingredient) -> buf.writeCollection(ingredient.getChoices(), (buf1, customItem) -> buf1.writeItemStack(customItem.create())));
    }

    @Override
    public boolean save(@Nullable Player player) {
        boolean saveSuccessful = super.save(player);
        if (saveSuccessful) {
            //We need to delete the old recipe when the type changes between shapeless and shaped, because else it is present in two different folders!
            CustomRecipe<?> oldRecipe = customCrafting.getRegistries().getRecipes().get(getNamespacedKey());
            if (oldRecipe instanceof CraftingRecipe<?, ?> oldCraftingRecipe && oldCraftingRecipe.isShapeless() != isShapeless()) {
                getAPI().getChat().sendMessage(player, ChatColor.YELLOW + "Recipe Type changed... deleting old recipe!");
                oldCraftingRecipe.delete(player);
            }
        }
        return saveSuccessful;
    }
}
