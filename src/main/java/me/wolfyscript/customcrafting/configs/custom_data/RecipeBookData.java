package me.wolfyscript.customcrafting.configs.custom_data;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.DeserializationContext;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.io.IOException;
import java.util.Objects;

public class RecipeBookData extends CustomData implements Cloneable {

    private boolean enabled;

    protected RecipeBookData(NamespacedKey namespacedKey) {
        super(namespacedKey);
        this.enabled = false;
    }

    protected RecipeBookData(RecipeBookData recipeBookData) {
        super(recipeBookData);
        this.enabled = recipeBookData.enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void writeToJson(CustomItem customItem, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeBooleanField("enabled", enabled);
    }

    @Override
    protected void readFromJson(CustomItem customItem, JsonNode node, DeserializationContext deserializationContext) throws IOException {
        setEnabled(node.get("enabled").asBoolean(false));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeBookData)) return false;
        if (!super.equals(o)) return false;
        RecipeBookData that = (RecipeBookData) o;
        return enabled == that.enabled;
    }

    @Override
    public RecipeBookData clone() {
        return new RecipeBookData(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), enabled);
    }

    public static class Provider extends CustomData.Provider<RecipeBookData> {

        public Provider() {
            super(CustomCrafting.RECIPE_BOOK, RecipeBookData.class);
        }

    }
}
