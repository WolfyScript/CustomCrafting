package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public abstract class Condition {

    protected Conditions.Option option;

    private ItemStack iconEnabled, iconDisabled;
    private final String id;
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

    public abstract boolean check(ICustomRecipe<?,?> recipe, Conditions.Data data);

    public String getId() {
        return id;
    }

    /**
     *
     *
     * @param gen the current JsonGenerator
     * @throws IOException
     */
    public void writeJson(@NotNull JsonGenerator gen) throws IOException {

    }

    public void readFromJson(JsonNode node){
        //Not every Condition needs this method as they just extend this clas with no extra variables!
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
