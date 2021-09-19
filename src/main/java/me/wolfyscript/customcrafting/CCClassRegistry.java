package me.wolfyscript.customcrafting;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.recipes.items.extension.ResultExtension;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.utilities.util.ClassRegistry;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface CCClassRegistry<T extends Keyed> extends ClassRegistry<T> {

    RecipeConditionsRegistry RECIPE_CONDITIONS = new RecipeConditionsRegistry();
    SimpleClassRegistry<MergeAdapter> RESULT_MERGE_ADAPTERS = new SimpleClassRegistry<>();
    SimpleClassRegistry<ResultExtension> RESULT_EXTENSIONS = new SimpleClassRegistry<>();

    class RecipeConditionsRegistry extends SimpleClassRegistry<Condition<?>> {

        /**
         * Registers the {@link Condition} and it's optional {@link Condition.AbstractGUIComponent} for the GUI settings.
         *
         * @param condition The {@link Condition} to register.
         * @param component An optional {@link Condition.AbstractGUIComponent}, to edit settings inside the GUI.
         * @param <C>       The type of the {@link Condition}
         */
        public <C extends Condition<C>> void register(NamespacedKey key, Class<C> condition, @Nullable Condition.AbstractGUIComponent<C> component) {
            Preconditions.checkArgument(condition != null, "Condition must not be null!");
            Condition.registerGUIComponent(Objects.requireNonNull(key, "Invalid NamespacedKey! Key cannot be null!"), component);
            super.register(key, condition);
        }

        /**
         * Registers a {@link Condition} into the {@link CCClassRegistry#RECIPE_CONDITIONS} without an additional GUI component.
         *
         * @param condition The {@link Condition} to register.
         */
        @Override
        public void register(Condition<?> condition) {
            this.register(condition.getNamespacedKey(), condition.getClass(), null);
        }

        @Override
        public void register(NamespacedKey key, Class<? extends Condition<?>> value) {
            Condition.registerGUIComponent(Objects.requireNonNull(key, "Invalid NamespacedKey! Key cannot be null!"), null);
            super.register(key, value);
        }

        @Override
        public void register(NamespacedKey key, Condition<?> value) {
            this.register(key, value.getClass(), null);
        }
    }

}
