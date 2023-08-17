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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.ButtonContainerIngredient;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeBook;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.SmithingData;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.adapters.ArmorTrimMergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.adapters.DamageMergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.adapters.EnchantMergeAdapter;
import me.wolfyscript.customcrafting.recipes.validator.ValidationContainer;
import me.wolfyscript.customcrafting.recipes.validator.Validator;
import me.wolfyscript.customcrafting.recipes.validator.ValidatorBuilder;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.*;
import me.wolfyscript.lib.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomRecipeSmithing extends CustomRecipe<CustomRecipeSmithing> implements ICustomVanillaRecipe<SmithingRecipe> {

    static {
        final Validator<CustomRecipeSmithing> VALIDATOR = ValidatorBuilder.<CustomRecipeSmithing>object(RecipeType.SMITHING.getNamespacedKey()).def()
                .name(container -> "Smithing Recipe" + container.value().map(customRecipeSmithing -> " [" + customRecipeSmithing.getNamespacedKey() + "]").orElse(""))
                .object(recipe -> recipe.result, initStep -> initStep.use(Result.VALIDATOR).name(container -> "Result"))
                .object(Function.identity(), init -> init.def()
                        .name(container -> "Ingredients")
                        .object(i -> i.template, step -> step.def().name(container -> "Template")
                                .optional()
                                .object(Function.identity(), iInit -> iInit.use(Ingredient.VALIDATOR)))
                        .object(i -> i.base, step -> step.def().name(container -> "Base")
                                .optional()
                                .object(Function.identity(), iInit -> iInit.use(Ingredient.VALIDATOR)))
                        .object(i -> i.addition, step -> step.def().name(container -> "Addition")
                                .optional()
                                .object(Function.identity(), iInit -> iInit.use(Ingredient.VALIDATOR)))
                        // Make sure at least one ingredient is valid/pending
                        .require(1)
                        .validate(container -> {
                            if (container.type() == ValidationContainer.ResultType.INVALID) {
                                return container.update().fault("No ingredients could be loaded! At least one ingredient (Template, Base, or Addition) must be available!");
                            }
                            container.children().forEach(child -> {
                                if (child.type() == ValidationContainer.ResultType.INVALID) {
                                    child.update().type(ValidationContainer.ResultType.VALID).clearFaults();
                                    child.children().get(0).update().type(ValidationContainer.ResultType.VALID).clearFaults();
                                }
                            });
                            if (container.type() == ValidationContainer.ResultType.PENDING) {
                                return container.update().fault("At least one ingredient is still pending!");
                            }
                            return container.update();
                        }))
                .build();
        CustomCrafting.inst().getRegistries().getValidators().register(VALIDATOR);
    }

    private static final String KEY_BASE = "base";
    private static final String KEY_ADDITION = "addition";

    private static final boolean IS_1_20 = ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0));
    public static final int RESULT_SLOT = IS_1_20 ? 3 : 2;
    public static final int BASE_SLOT = IS_1_20 ? 1 : 0;
    public static final int ADDITION_SLOT = IS_1_20 ? 2 : 1;

    private Ingredient template;
    private Ingredient base;
    private Ingredient addition;

    private boolean preserveEnchants;
    private boolean preserveDamage;
    private boolean preserveTrim;
    private boolean onlyChangeMaterial; //Only changes the material of the item. Useful to make vanilla style recipes.

    @JsonIgnore
    private List<MergeAdapter> internalMergeAdapters = new ArrayList<>(3);

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
        this.template = ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0)) ? new Ingredient(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) : new Ingredient();
        this.base = new Ingredient();
        this.addition = new Ingredient();
        this.result = new Result();
        this.preserveEnchants = true;
        this.preserveDamage = true;
        this.preserveTrim = ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0));
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
        this.preserveTrim = customRecipeSmithing.isPreserveTrim();
        this.onlyChangeMaterial = customRecipeSmithing.isOnlyChangeMaterial();
    }

    @JsonIgnore
    public List<MergeAdapter> getInternalMergeAdapters() {
        return Collections.unmodifiableList(internalMergeAdapters);
    }

    public SmithingData check(Player player, InventoryView view, ItemStack template, ItemStack base, ItemStack addition) {
        if (!checkConditions(Conditions.Data.of(player, view))) return null;

        IngredientData templateData = null;
        IngredientData baseData = null;
        IngredientData additionData = null;

        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0)) && getTemplate() != null) {
            Optional<CustomItem> templateCustom = getTemplate().check(template, isCheckNBT());
            if (templateCustom.isPresent()) {
                templateData = new IngredientData(0, 0, getTemplate(), templateCustom.get(), template);
            } else if (!getTemplate().isAllowEmpty()) return null;
        }

        Optional<CustomItem> baseCustom = getBase().check(base, isCheckNBT());
        if (baseCustom.isPresent()) {
            baseData = new IngredientData(BASE_SLOT, BASE_SLOT, getBase(), baseCustom.get(), base);
        } else if (!getBase().isAllowEmpty()) return null;

        Optional<CustomItem> additionCustom = getAddition().check(addition, isCheckNBT());
        if (additionCustom.isPresent()) {
            additionData = new IngredientData(ADDITION_SLOT, ADDITION_SLOT, getAddition(), additionCustom.get(), base);
        } else if (!getAddition().isAllowEmpty()) return null;

        IngredientData[] ingredientData;
        if (IS_1_20) {
            ingredientData = new IngredientData[]{templateData, baseData, additionData};
        } else {
            ingredientData = new IngredientData[]{baseData, additionData};
        }
        return new SmithingData(this, ingredientData);
    }


    @Override
    public Ingredient getIngredient(int slot) {
        return slot == 0 ? getBase() : getAddition();
    }

    public Ingredient getAddition() {
        return addition;
    }

    public void setAddition(@NotNull Ingredient addition) {
        // Preconditions.checkArgument(!addition.isEmpty(), "Invalid Addition! Recipe must have non-air addition!");
        this.addition = addition;
    }

    public Ingredient getBase() {
        return base;
    }

    public void setBase(@NotNull Ingredient base) {
        // Preconditions.checkArgument(!base.isEmpty(), "Invalid Base ingredient! Recipe must have non-air base ingredient!");
        this.base = base;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonGetter("template")
    public Ingredient getTemplate() {
        return template;
    }

    @JsonSetter("template")
    public void setTemplate(Ingredient template) {
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
            if (template == null) {
                customCrafting.getLogger().warning("Smithing recipe '" + namespacedKey + "' has no template ingredient set! Using Netherite Upgrade Template instead!");
                customCrafting.getLogger().warning("Specify an empty ingredient to allow the recipe to work without template!");
                template = new Ingredient(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
            }
            this.template = template;
        }
    }

    public boolean isPreserveEnchants() {
        return preserveEnchants;
    }

    public void setPreserveEnchants(boolean preserveEnchants) {
        this.preserveEnchants = preserveEnchants;
        if (!onlyChangeMaterial && preserveEnchants) {
            internalMergeAdapters.add(new EnchantMergeAdapter());
        } else if (!preserveEnchants) {
            internalMergeAdapters.removeIf(mergeAdapter -> mergeAdapter.getClass().equals(EnchantMergeAdapter.class));
        }
    }

    public boolean isPreserveDamage() {
        return preserveDamage;
    }

    public void setPreserveDamage(boolean preserveDamage) {
        this.preserveDamage = preserveDamage;
        if (!onlyChangeMaterial && preserveEnchants) {
            internalMergeAdapters.add(new DamageMergeAdapter());
        } else if (!preserveDamage) {
            internalMergeAdapters.removeIf(mergeAdapter -> mergeAdapter.getClass().equals(DamageMergeAdapter.class));
        }
    }

    public boolean isPreserveTrim() {
        return preserveTrim;
    }

    public void setPreserveTrim(boolean preserveTrim) {
        this.preserveTrim = preserveTrim;
        if (!onlyChangeMaterial && preserveTrim) {
            internalMergeAdapters.add(new ArmorTrimMergeAdapter());
        } else if (!preserveTrim) {
            internalMergeAdapters.removeIf(mergeAdapter -> mergeAdapter.getClass().equals(ArmorTrimMergeAdapter.class));
        }
    }

    public boolean isOnlyChangeMaterial() {
        return onlyChangeMaterial;
    }

    public void setOnlyChangeMaterial(boolean onlyChangeMaterial) {
        this.onlyChangeMaterial = onlyChangeMaterial;
        if (onlyChangeMaterial) {
            internalMergeAdapters.clear();
        }
    }

    @Override
    public CustomRecipeSmithing clone() {
        return new CustomRecipeSmithing(this);
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
            ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(0))).setVariants(guiHandler, getTemplate());
        }
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(1))).setVariants(guiHandler, getBase());
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(2))).setVariants(guiHandler, getAddition());
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(3))).setVariants(guiHandler, this.getResult());
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        var cluster = guiWindow.getCluster();
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
            event.setButton(19, ButtonContainerIngredient.key(cluster, 0));
            event.setButton(20, ButtonContainerIngredient.key(cluster, 1));
        } else {
            event.setButton(19, ButtonContainerIngredient.key(cluster, 1));
        }
        event.setButton(21, ButtonContainerIngredient.key(cluster, 2));
        event.setButton(23, new NamespacedKey(ClusterRecipeBook.KEY, "smithing"));
        event.setButton(25, ButtonContainerIngredient.key(cluster, 3));
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

    @Override
    public SmithingRecipe getVanillaRecipe() {
        /*
         Smithing recipes need to be registered into minecraft, so that you can place the ingredients into the inventory.
         ExactChoices cannot be used as those would rely on vanilla MC to compare the items. So we'll just use MaterialChoices to use our own checks.
         */
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
            return new Instantiate1_20Recipe().create1_20PlaceholderRecipe();
        }
        return new SmithingRecipe(ICustomVanillaRecipe.toPlaceholder(getNamespacedKey()).bukkit(), getResult().getItemStack(), getRecipeChoiceFor(getBase()), getRecipeChoiceFor(getAddition()));
    }

    /**
     * A little hack to make it work on pre-1.20 servers without a ClassNotFoundException.
     */
    private final class Instantiate1_20Recipe {

        private SmithingRecipe create1_20PlaceholderRecipe() {
            return new org.bukkit.inventory.SmithingTransformRecipe(ICustomVanillaRecipe.toPlaceholder(getNamespacedKey()).bukkit(), getResult().getItemStack(), getRecipeChoiceFor(getTemplate()), getRecipeChoiceFor(getBase()), getRecipeChoiceFor(getAddition()));
        }

    }

    private static RecipeChoice getRecipeChoiceFor(Ingredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) return new RecipeChoice.MaterialChoice(Material.AIR);
        List<Material> choices = ingredient.getChoicesStream().map(customItem -> customItem.create().getType()).collect(Collectors.toList());
        if (ingredient.isAllowEmpty()) choices.add(Material.AIR);
        return new RecipeChoice.MaterialChoice(choices);
    }

    @Override
    public boolean isVisibleVanillaBook() {
        return vanillaBook;
    }

    @Override
    public void setVisibleVanillaBook(boolean vanillaBook) {
        this.vanillaBook = vanillaBook;
    }

    @Override
    public boolean isAutoDiscover() {
        return autoDiscover;
    }

    @Override
    public void setAutoDiscover(boolean autoDiscover) {
        this.autoDiscover = autoDiscover;
    }
}
