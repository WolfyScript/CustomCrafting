package me.wolfyscript.customcrafting.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public abstract class Condition {

    protected Conditions.Option option;

    private ItemStack iconEnabled, iconDisabled;
    private String id;
    private List<Conditions.Option> availableOptions;

    protected Condition(String id) {
        this.id = id;
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

    public abstract boolean check(CustomRecipe recipe, Conditions.Data data);

    public String getId() {
        return id;
    }

    public JsonElement toJsonElement() {
        JsonObject element = new JsonObject();
        element.addProperty("id", id);
        element.addProperty("option", option.toString());
        return element;
    }

    public abstract void fromJsonElement(JsonElement jsonElement);

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
