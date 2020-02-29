package me.wolfyscript.customcrafting.recipes.types.blast_furnace;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class CustomBlastRecipe extends BlastingRecipe implements CustomCookingRecipe<BlastingConfig> {

    private boolean exactMeta, hidden;

    private RecipePriority priority;
    private List<CustomItem> result;
    private List<CustomItem> source;
    private String id;
    private BlastingConfig config;
    private Conditions conditions;

    public CustomBlastRecipe(BlastingConfig config) {
        super(new NamespacedKey(config.getNamespace(), config.getName()), config.getResult().get(0), new RecipeChoice.ExactChoice(new ArrayList<>(config.getSource())), config.getXP(), config.getCookingTime());
        this.id = config.getId();
        this.config = config;
        this.result = config.getResult();
        this.source = config.getSource();
        this.priority = config.getPriority();
        this.exactMeta = config.isExactMeta();
        this.conditions = config.getConditions();
        this.hidden = config.isHidden();
        setGroup(config.getGroup());
    }

    public CustomBlastRecipe() {
        super(new NamespacedKey("null", "null"), new ItemStack(Material.STONE), Material.STONE, 0, 0);
        this.config = null;
        this.id = "";
        this.result = new ArrayList<>();
        this.source = new ArrayList<>();
        this.priority = RecipePriority.NORMAL;
        this.exactMeta = true;
        this.conditions = new Conditions();
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return result;
    }

    @Override
    public List<CustomItem> getSource() {
        return source;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public BlastingConfig getConfig() {
        return config;
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }

    @Override
    public Conditions getConditions() {
        return conditions;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.BLAST_FURNACE;
    }
}
