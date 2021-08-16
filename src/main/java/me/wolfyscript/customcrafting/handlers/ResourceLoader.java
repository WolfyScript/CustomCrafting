package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

public abstract class ResourceLoader {

    protected final CustomCrafting customCrafting;
    protected final MainConfig config;
    protected final WolfyUtilities api;
    protected final ObjectMapper objectMapper;
    private int priority = 0;

    protected ResourceLoader(CustomCrafting customCrafting) {
        this.api = WolfyUtilities.get(customCrafting);
        this.config = customCrafting.getConfigHandler().getConfig();
        this.customCrafting = customCrafting;
        this.objectMapper = JacksonUtil.getObjectMapper();
    }

    public abstract void load();

    public void load(boolean upgrade) {
        load();
        if (upgrade) {
            api.getConsole().info("Updating Items & Recipes to the latest format..");
            save();
            api.getConsole().info("Loading updated Items & Recipes...");
            load();
        }
    }

    public void save() {
        me.wolfyscript.utilities.util.Registry.CUSTOM_ITEMS.entrySet().forEach(entry -> ItemLoader.saveItem(this, entry.getKey(), entry.getValue()));
        CCRegistry.RECIPES.values().forEach(recipe -> recipe.save(this, null));
    }

    public abstract boolean save(ICustomRecipe<?> recipe);

    public abstract boolean save(CustomItem item);

    public abstract boolean delete(ICustomRecipe<?> recipe);

    public abstract boolean delete(CustomItem item);

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
