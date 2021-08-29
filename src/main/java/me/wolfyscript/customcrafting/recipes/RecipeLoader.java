package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.lang.reflect.InvocationTargetException;

public interface RecipeLoader<C extends CustomRecipe<?>> {

    String getId();

    C getInstance(NamespacedKey namespacedKey, JsonNode node) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException;

}
