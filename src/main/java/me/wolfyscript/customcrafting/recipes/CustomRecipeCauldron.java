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
import com.google.common.collect.Streams;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.persistent.CauldronBlockData;
import me.wolfyscript.customcrafting.gui.recipebook.ButtonContainerIngredient;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeBook;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.recipes.conditions.PermissionCondition;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JacksonInject;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.lib.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomRecipeCauldron extends CustomRecipe<CustomRecipeCauldron> {

    private static final int maxIngredients = 6;


    private int cookingTime;
    private int xp;
    private Deque<Ingredient> ingredients;

    private boolean canCookInLava;
    private boolean canCookInWater;
    private int fluidLevel;

    private boolean campfire;
    private boolean soulCampfire;
    private boolean requiresLitCampfire;
    private boolean signalFire;

    public CustomRecipeCauldron(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.xp = node.path("exp").asInt(0);
        this.cookingTime = node.path("cookingTime").asInt(60);
        this.fluidLevel = node.path("waterLevel").asInt(1);
        this.canCookInLava = false;
        this.canCookInWater = node.path("water").asBoolean(true);
        this.campfire = this.requiresLitCampfire = node.path("fire").asBoolean(true);
        JsonNode ingredientsNode = node.path("ingredient");
        this.ingredients = new ArrayDeque<>();
        if (ingredientsNode.isObject()) {
            ItemLoader.loadIngredient(node.path("ingredients")).getChoices().stream().map(customItem -> new Ingredient(customItem.getApiReference())).forEach(ingredients::add);
        } else {
            Streams.stream(ingredientsNode.elements()).map(ItemLoader::loadIngredient).forEach(this::addIngredients);
        }
    }

    @JsonCreator
    public CustomRecipeCauldron(@JsonProperty("key") @JacksonInject("key") NamespacedKey key, @JacksonInject("customcrafting") CustomCrafting customCrafting) {
        super(key, customCrafting, RecipeType.CAULDRON);
        this.result = new Result();
        this.ingredients = new ArrayDeque<>();
        this.xp = 0;
        this.cookingTime = 80;
        this.fluidLevel = 0;
        this.canCookInWater = true;
    }

    @Deprecated
    public CustomRecipeCauldron(NamespacedKey key) {
        this(key, CustomCrafting.inst());
    }

    public CustomRecipeCauldron(CustomRecipeCauldron customRecipeCauldron) {
        super(customRecipeCauldron);
        this.result = customRecipeCauldron.getResult();
        this.ingredients = new ArrayDeque<>();
        if (customRecipeCauldron.ingredients != null) {
            addIngredients(customRecipeCauldron.ingredients.stream().map(Ingredient::clone).toList());
        }
        this.xp = customRecipeCauldron.getXp();
        this.cookingTime = customRecipeCauldron.getCookingTime();

        this.canCookInWater = customRecipeCauldron.isCanCookInWater();
        this.canCookInLava = customRecipeCauldron.isCanCookInLava();
        this.fluidLevel = customRecipeCauldron.getWaterLevel();

        this.campfire = customRecipeCauldron.isCampfire();
        this.soulCampfire = customRecipeCauldron.isSoulCampfire();
        this.signalFire = customRecipeCauldron.isSignalFire();
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public boolean isCanCookInLava() {
        return canCookInLava;
    }

    public boolean isCanCookInWater() {
        return canCookInWater;
    }

    public boolean isRequiresLitCampfire() {
        return requiresLitCampfire;
    }

    public boolean isCampfire() {
        return campfire;
    }

    public boolean isSoulCampfire() {
        return soulCampfire;
    }

    public boolean isSignalFire() {
        return signalFire;
    }

    public int getFluidLevel() {
        return fluidLevel;
    }

    @Deprecated
    public boolean needsFire() {
        return requiresLitCampfire;
    }

    @Deprecated
    public void setNeedsFire(boolean needsFire) {
        this.campfire = this.requiresLitCampfire = needsFire;
    }

    @Deprecated
    public int getWaterLevel() {
        return fluidLevel;
    }

    @Deprecated
    public void setWaterLevel(int waterLevel) {
        this.fluidLevel = waterLevel;
    }

    @Deprecated
    public boolean needsWater() {
        return canCookInWater;
    }

    @Deprecated
    public void setNeedsWater(boolean needsWater) {
        this.canCookInWater = needsWater;
    }

    public boolean checkRecipeStatus(CauldronBlockData.CauldronStatus status) {
        if ((status.hasCampfire() && isCampfire()) || (status.hasSoulCampfire() && isSoulCampfire())) {
            if ((isRequiresLitCampfire() && !status.isLit()) || (isSignalFire() && !status.isSignalFire())) {
                return false;
            }
        } else if (isSoulCampfire() || isCampfire()) {
            return false;
        }
        if ((isCanCookInLava() && status.hasLava()) || (isCanCookInWater() && status.hasWater())) {
            return status.getLevel() >= fluidLevel;
        }
        return !isCanCookInLava() && !isCanCookInWater();
    }

    public boolean checkRecipe(List<ItemStack> items, CauldronBlockData.CauldronStatus status) {
        if (!checkRecipeStatus(status)) return false;
        int inputI = 0;
        for (Ingredient ingredient : ingredients) {
            ItemStack input = items.get(inputI);
            Optional<CustomItem> checkResult = ingredient.check(input, isCheckNBT());
            if (checkResult.isPresent()) {
                if (checkResult.get().getAmount() == input.getAmount()) {
                    inputI++;
                    continue;
                }
                return false;
            }
            if (!ingredient.isAllowEmpty()) {
                return false;
            }
        }
        return true;
    }

    @JsonIgnore
    @Deprecated
    public CustomItem getHandItem() {
        return new CustomItem(Material.AIR);
    }

    @JsonIgnore
    @Deprecated
    public void setHandItem(CustomItem handItem) {
        // This is no longer used!
    }

    @JsonIgnore
    @Deprecated
    public boolean dropItems() {
        return false;
    }

    @JsonIgnore
    @Deprecated
    public void setDropItems(boolean dropItems) {
        // This is no longer used!
    }

    @JsonIgnore
    public Ingredient getIngredient() {
        return getIngredient(0);
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return this.ingredients.toArray(new Ingredient[0])[slot];
    }

    @JsonIgnore
    public void setIngredient(Ingredient ingredient) {
        addIngredients(ingredient);
    }

    public void addIngredients(Ingredient... ingredients) {
        addIngredients(Arrays.asList(ingredients));
    }

    public void addIngredients(List<Ingredient> ingredients) {
        Preconditions.checkArgument(this.ingredients.size() + ingredients.size() <= maxIngredients, "Recipe cannot have more than " + maxIngredients + " ingredients!");
        ingredients.forEach(ingredient -> {
            ingredient.buildChoices();
            if (!ingredient.isEmpty()) {
                this.ingredients.add(ingredient);
            }
        });
    }

    private void setIngredients(ArrayDeque<Ingredient> ingredients) {
        Preconditions.checkArgument(this.ingredients.size() <= maxIngredients, "Recipe cannot have more than " + maxIngredients + " ingredients!");
        this.ingredients = ingredients;
    }

    public Deque<Ingredient> getIngredients() {
        return ingredients;
    }

    @JsonSetter
    private void setIngredients(JsonNode ingredientsNode) {
        if (ingredientsNode.isObject()) {
            //Directly set ingredients to bypass max ingredient check, since old recipes might have more ingredients!
            ItemLoader.loadIngredient(ingredientsNode).getChoices().stream().map(customItem -> new Ingredient(customItem.getApiReference())).forEach(ingredient -> this.ingredients.add(ingredient));
        } else {
            //But disallow it for newly created recipes!
            Streams.stream(ingredientsNode.elements()).map(ItemLoader::loadIngredient).forEach(this::addIngredients);
        }
    }

    @Override
    public CustomRecipeCauldron clone() {
        return new CustomRecipeCauldron(this);
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeNumberField("exp", xp);
        gen.writeNumberField("cookingTime", cookingTime);
        gen.writeObjectField("result", this.result);
        gen.writeObjectField("ingredients", ingredients);
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        Ingredient ingredients = getIngredient();
        int invSlot;
        for (int i = 0; i < 6; i++) {
            invSlot = 10 + i + (i / 3) * 6;
            if (i < ingredients.size()) {
                ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(invSlot))).setVariants(guiHandler, Collections.singletonList(ingredients.getChoices().get(i)));
            } else {
                ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(invSlot))).setVariants(guiHandler, Collections.singletonList(new CustomItem(Material.AIR)));
            }
        }
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(25))).setVariants(guiHandler, getResult());
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        var cluster = guiWindow.getCluster();
        int invSlot;
        for (int i = 0; i < 6; i++) {
            invSlot = 10 + i + (i / 3) * 6;
            event.setButton(invSlot, ButtonContainerIngredient.key(cluster, invSlot));
        }
        List<Condition<?>> conditions = getConditions().getValues().stream().filter(condition -> !condition.getNamespacedKey().equals(PermissionCondition.KEY)).toList();
        int startSlot = 9 / (conditions.size() + 1);
        int slot = 0;
        for (Condition<?> condition : conditions) {
            event.setButton(36 + startSlot + slot, new NamespacedKey(ClusterRecipeBook.KEY, "conditions." + condition.getId()));
            slot += 2;
        }
        if (needsWater()) {
            event.setButton(23, new NamespacedKey(cluster.getId(), "cauldron.water.enabled"));
        } else {
            event.setButton(23, new NamespacedKey(ClusterRecipeBook.KEY, "cauldron.water.disabled"));
        }
        event.setButton(32, new NamespacedKey(ClusterRecipeBook.KEY, needsFire() ? "cauldron.fire.enabled" : "cauldron.fire.disabled"));
        event.setButton(25, ButtonContainerIngredient.key(cluster, 25));
    }


}
