package me.wolfyscript.customcrafting.recipes.types.stonecutter;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.ArrayList;
import java.util.List;

public class CustomStonecutterRecipe extends StonecuttingRecipe implements CustomRecipe<StonecutterConfig> {

    private boolean exactMeta, hidden;

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
        this.conditions = config.getConditions();
        this.hidden = config.isHidden();
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

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.STONECUTTER;
    }

    @Override
    public void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event) {
        event.setButton(0, "back");
        //TODO STONECUTTER
        event.setItem(20, getSource().get(0));
        event.setItem(24, getCustomResult().getRealItem());
        event.setButton(29, "none", "glass_green");
        event.setButton(30, "none", "glass_green");
        event.setButton(31, "recipe_book", "stonecutter");
        event.setButton(32, "none", "glass_green");
        event.setButton(33, "none", "glass_green");

    }
}
