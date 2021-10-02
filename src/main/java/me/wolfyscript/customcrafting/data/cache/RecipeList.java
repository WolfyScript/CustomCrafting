package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import org.bukkit.inventory.*;

import java.util.List;

public class RecipeList {

    private String namespace;
    private int page;
    private RecipeType<?> filterType;
    private Class<? extends Recipe> filterClass;

    public RecipeList() {
        this.namespace = null;
        this.page = 0;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public int getPage(int maxPages) {
        if (this.page > maxPages) {
            this.page = maxPages;
        }
        return this.page;
    }

    public int getMaxPages(int size) {
        return size / 45 + (size % 45 > 0 ? 1 : 0);
    }

    public void setFilterType(RecipeType<?> filterType) {
        this.filterType = filterType;
        this.filterClass = switch (filterType.getType()) {
            case CRAFTING_SHAPED -> ShapedRecipe.class;
            case CRAFTING_SHAPELESS -> ShapelessRecipe.class;
            case SMOKER -> SmokingRecipe.class;
            case FURNACE -> FurnaceRecipe.class;
            case BLAST_FURNACE -> BlastingRecipe.class;
            case CAMPFIRE -> CampfireRecipe.class;
            case SMITHING -> SmithingRecipe.class;
            case STONECUTTER -> StonecuttingRecipe.class;
            default -> null;
        };
    }

    public RecipeType<?> getFilterType() {
        return filterType;
    }

    public void filterCustomRecipes(List<CustomRecipe<?>> recipes) {
        if (filterType != null) {
            recipes.removeIf(recipe -> !filterType.isInstance(recipe));
        }
    }

    public void filterVanillaRecipes(List<Recipe> recipes) {
        if (filterClass != null) {
            recipes.removeIf(recipe -> !filterClass.isInstance(recipe));
        }
    }
}
