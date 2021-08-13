package me.wolfyscript.customcrafting.configs.recipebook;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.recipes.*;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.EliteWorkbenchCondition;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RecipeContainer implements Comparable<RecipeContainer> {

    private final List<ICustomRecipe<?>> cachedRecipes;
    //private final Map<UUID, List<ICustomRecipe<?, ?>>> cachedPlayerRecipes = new HashMap<>();
    private final Map<UUID, List<ItemStack>> cachedPlayerItemStacks = new HashMap<>();

    private final String group;
    private final NamespacedKey recipe;

    public RecipeContainer(String group) {
        this.group = group;
        this.recipe = null;
        this.cachedRecipes = CCRegistry.RECIPES.getGroup(group);
    }

    public RecipeContainer(NamespacedKey recipe) {
        this.group = null;
        this.recipe = recipe;
        this.cachedRecipes = Collections.singletonList(CCRegistry.RECIPES.get(recipe));
    }

    public RecipeContainer(ICustomRecipe<?> recipe) {
        this.group = null;
        this.recipe = recipe.getNamespacedKey();
        this.cachedRecipes = Collections.singletonList(recipe);
    }

    /**
     * @param player The player to get the recipes for.
     * @return The recipes of this container the player has access to.
     */
    public List<ICustomRecipe<?>> getRecipes(Player player) {
        return CCRegistry.RECIPES.getAvailable(cachedRecipes, player); //Possible strict caching in the future?! return cachedPlayerRecipes.computeIfAbsent(player.getUniqueId(), uuid -> Registry.RECIPES.getAvailable(cachedRecipes, player));
    }

    /**
     * Checks if a player can view at least one recipe of this container.
     *
     * @param player The player to check.
     * @return True if the player can view the container.
     */
    public boolean canView(Player player) {
        return cachedRecipes.stream().anyMatch(recipe1 -> !recipe1.isHidden() && !recipe1.isDisabled() && recipe1.checkCondition("permission", new Conditions.Data(player)));
    }

    public @Nullable String getGroup() {
        return group;
    }

    public @Nullable NamespacedKey getRecipe() {
        return recipe;
    }

    public boolean isValid(Set<Material> materials) {
        return materials.isEmpty() || cachedRecipes.parallelStream().anyMatch(recipe1 -> recipe1.getResult().getChoices().parallelStream().anyMatch(customItem -> materials.contains(customItem.getItemStack().getType())));
    }

    public boolean isValid(EliteWorkbench cache) {
        var data = cache.getEliteWorkbenchData();
        return cachedRecipes.parallelStream().anyMatch(cachedRecipe -> {
            if (cachedRecipe instanceof CraftingRecipe && (RecipeType.ELITE_WORKBENCH.isInstance(cachedRecipe) || data.isAdvancedRecipes())) {
                if (RecipeType.ELITE_WORKBENCH.isInstance(cachedRecipe)) {
                    EliteWorkbenchCondition condition = cachedRecipe.getConditions().getEliteCraftingTableCondition();
                    if (condition != null && !condition.getEliteWorkbenches().contains(data.getNamespacedKey())) {
                        return false;
                    }
                    if (cachedRecipe instanceof AbstractRecipeShapeless<?, ?> shapeless) {
                        return shapeless.getFlatIngredients().size() <= cache.getCurrentGridSize() * cache.getCurrentGridSize();
                    } else {
                        CraftingRecipeEliteShaped recipe1 = (CraftingRecipeEliteShaped) cachedRecipe;
                        return recipe1.getShape().length <= cache.getCurrentGridSize() && recipe1.getShape()[0].length() <= cache.getCurrentGridSize();
                    }
                }
                return true;
            }
            return false;
        });
    }

    public String getValue() {
        return group != null ? group : recipe.toString();
    }

    public ItemStack getDisplayItem() {
        return cachedRecipes.isEmpty() ? new ItemStack(Material.STONE) : cachedRecipes.get(0).getRecipeBookItems().get(0).create();
    }

    public List<ItemStack> getDisplayItems(Player player) {
        return cachedPlayerItemStacks.computeIfAbsent(player.getUniqueId(), uuid -> getRecipes(player).stream().flatMap(recipe1 -> recipe1.getRecipeBookItems().stream()).map(CustomItem::create).distinct().toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeContainer that = (RecipeContainer) o;
        return Objects.equals(group, that.group) && Objects.equals(recipe, that.recipe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, recipe);
    }

    @Override
    public int compareTo(@NotNull RecipeContainer container) {
        return getValue().compareTo(container.getValue());
    }

    @Override
    public String toString() {
        return "RecipeContainer{" +
                "group='" + group + '\'' +
                ", recipe=" + recipe +
                '}';
    }
}
