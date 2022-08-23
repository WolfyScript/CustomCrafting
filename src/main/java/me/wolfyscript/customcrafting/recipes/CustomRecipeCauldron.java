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
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.data.persistent.CauldronBlockData;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.gui.recipebook.ButtonContainerIngredient;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeBook;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.recipes.conditions.PermissionCondition;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
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
    private Result[] additionalResults;

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
        this.additionalResults = new Result[] { new Result(), new Result(), new Result() };
        JsonNode ingredientsNode = node.path("ingredient");
        this.ingredients = new ArrayDeque<>();
        if (ingredientsNode.isObject()) {
            ItemLoader.loadIngredient(node.path("ingredients")).getChoices().stream().map(customItem -> {
                Ingredient ingredient = new Ingredient(customItem.getApiReference());
                ingredient.buildChoices();
                return ingredient;
            }).forEach(ingredients::add);
        } else {
            Streams.stream(ingredientsNode.elements()).map(ItemLoader::loadIngredient).forEach(this::addIngredients);
        }
    }

    @JsonCreator
    public CustomRecipeCauldron(@JsonProperty("key") @JacksonInject("key") NamespacedKey key, @JacksonInject("customcrafting") CustomCrafting customCrafting) {
        super(key, customCrafting, RecipeType.CAULDRON);
        this.result = new Result();
        this.additionalResults = new Result[]{ new Result(), new Result(), new Result() };
        this.ingredients = new ArrayDeque<>();
        this.xp = 0;
        this.cookingTime = 80;
        this.fluidLevel = 0;
        this.canCookInWater = true;
        this.canCookInLava = false;
        setCampfire(true);
        setSoulCampfire(false);
        this.signalFire = false;
    }

    @Deprecated
    public CustomRecipeCauldron(NamespacedKey key) {
        this(key, CustomCrafting.inst());
    }

    public CustomRecipeCauldron(CustomRecipeCauldron customRecipeCauldron) {
        super(customRecipeCauldron);
        this.result = customRecipeCauldron.getResult().clone();
        this.additionalResults = Arrays.stream(customRecipeCauldron.additionalResults).map(result1 -> result1 == null ? null : result1.clone()).toArray(value -> new Result[3]);
        this.ingredients = new ArrayDeque<>();
        if (customRecipeCauldron.ingredients != null) {
            addIngredients(customRecipeCauldron.ingredients.stream().map(Ingredient::clone).toList());
        }
        this.xp = customRecipeCauldron.getXp();
        this.cookingTime = customRecipeCauldron.getCookingTime();

        this.canCookInWater = customRecipeCauldron.isCanCookInWater();
        this.canCookInLava = customRecipeCauldron.isCanCookInLava();
        this.fluidLevel = customRecipeCauldron.getFluidLevel();

        setCampfire(customRecipeCauldron.isCampfire());
        setSoulCampfire(customRecipeCauldron.isSoulCampfire());
        this.signalFire = customRecipeCauldron.isSignalFire();
    }

    @JsonSetter("additionalResults")
    public void setAdditionalResults(Result[] additionalResults) {
        Preconditions.checkArgument(additionalResults.length <= 3, "Recipe cannot have more than 3 additional results!");
        if (additionalResults.length != 3) {
            Result[] filled = new Result[3];
            for (int i = 0; i < filled.length; i++) {
                if (i < additionalResults.length) {
                    filled[i] = additionalResults[i];
                }
            }
            this.additionalResults = filled;
        } else {
            this.additionalResults = additionalResults;
        }
    }

    public Result[] getAdditionalResults() {
        return additionalResults;
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

    public void setCanCookInLava(boolean canCookInLava) {
        this.canCookInLava = canCookInLava;
    }

    public boolean isCanCookInWater() {
        return canCookInWater;
    }

    public void setCanCookInWater(boolean canCookInWater) {
        this.canCookInWater = canCookInWater;
    }

    public boolean isRequiresLitCampfire() {
        return requiresLitCampfire;
    }

    public boolean isCampfire() {
        return campfire;
    }

    public void setCampfire(boolean campfire) {
        this.campfire = campfire;
        this.requiresLitCampfire = campfire || soulCampfire;
    }

    public boolean isSoulCampfire() {
        return soulCampfire;
    }

    public void setSoulCampfire(boolean soulCampfire) {
        this.soulCampfire = soulCampfire;
        this.requiresLitCampfire = campfire || soulCampfire;
    }

    public boolean isSignalFire() {
        return signalFire;
    }

    public void setSignalFire(boolean signalFire) {
        this.signalFire = signalFire;
    }

    public int getFluidLevel() {
        return fluidLevel;
    }

    public void setFluidLevel(int fluidLevel) {
        this.fluidLevel = fluidLevel;
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
        } else if (!isCanCookInWater() && !isCanCookInLava()) {
            return !status.hasWater() && !status.hasLava();
        }
        return false;
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

    private void setIngredients(Deque<Ingredient> ingredients) {
        Preconditions.checkArgument(ingredients.size() <= maxIngredients, "Recipe cannot have more than " + maxIngredients + " ingredients!");
        this.ingredients = ingredients;
    }

    public Deque<Ingredient> getIngredients() {
        return ingredients;
    }

    @JsonSetter("ingredients")
    private void setIngredients(JsonNode ingredientsNode) {
        if (ingredientsNode.isObject()) {
            //Directly set ingredients to bypass max ingredient check, since old recipes might have more ingredients!
            ItemLoader.loadIngredient(ingredientsNode).getChoices().stream().map(customItem ->{
                Ingredient ingredient = new Ingredient(customItem.getApiReference());
                ingredient.buildChoices();
                return ingredient;
            }).forEach(ingredient -> this.ingredients.add(ingredient));
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
        Ingredient[] ingredients = getIngredients().toArray(Ingredient[]::new);

        for (int i = 5; i > -1; i--) {
            if (i < ingredients.length) {
                ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(i))).setVariants(guiHandler, ingredients[i]);
            } else {
                ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(i))).setVariants(guiHandler, Collections.singletonList(new CustomItem(Material.AIR)));
            }
        }

        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(25))).setVariants(guiHandler, getResult());
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(26))).setVariants(guiHandler, getAdditionalResults()[0]);
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(34))).setVariants(guiHandler, getAdditionalResults()[1]);
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(35))).setVariants(guiHandler, getAdditionalResults()[2]);
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        var cluster = guiWindow.getCluster();
        CCPlayerData playerStore = PlayerUtil.getStore(event.getPlayer());

        int slot = 10;
        for (int i = 5; i > -1; i--) {
            event.setButton(slot, ButtonContainerIngredient.key(cluster, i));
            slot += i == 3 ? 9-3 : i == 1 ? 9-1 : 2;
        }

        List<Condition<?>> conditions = getConditions().getValues().stream().filter(condition -> !condition.getNamespacedKey().equals(PermissionCondition.KEY)).toList();
        int startSlot = 9 / (conditions.size() + 1);
        slot = 0;
        for (Condition<?> condition : conditions) {
            event.setButton(36 + startSlot + slot, new NamespacedKey(ClusterRecipeBook.KEY, "conditions." + condition.getId()));
            slot += 2;
        }

        if (canCookInWater && canCookInLava) {
            event.setButton(36, ClusterRecipeBook.CAULDRON_COOK_WATER);
            event.setButton(37, ClusterRecipeBook.CAULDRON_COOK_LAVA);
        } else if (canCookInWater) {
            event.setButton(36, ClusterRecipeBook.CAULDRON_COOK_WATER);
        } else if (canCookInLava) {
            event.setButton(36, ClusterRecipeBook.CAULDRON_COOK_LAVA);
        } else {
            event.setButton(36, ClusterRecipeBook.CAULDRON_EMPTY);
        }
        if (canCookInWater || canCookInLava) {
            NamespacedKey backgroundBtn = playerStore.isDarkMode() ? ClusterMain.GLASS_WHITE : ClusterMain.GLASS_BLACK;
            ItemStack levelItem = new ItemStack(canCookInWater ? Material.BLUE_STAINED_GLASS_PANE : Material.ORANGE_STAINED_GLASS_PANE);
            for (int i = 0; i < 3; i++) {
                if (i < fluidLevel || canCookInLava) {
                    event.setItem(45 + i, levelItem);
                } else {
                    event.setButton(45 + i, backgroundBtn);
                }
            }
        }

        if (requiresLitCampfire) {
            if (campfire && soulCampfire) {
                event.setButton(44, ClusterRecipeBook.CAULDRON_CAMPFIRE);
                event.setButton(43, ClusterRecipeBook.CAULDRON_SOUL_CAMPFIRE);
            } else {
                event.setButton(44, campfire ? ClusterRecipeBook.CAULDRON_CAMPFIRE : ClusterRecipeBook.CAULDRON_SOUL_CAMPFIRE);
            }
            if (signalFire) {
                event.setButton(53, ClusterRecipeBook.CAULDRON_SIGNAL_FIRE);
            }
        }

        event.setButton(16, ButtonContainerIngredient.key(cluster, 25));
        event.setButton(17, ButtonContainerIngredient.key(cluster, 26));
        event.setButton(25, ButtonContainerIngredient.key(cluster, 34));
        event.setButton(26, ButtonContainerIngredient.key(cluster, 35));
    }


}
