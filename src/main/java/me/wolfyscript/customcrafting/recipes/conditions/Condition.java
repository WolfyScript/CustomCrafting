/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.recipes.conditions;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.recipe_creator.RecipeCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.MenuConditions;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JacksonInject;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonAutoDetect;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonInclude;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeResolver;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "key")
@JsonPropertyOrder("key")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties("id")
public abstract class Condition<C extends Condition<C>> implements Keyed {

    public static String getLangKey(String condition, String subPath) {
        return "recipe_conditions." + condition + "." + subPath;
    }

    private static final Map<NamespacedKey, AbstractGUIComponent<?>> GUI_COMPONENTS = new HashMap<>();

    /**
     * Registers the optional {@link AbstractGUIComponent} of the {@link Condition} for the GUI settings.
     *
     * @param component An optional {@link AbstractGUIComponent}, to edit settings inside the GUI.
     * @param <C>       The type of the {@link Condition}
     */
    public static <C extends Condition<C>> void registerGUIComponent(NamespacedKey key, @Nullable AbstractGUIComponent<C> component) {
        Preconditions.checkArgument(!GUI_COMPONENTS.containsKey(key), "Can't register GUI Component for condition \"" + key + "\"! Value already exists!");
        GUI_COMPONENTS.putIfAbsent(key, component != null ? component : new IconGUIComponent<>(Material.COMMAND_BLOCK, key.toString(), List.of()));
    }

    /**
     * @return An unmodifiable Map containing the available {@link AbstractGUIComponent}s.
     */
    public static Map<NamespacedKey, AbstractGUIComponent<?>> getGuiComponents() {
        return Map.copyOf(GUI_COMPONENTS);
    }

    public static AbstractGUIComponent<?> getGuiComponent(NamespacedKey key) {
        return GUI_COMPONENTS.get(key);
    }

    @JsonProperty("key")
    private final NamespacedKey key;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    protected Conditions.Option option = Conditions.Option.EXACT;
    @JsonIgnore
    private List<Conditions.Option> availableOptions;
    @JsonIgnore
    @JacksonInject("customcrafting")
    protected CustomCrafting customCrafting;

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

    public abstract boolean check(CustomRecipe<?> recipe, Conditions.Data data);

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    @Deprecated
    public String getId() {
        return key.toString();
    }

    public boolean isApplicable(CustomRecipe<?> recipe) {
        return true;
    }

    @JsonIgnore
    public AbstractGUIComponent<C> getGuiComponent() {
        return (AbstractGUIComponent<C>) GUI_COMPONENTS.get(key);
    }

    public void render(GuiUpdate<CCCache> update, CCCache cache, RecipeCache<?> recipe) {
        if (getGuiComponent() != null) {
            getGuiComponent().renderMenu(update, cache, (C) this, recipe);
        }
    }

    /**
     * GUI Component to edit conditions of this type. <br>
     * This GUI Component will be initialized once when the ConditionsMenu is initiated and will be used for all the instances of conditions of this type. <br>
     * Therefore, the implementation must be static! <br>
     */
    public abstract static class AbstractGUIComponent<C extends Condition<C>> {

        private final Material icon;
        private final String name;
        private final List<String> description;
        private String nameLangKey = null;
        private String descriptionLangKey = null;

        @Deprecated
        protected AbstractGUIComponent(Material icon, String name, List<String> description) {
            this.icon = icon;
            this.name = name;
            this.description = description;
            this.nameLangKey = name;
            this.descriptionLangKey = "";
        }

        protected AbstractGUIComponent(Material icon, String nameKey, String descriptionKey) {
            this.icon = icon;
            this.name = nameKey;
            this.description = Collections.singletonList(descriptionKey);
            this.nameLangKey = nameKey;
            this.descriptionLangKey = descriptionKey;
        }

        public abstract void init(MenuConditions menu, WolfyUtilities api);

        public abstract void renderMenu(GuiUpdate<CCCache> update, CCCache cache, C condition, RecipeCache<?> recipe);

        public Material getIcon() {
            return icon;
        }

        @Deprecated
        public String getName() {
            return name;
        }

        public Component getDisplayName() {
            return CustomCrafting.inst().getApi().getLanguageAPI().getComponent(nameLangKey);
        }

        public List<Component> getDescriptionComponents() {
            return CustomCrafting.inst().getApi().getLanguageAPI().getComponents(descriptionLangKey);
        }

        public List<String> getDescription() {
            return description;
        }

        public boolean shouldRender(RecipeType<?> type) {
            return true;
        }
    }

    public static class IconGUIComponent<C extends Condition<C>> extends AbstractGUIComponent<C> {

        protected IconGUIComponent(Material icon, String name, List<String> description) {
            super(icon, name, description);
        }

        @Override
        public void init(MenuConditions menu, WolfyUtilities api) {
            //We only have an icon!
        }

        @Override
        public void renderMenu(GuiUpdate<CCCache> update, CCCache cache, C condition, RecipeCache<?> recipe) {
            //We only have an icon and no menu to render!
        }
    }

    public static class FunctionalGUIComponent<C extends Condition<C>> extends AbstractGUIComponent<C> {

        private final BiConsumer<MenuConditions, WolfyUtilities> initConsumer;
        private final RenderConsumer<C> renderConsumer;

        @Deprecated
        protected FunctionalGUIComponent(Material icon, String name, List<String> description, BiConsumer<MenuConditions, WolfyUtilities> init, RenderConsumer<C> render) {
            super(icon, name, description);
            this.initConsumer = init;
            this.renderConsumer = render;
        }

        protected FunctionalGUIComponent(Material icon, String nameLangKey, String descriptionLangKey, BiConsumer<MenuConditions, WolfyUtilities> init, RenderConsumer<C> render) {
            super(icon, nameLangKey, descriptionLangKey);
            this.initConsumer = init;
            this.renderConsumer = render;
        }

        @Override
        public void init(MenuConditions menu, WolfyUtilities api) {
            if (initConsumer != null) {
                initConsumer.accept(menu, api);
            }
        }

        public void renderMenu(GuiUpdate<CCCache> update, CCCache cache, C condition, RecipeCache<?> recipe) {
            if (renderConsumer != null) {
                renderConsumer.accept(update, cache, condition, recipe);
            }
        }

        public interface RenderConsumer<C extends Condition<C>> {

            void accept(GuiUpdate<CCCache> update, CCCache cache, C condition, RecipeCache<?> recipe);

        }
    }
}
