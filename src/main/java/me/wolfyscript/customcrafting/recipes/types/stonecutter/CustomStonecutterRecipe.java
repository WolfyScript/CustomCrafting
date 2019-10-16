package me.wolfyscript.customcrafting.recipes.types.stonecutter;

import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.ArrayList;
import java.util.List;

public class CustomStonecutterRecipe extends StonecuttingRecipe implements CustomRecipe {

    private boolean exactMeta;

    private StonecutterConfig config;
    private String id;
    private List<CustomItem> result;
    private List<CustomItem> source;
    private RecipePriority priority;
    private Conditions conditions;

    public CustomStonecutterRecipe(StonecutterConfig config) {
        super(new NamespacedKey(config.getNamespace(), config.getName()), config.getResult().get(0), new RecipeChoice.ExactChoice(new ArrayList<>(config.getSource())));
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.priority = config.getPriority();
        this.exactMeta = config.isExactMeta();
        this.source = config.getSource();
        setGroup(config.getGroup());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return result;
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }

    @Override
    public void load() {
        //NO LOADING NEEDED!
    }

    public List<CustomItem> getSource() {
        return source;
    }

    @Override
    public void save() {
        //NO SAVING NEEDED!
    }

    @Override
    public RecipeConfig getConfig() {
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
}
