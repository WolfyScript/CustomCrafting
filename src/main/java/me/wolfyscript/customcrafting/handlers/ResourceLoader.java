package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class ResourceLoader implements Comparable<ResourceLoader>, Keyed {

    protected final NamespacedKey key;
    protected final CustomCrafting customCrafting;
    protected final MainConfig config;
    protected final WolfyUtilities api;
    protected final ObjectMapper objectMapper;
    private int priority = 0;

    protected ResourceLoader(CustomCrafting customCrafting, NamespacedKey key) {
        this.key = key;
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

    public abstract boolean save(CustomRecipe<?> recipe);

    public abstract boolean save(CustomItem item);

    public abstract boolean delete(CustomRecipe<?> recipe);

    public abstract boolean delete(CustomItem item);

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceLoader that)) return false;
        return priority == that.priority && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, priority);
    }

    @Override
    public int compareTo(@NotNull ResourceLoader other) {
        return Integer.compare(other.priority, priority);
    }
}
