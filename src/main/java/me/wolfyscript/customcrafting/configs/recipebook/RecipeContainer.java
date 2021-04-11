package me.wolfyscript.customcrafting.configs.recipebook;

import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.EliteWorkbenchCondition;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class RecipeContainer implements Comparable<RecipeContainer> {

    private final List<ICustomRecipe<?, ?>> cachedRecipes;
    private final Map<UUID, List<ICustomRecipe<?, ?>>> cachedPlayerRecipes = new HashMap<>();
    private final Map<UUID, List<ItemStack>> cachedPlayerItemStacks = new HashMap<>();

    private final String group;
    private final NamespacedKey recipe;

    public RecipeContainer(String group) {
        this.group = group;
        this.recipe = null;
        this.cachedRecipes = Registry.RECIPES.getGroup(group);
    }

    public RecipeContainer(NamespacedKey recipe) {
        this.group = null;
        this.recipe = recipe;
        this.cachedRecipes = Collections.singletonList(Registry.RECIPES.get(recipe));
    }

    public RecipeContainer(ICustomRecipe<?, ?> recipe) {
        this.group = null;
        this.recipe = recipe.getNamespacedKey();
        this.cachedRecipes = Collections.singletonList(recipe);
    }

    public List<ICustomRecipe<?, ?>> getRecipes(Player player) {
        return Registry.RECIPES.getAvailable(cachedRecipes, player); //Possible strict caching in the future?! return cachedPlayerRecipes.computeIfAbsent(player.getUniqueId(), uuid -> Registry.RECIPES.getAvailable(cachedRecipes, player));
    }

    public boolean canView(Player player) {
        return cachedRecipes.stream().anyMatch(recipe1 -> recipe1.getConditions().getByID("permission") == null || recipe1.getConditions().getByID("permission").check(recipe1, new Conditions.Data(player, null, null)));
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
        EliteWorkbenchData data = cache.getEliteWorkbenchData();
        return cachedRecipes.parallelStream().anyMatch(recipe -> {
            if (recipe instanceof CraftingRecipe && (recipe instanceof EliteCraftingRecipe || data.isAdvancedRecipes())) {
                if (recipe instanceof EliteCraftingRecipe) {
                    EliteWorkbenchCondition condition = recipe.getConditions().getEliteCraftingTableCondition();
                    if (condition != null && !condition.getOption().equals(Conditions.Option.IGNORE) && !condition.getEliteWorkbenches().contains(data.getNamespacedKey())) {
                        return false;
                    }
                    if (((EliteCraftingRecipe) recipe).isShapeless()) {
                        return ((EliteCraftingRecipe) recipe).getIngredients().size() <= cache.getCurrentGridSize() * cache.getCurrentGridSize();
                    } else {
                        ShapedEliteCraftRecipe recipe1 = (ShapedEliteCraftRecipe) recipe;
                        return recipe1.getHeight() <= cache.getCurrentGridSize() && recipe1.getWidth() <= cache.getCurrentGridSize();
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
        return cachedPlayerItemStacks.computeIfAbsent(player.getUniqueId(), uuid -> getRecipes(player).stream().flatMap(recipe1 -> recipe1.getRecipeBookItems().stream()).map(CustomItem::create).distinct().collect(Collectors.toList()));
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
