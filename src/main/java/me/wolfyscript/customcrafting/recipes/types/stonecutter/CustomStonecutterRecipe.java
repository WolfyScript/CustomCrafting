package me.wolfyscript.customcrafting.recipes.types.stonecutter;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.ArrayList;
import java.util.List;

public class CustomStonecutterRecipe extends StonecuttingRecipe implements CustomRecipe<StonecutterConfig> {

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

    @Override
    public CustomRecipe save(ConfigAPI configAPI, String namespace, String key) {
        return null;
    }

    @Override
    public CustomRecipe save(StonecutterConfig config) {
        return null;
    }

    public List<CustomItem> getSource() {
        return source;
    }

    @Override
    public StonecutterConfig getConfig() {
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
