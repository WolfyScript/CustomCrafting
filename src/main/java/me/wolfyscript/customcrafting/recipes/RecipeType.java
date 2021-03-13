package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

public class RecipeType<C extends ICustomRecipe<?,?>> {

    /**
     * This is the RecipeType object.
     */
    private final String id;
    private final String creatorID;
    private final Class<C> clazz;
    private final Types.Type type;

    public RecipeType(Types.Type type, Class<C> clazz){
        this(type, clazz, type.toString().toLowerCase(Locale.ROOT));
    }

    public RecipeType(Types.Type type, Class<C> clazz, String creatorID){
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

    public static final class CookingRecipeType<C extends CustomCookingRecipe<?, ?>> extends RecipeType<C> {

        public CookingRecipeType(Types.Type type, Class<C> clazz) {
            super(type, clazz, "cooking");
        }
    }

    public static final class CraftingRecipeType<C extends CraftingRecipe<?>, SHAPELESS extends C, SHAPED extends C> extends RecipeType<C> {

        private final Class<SHAPELESS> shapelessClass;
        private final Class<SHAPED> shapedClass;

        public CraftingRecipeType(Types.Type type, Class<C> clazz, Class<SHAPELESS> shapelessClass, Class<SHAPED> shapedClass) {
            super(type, clazz);
            this.shapelessClass = shapelessClass;
            this.shapedClass = shapedClass;
        }

        @Override
        public C getInstance(NamespacedKey namespacedKey, JsonNode node){
            try {
                Class<? extends C> recipeClass = node.path("shapeless").asBoolean() ? shapelessClass : shapedClass;
                return recipeClass.getDeclaredConstructor(NamespacedKey.class, JsonNode.class).newInstance(namespacedKey, node);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public String toString() {
            return "CraftingRecipeType{" +
                    "shapelessClass=" + shapelessClass +
                    ", shapedClass=" + shapedClass +
                    "} " + super.toString();
        }
    }
}
