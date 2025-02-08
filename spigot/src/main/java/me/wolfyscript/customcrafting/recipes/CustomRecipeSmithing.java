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

import com.wolfyscript.utilities.dependency.DependencySource;
import com.wolfyscript.utilities.verification.Verifier;
import com.wolfyscript.utilities.verification.VerifierBuilder;
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
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.*;
import me.wolfyscript.lib.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
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
        final Verifier<CustomRecipeSmithing> VERIFIER = VerifierBuilder.<CustomRecipeSmithing>object(RecipeType.SMITHING.getNamespacedKey())
                .name(container -> "Smithing Recipe" + container.value().map(recipe -> " [" + recipe.getNamespacedKey() + "]").orElse(""))
                .object(recipe -> recipe.result, Result.VERIFIER)
                .object(Function.identity(), builder -> builder
                        .name(container -> "Ingredients")
                        .object(i -> i.template, Ingredient.VERIFIER, override -> override
                                .name("Template")
                                .optional()
                        )
                        .object(i -> i.base, Ingredient.VERIFIER, step -> step
                                .name("Base")
                                .optional()
                        )
                        .object(i -> i.addition, Ingredient.VERIFIER, step -> step
                                .name("Addition")
                                .optional()
                        )
                        .require(1) // Make sure at least one ingredient is valid
                        .validate(result -> {
                            if (!result.currentType().isValid()) {
                                result.fault("At least one ingredient (Template, Base, or Addition) must be valid!");
                            }
                        })
                )
                .build();
        CustomCrafting.inst().getRegistries().getVerifiers().register(VERIFIER);
    }

    private static final String KEY_BASE = "base";
    private static final String KEY_ADDITION = "addition";

    private static final boolean IS_1_20 = ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0));
    public static final int RESULT_SLOT = IS_1_20 ? 3 : 2;
    public static final int BASE_SLOT = IS_1_20 ? 1 : 0;
    public static final int ADDITION_SLOT = IS_1_20 ? 2 : 1;

    @DependencySource
    private Ingredient template;
    @DependencySource
    private Ingredient base;
    @DependencySource
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
        setOnlyChangeMaterial(node.path("onlyChangeMaterial").asBoolean(false));
        setPreserveEnchants(node.path("preserve_enchants").asBoolean(true));
        setPreserveDamage(node.path("preserveDamage").asBoolean(true));
    }

    @JsonCreator
    public CustomRecipeSmithing(
            @JsonProperty("key") @JacksonInject("key") NamespacedKey key,
            @JacksonInject("customcrafting") CustomCrafting customCrafting,
            @JsonProperty("preserveEnchants") boolean preserveEnchants,
            @JsonProperty("preserveDamage") boolean preserveDamage,
            @JsonProperty("preserveTrim") boolean preserveTrim,
            @JsonProperty("onlyChangeMaterial") boolean onlyChangeMaterial
    ) {
        super(key, customCrafting, RecipeType.SMITHING);
        this.template = ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0)) ? new Ingredient(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) : new Ingredient();
        this.base = new Ingredient();
        this.addition = new Ingredient();
        this.result = new Result();
        setOnlyChangeMaterial(onlyChangeMaterial);
        setPreserveEnchants(preserveEnchants);
        setPreserveDamage(preserveDamage);
        setPreserveTrim(preserveTrim);
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

        final Optional<IngredientData> baseData = getBase().checkChoices(base, isCheckNBT()).map(reference -> new IngredientData(BASE_SLOT, BASE_SLOT, getBase(), reference, base));
        if (baseData.isEmpty() && (!getBase().isAllowEmpty() || !ItemUtils.isAirOrNull(base))) return null;

        final Optional<IngredientData> additionData = getAddition().checkChoices(addition, isCheckNBT()).map(reference -> new IngredientData(ADDITION_SLOT, ADDITION_SLOT, getAddition(), reference, addition));
        if (additionData.isEmpty() && (!getAddition().isAllowEmpty() || !ItemUtils.isAirOrNull(addition))) return null;

        Optional<IngredientData> templateData = Optional.empty();
        if (IS_1_20 && getTemplate() != null) {
            templateData = getTemplate().checkChoices(template, isCheckNBT()).map(reference -> new IngredientData(0, 0, getTemplate(), reference, template));
            if (templateData.isEmpty() && (!getTemplate().isAllowEmpty() || !ItemUtils.isAirOrNull(template))) return null; // Do not allow recipe, when there is an invalid item, or when slot is not allowed to be empty!
        }

        final IngredientData[] ingredientData = IS_1_20 ?
                new IngredientData[]{templateData.orElse(null), baseData.orElse(null), additionData.orElse(null)} :
                new IngredientData[]{baseData.orElse(null), additionData.orElse(null)};
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
            internalMergeAdapters.add(new EnchantMergeAdapter(true, true, true, false));
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
            return new org.bukkit.inventory.SmithingTransformRecipe(
                    ICustomVanillaRecipe.toPlaceholder(getNamespacedKey()).bukkit(),
                    getResult().getItemStack(),
                    getRecipeChoiceFor(getTemplate()),
                    getRecipeChoiceFor(getBase()),
                    getRecipeChoiceFor(getAddition())
            );
        }

    }

    private static RecipeChoice getRecipeChoiceFor(Ingredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            // Need a placeholder item to bypass Spigots dumbass Air and emtpy choice check.
            // Thanks for nothing Spigot... now this needs to be handled by the PrepareSmithingEvent!
            // Note: Minecraft does support emtpy Ingredients for SmithingRecipes!! Just fucking Spigot doesn't (or at least not anymore for whatever reason)!
            // Paper has a RecipeChoice.emtpy(), however that would require compilation against Java 21.
            return new RecipeChoice.MaterialChoice(Material.BARRIER);
        }
        List<Material> choices = ingredient.choicesStream().map(reference -> reference.referencedStack().getType()).collect(Collectors.toList());
        if (ingredient.isAllowEmpty()) {
            //choices.add(Material.AIR); // This no longer works due to minecraft not being able to serialize Air stacks
        }
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
