package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.ConditionsMenu;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.*;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeResolver;
import org.bukkit.Material;

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
    protected Conditions.Option option = Conditions.Option.EXACT;

    @JsonIgnore
    private List<Conditions.Option> availableOptions;
    @JsonIgnore
    private AbstractGUIComponent guiComponent;

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

    @JsonIgnore
    public AbstractGUIComponent getGuiComponent() {
        return guiComponent;
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

    public boolean isApplicable(ICustomRecipe<?> recipe) {
        return true;
    }

    protected void setGuiComponent(AbstractGUIComponent component) {
        this.guiComponent = component;
    }

    public class AbstractGUIComponent {

        private final Material icon;
        private final String name;
        private final List<String> description;

        protected AbstractGUIComponent(Material icon, String name, List<String> description) {
            this.icon = icon;
            this.name = name;
            this.description = description;
        }

        public void init(ConditionsMenu menu, WolfyUtilities api) {

        }

        public void renderMenu(GuiUpdate<CCCache> update, CCCache cache, ICustomRecipe<?> recipe) {


        }

        public Material getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }

        public List<String> getDescription() {
            return description;
        }
    }
}
