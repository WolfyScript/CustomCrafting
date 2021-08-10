package me.wolfyscript.customcrafting;

import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.utilities.util.ClassRegistry;
import me.wolfyscript.utilities.util.Keyed;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface CCClassRegistry<T extends Keyed> extends ClassRegistry<T> {

    RecipeConditionsRegistry RECIPE_CONDITIONS = new RecipeConditionsRegistry();

    class RecipeConditionsRegistry extends SimpleClassRegistry<Condition<?>> {

        /**
         * Registers the {@link Condition} and it's optional {@link Condition.AbstractGUIComponent} for the GUI settings.
         *
         * @param condition The {@link Condition} to register.
         * @param component An optional {@link Condition.AbstractGUIComponent}, to edit settings inside the GUI.
         * @param <C>       The type of the {@link Condition}
         */
        public <C extends Condition<C>> void register(Condition<C> condition, @Nullable Condition.AbstractGUIComponent<C> component) {
            var key = Objects.requireNonNull(condition, "Can't register condition! Condition must not be null!").getNamespacedKey();
            Condition.registerGUIComponent(key, component);
            super.register(condition);
        }

        /**
         * Registers a {@link Condition} into the {@link CCClassRegistry#RECIPE_CONDITIONS} without an additional GUI component.
         *
         * @param condition The {@link Condition} to register.
         */
        @Override
        public void register(Condition<?> condition) {
            register(condition, null);
        }

    }

}
