package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

public class RecipeType<C extends ICustomRecipe<?>> {

    private final String id;
    private final String creatorID;
    private final Class<C> clazz;
    private final Types.Type type;

    public RecipeType(Types.Type type, Class<C> clazz) {
        this(type, clazz, type.toString().toLowerCase(Locale.ROOT));
    }

    public RecipeType(Types.Type type, Class<C> clazz, String creatorID) {
        this.type = type;
        this.clazz = clazz;
        this.id = type.toString().toLowerCase(Locale.ROOT);
        this.creatorID = creatorID;
        Types.values.add(this);
    }

    public String getId() {
        return id;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public Class<C> getClazz() {
        return clazz;
    }

    public boolean isInstance(ICustomRecipe<?> recipe) {
        return clazz.isInstance(recipe);
    }

    public C cast(ICustomRecipe<?> recipe) {
        return clazz.cast(recipe);
    }

    public C getInstance(NamespacedKey namespacedKey, JsonNode node) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return clazz.getDeclaredConstructor(NamespacedKey.class, JsonNode.class).newInstance(namespacedKey, node);
    }

    public Types.Type getType() {
        return type;
    }

    public String name() {
        return getType().toString();
    }

    @Override
    public String toString() {
        return "RecipeType{" +
                "id='" + id + '\'' +
                ", creatorID='" + creatorID + '\'' +
                ", clazz=" + clazz +
                ", type=" + type +
                '}';
    }

    public static final class CookingRecipeType<C extends CustomRecipeCooking<?, ?>> extends RecipeType<C> {

        public CookingRecipeType(Types.Type type, Class<C> clazz) {
            super(type, clazz, "cooking");
        }
    }

    public static class CraftingRecipeType<T extends CraftingRecipeSettings, D extends CraftingRecipe<D, T>, S extends CraftingRecipe<S, T>> extends RecipeType<CraftingRecipe<?, T>> {

        private final RecipeType<D> shaped;
        private final RecipeType<S> shapeless;
        private final Class<T> typeClass;

        public CraftingRecipeType(Types.Type type, Class<T> settingsType, RecipeType<D> shaped, RecipeType<S> shapeless) {
            super(type, (Class<CraftingRecipe<?, T>>) (Object) CraftingRecipe.class);
            this.typeClass = settingsType;
            this.shaped = shaped;
            this.shapeless = shapeless;
        }

        @Override
        public CraftingRecipe<?, T> getInstance(NamespacedKey namespacedKey, JsonNode node) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
            return node.path("shapeless").asBoolean() ? shapeless.getInstance(namespacedKey, node) : shaped.getInstance(namespacedKey, node);
        }

        @Override
        public boolean isInstance(ICustomRecipe<?> recipe) {
            return recipe instanceof CraftingRecipe<?, ?> craftingRecipe && typeClass.isInstance(craftingRecipe.getSettings());
        }

        @Override
        public CraftingRecipe<?, T> cast(ICustomRecipe<?> recipe) {
            return (CraftingRecipe<?, T>) recipe;
        }

        @Override
        public String toString() {
            return "CraftingRecipeType{" +
                    "shapelessClass=" + shapeless +
                    ", shapedClass=" + shaped +
                    "} " + super.toString();
        }
    }
}
