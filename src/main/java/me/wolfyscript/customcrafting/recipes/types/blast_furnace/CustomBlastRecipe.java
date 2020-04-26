package me.wolfyscript.customcrafting.recipes.types.blast_furnace;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import org.bukkit.Material;
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
    private BlastingConfig config;
    private Conditions conditions;
    private NamespacedKey namespacedKey;

    public CustomBlastRecipe(BlastingConfig config) {
        super(new org.bukkit.NamespacedKey(config.getNamespace(), config.getName()), config.getResult().get(0), new RecipeChoice.ExactChoice(new ArrayList<>(config.getSource())), config.getXP(), config.getCookingTime());
        this.namespacedKey = config.getNamespacedKey();
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
        super(new org.bukkit.NamespacedKey("null", "null"), new ItemStack(Material.STONE), Material.STONE, 0, 0);
        this.config = null;
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
    @Deprecated
    public String getId() {
        return namespacedKey.toString();
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
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
