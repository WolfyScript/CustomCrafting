package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.*;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeResolver;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "key")
@JsonPropertyOrder("key")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties("id")
public abstract class Condition implements Keyed {

    @JsonProperty("key")
    private final NamespacedKey key;
    protected Conditions.Option option;

    @JsonIgnore
    private ItemStack iconEnabled;
    @JsonIgnore
    private ItemStack iconDisabled;
    @JsonIgnore
    private List<Conditions.Option> availableOptions;

    protected Condition(NamespacedKey key) {
        this.key = key;
    }

    public Conditions.Option getOption() {
        return option;
    }

    public void setOption(Conditions.Option option) {
        this.option = option;
    }

    public List<Conditions.Option> getAvailableOptions() {
        return availableOptions;
    }

    protected void setAvailableOptions(Conditions.Option... options) {
        if (options != null) {
            availableOptions = Arrays.asList(options);
        }
    }

    public void toggleOption() {
        int index = availableOptions.indexOf(this.option);
        if (index < availableOptions.size() - 1) {
            index++;
        } else {
            index = 0;
        }
        this.option = availableOptions.get(index);
    }

    public abstract boolean check(ICustomRecipe<?> recipe, Conditions.Data data);

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    @Deprecated
    public String getId() {
        return key.toString();
    }

    public ItemStack getIconEnabled() {
        return iconEnabled;
    }

    protected void setIconEnabled(ItemStack iconEnabled) {
        this.iconEnabled = iconEnabled;
    }

    public ItemStack getIconDisabled() {
        return iconDisabled;
    }

    protected void setIconDisabled(ItemStack iconDisabled) {
        this.iconDisabled = iconDisabled;
    }

    protected void setIcons(ItemStack iconEnabled, ItemStack iconDisabled) {
        this.iconEnabled = iconEnabled;
        this.iconDisabled = iconDisabled;
    }
}
