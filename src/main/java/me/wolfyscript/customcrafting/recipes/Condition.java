package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public abstract class Condition implements Serializable {

    private static final long serialVersionUID = 42069L;

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

    @Override
    public String toString() {
        return option.toString();
    }

    public void fromString(String value) {
        this.option = Conditions.Option.valueOf(value);
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
