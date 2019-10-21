package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Anvil extends RecipeData{

    private RecipePriority priority;

    private CustomAnvilRecipe.Mode mode;
    private int durability;

    private HashMap<Integer, List<CustomItem>> ingredients;

    public Anvil() {
        super();
        this.ingredients = new HashMap<>();
        this.ingredients.put(2, new ArrayList<>(Collections.singleton(new CustomItem(Material.AIR))));
        this.mode = CustomAnvilRecipe.Mode.RESULT;
        this.durability = 0;
    }

    public RecipePriority getPriority() {
        return priority;
    }

    public void setPriority(RecipePriority priority) {
        this.priority = priority;
    }

    public List<CustomItem> getResult() {
        return getIngredients(2);
    }

    public List<CustomItem> getIngredients(int slot) {
        return ingredients.getOrDefault(slot, new ArrayList<>());
    }

    public CustomAnvilRecipe.Mode getMode() {
        return mode;
    }

    public void setMode(CustomAnvilRecipe.Mode mode) {
        this.mode = mode;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }
}
