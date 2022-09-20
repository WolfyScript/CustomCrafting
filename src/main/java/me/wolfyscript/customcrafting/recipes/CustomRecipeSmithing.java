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

package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.ButtonContainerIngredient;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeBook;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JacksonInject;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.lib.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CustomRecipeSmithing extends CustomRecipe<CustomRecipeSmithing> {

    private static final String KEY_BASE = "base";
    private static final String KEY_ADDITION = "addition";

    private Ingredient base;
    private Ingredient addition;

    private boolean preserveEnchants;
    private boolean preserveDamage;
    private boolean onlyChangeMaterial; //Only changes the material of the item. Useful to make vanilla style recipes.

    public CustomRecipeSmithing(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.type = RecipeType.SMITHING;
        setBase(ItemLoader.loadIngredient(node.path(KEY_BASE)));
        setAddition(ItemLoader.loadIngredient(node.path(KEY_ADDITION)));
        preserveEnchants = node.path("preserve_enchants").asBoolean(true);
        preserveDamage = node.path("preserveDamage").asBoolean(true);
        onlyChangeMaterial = node.path("onlyChangeMaterial").asBoolean(false);
    }

    @JsonCreator
    public CustomRecipeSmithing(@JsonProperty("key") @JacksonInject("key") NamespacedKey key, @JacksonInject("customcrafting") CustomCrafting customCrafting) {
        super(key, customCrafting, RecipeType.SMITHING);
        this.base = new Ingredient();
        this.addition = new Ingredient();
        this.result = new Result();
        this.preserveEnchants = true;
        this.preserveDamage = true;
        this.onlyChangeMaterial = false;
    }

    @Deprecated
    public CustomRecipeSmithing(NamespacedKey key) {
        this(key, CustomCrafting.inst());
    }

    private CustomRecipeSmithing(CustomRecipeSmithing customRecipeSmithing) {
        super(customRecipeSmithing);
        this.result = customRecipeSmithing.getResult();
        this.base = customRecipeSmithing.getBase();
        this.addition = customRecipeSmithing.getAddition();
        this.preserveEnchants = customRecipeSmithing.isPreserveEnchants();
        this.preserveDamage = customRecipeSmithing.isPreserveDamage();
        this.onlyChangeMaterial = customRecipeSmithing.isOnlyChangeMaterial();
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return slot == 0 ? getBase() : getAddition();
    }

    public Ingredient getAddition() {
        return addition;
    }

    public void setAddition(@NotNull Ingredient addition) {
        Preconditions.checkArgument(!addition.isEmpty(), "Invalid Addition! Recipe must have non-air addition!");
        this.addition = addition;
    }

    public Ingredient getBase() {
        return base;
    }

    public void setBase(@NotNull Ingredient base) {
        Preconditions.checkArgument(!base.isEmpty(), "Invalid Base ingredient! Recipe must have non-air base ingredient!");
        this.base = base;
    }

    public boolean isPreserveEnchants() {
        return preserveEnchants;
    }

    public void setPreserveEnchants(boolean preserveEnchants) {
        this.preserveEnchants = preserveEnchants;
    }

    public boolean isPreserveDamage() {
        return preserveDamage;
    }

    public void setPreserveDamage(boolean preserveDamage) {
        this.preserveDamage = preserveDamage;
    }

    public boolean isOnlyChangeMaterial() {
        return onlyChangeMaterial;
    }

    public void setOnlyChangeMaterial(boolean onlyChangeMaterial) {
        this.onlyChangeMaterial = onlyChangeMaterial;
    }

    @Override
    public CustomRecipeSmithing clone() {
        return new CustomRecipeSmithing(this);
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(10))).setVariants(guiHandler, getBase());
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(13))).setVariants(guiHandler, getAddition());
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(23))).setVariants(guiHandler, this.getResult());
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        var cluster = guiWindow.getCluster();
        event.setButton(19, ButtonContainerIngredient.key(cluster, 10));
        event.setButton(21, ButtonContainerIngredient.key(cluster, 13));
        event.setButton(23, new NamespacedKey(ClusterRecipeBook.KEY, "smithing"));
        event.setButton(25, ButtonContainerIngredient.key(cluster, 23));
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeBooleanField("preserve_enchants", preserveEnchants);
        gen.writeBooleanField("preserveDamage", preserveDamage);
        gen.writeBooleanField("onlyChangeMaterial", onlyChangeMaterial);
        gen.writeObjectField(KEY_RESULT, result);
        gen.writeObjectField(KEY_BASE, base);
        gen.writeObjectField(KEY_ADDITION, addition);
    }
}
