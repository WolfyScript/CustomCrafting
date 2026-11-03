package com.wolfyscript.customcrafting.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.wolfyscript.customcrafting.CustomCraftingProvider;
import com.wolfyscript.customcrafting.fabric.inject.RecipeManagerCustomRecipesExt;
import com.wolfyscript.customcrafting.fabric.inject.RecipeRegistrationUtils;
import com.wolfyscript.customcrafting.fabric.recipes.proxy.CustomRecipeProxyMap;
import com.wolfyscript.scafall.ScafallProvider;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.NotImplementedException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

/**
 * Integrates vanilla recipes that defer to custom recipes into the vanilla RecipeManager without modifying the original RecipeMap.
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerCustomRecipeMixin implements RecipeManagerCustomRecipesExt {

    @Unique
    private CustomRecipeProxyMap customRecipeProxies = CustomRecipeProxyMap.Companion.getEMPTY();

    @Unique
    private Collection<RecipeHolder<?>> customLearnableRecipes = List.of();

    @Shadow
    public abstract void finalizeRecipeLoading(FeatureFlagSet enabledFlags);

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    private static boolean isIngredientEnabled(final FeatureFlagSet enabledFlags, final Ingredient ingredient) {
        throw new NotImplementedException();
    }

    @Shadow
    private static List<RecipeManager.ServerDisplayInfo> unpackRecipeInfo(final Iterable<RecipeHolder<?>> recipes, final FeatureFlagSet enabledFeatures) {
        throw new NotImplementedException();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(HolderLookup.Provider registries, CallbackInfo ci) {
        if (CustomCraftingProvider.Companion.registered()) {
            initProxyRecipes();
            return;
        }
        customRecipeProxies = CustomRecipeProxyMap.Companion.getEMPTY();
        customLearnableRecipes = List.of();
    }

    @Inject(method = "finalizeRecipeLoading", at = @At(value = "INVOKE", target = "Ljava/util/Collection;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER))
    private void finalizeProxyRecipes(
            FeatureFlagSet enabledFlags,
            CallbackInfo ci,
            @Local(name = "propertySetCollectors") List<RecipeManager.IngredientCollector> propertySetCollectors,
            @Local(name = "stonecutterRecipes") List<SelectableRecipe.SingleInputEntry<StonecutterRecipe>> stonecutterRecipes
    ) {
        this.customRecipeProxies.values().forEach((recipeHolder) -> {
            Recipe<?> recipe = recipeHolder.value();
            if (!recipe.isSpecial() && recipe.placementInfo().isImpossibleToPlace()) {
                LOGGER.warn("Recipe {} can't be placed due to empty ingredients and will be ignored", recipeHolder.id().identifier());
            } else {
                propertySetCollectors.forEach((c) -> c.accept(recipe));
                if (recipe instanceof StonecutterRecipe stonecutterRecipe) {
                    if (isIngredientEnabled(enabledFlags, stonecutterRecipe.input()) && stonecutterRecipe.resultDisplay().isEnabled(enabledFlags)) {
                        stonecutterRecipes.add(new SelectableRecipe.SingleInputEntry(
                                        stonecutterRecipe.input(),
                                        new SelectableRecipe(stonecutterRecipe.resultDisplay(),
                                                Optional.of(recipeHolder)
                                        )
                                )
                        );
                    }
                }
            }
        });
    }

    @ModifyExpressionValue(
            method = "finalizeRecipeLoading",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/RecipeManager;unpackRecipeInfo(Ljava/lang/Iterable;Lnet/minecraft/world/flag/FeatureFlagSet;)Ljava/util/List;"
            ))
    private List<RecipeManager.ServerDisplayInfo> extractProxyRecipeDisplays(List<RecipeManager.ServerDisplayInfo> original, FeatureFlagSet enabledFlags) {
        var customInfo = extractCustomRecipeDisplays(original.size(), enabledFlags);
        var merged = new ArrayList<RecipeManager.ServerDisplayInfo>(customInfo.size() + original.size());
        merged.addAll(original);
        merged.addAll(customInfo);
        return merged;
    }

    /**
     * Modified version of the vanilla display recipe extraction with a custom start index to create correct display ids.
     * @param startIndex The next index to start the custom recipe displays.
     * @param enabledFeatures Minecraft's enabled features
     * @return The custom recipe displays
     */
    @Unique
    private List<RecipeManager.ServerDisplayInfo> extractCustomRecipeDisplays(int startIndex, FeatureFlagSet enabledFeatures) {
        List<RecipeManager.ServerDisplayInfo> result = new ArrayList<>();
        Object2IntMap<String> recipeGroups = new Object2IntOpenHashMap<>();

        for(RecipeHolder<?> recipeHolder : customRecipeProxies.values()) {
            Recipe<?> recipe = recipeHolder.value();
            OptionalInt groupId;
            if (recipe.group().isEmpty()) {
                groupId = OptionalInt.empty();
            } else {
                groupId = OptionalInt.of(recipeGroups.computeIfAbsent(recipe.group(), (idx) -> recipeGroups.size()));
            }

            Optional<List<Ingredient>> placementCheck;
            if (recipe.isSpecial()) {
                placementCheck = Optional.empty();
            } else {
                placementCheck = Optional.of(recipe.placementInfo().ingredients());
            }

            for(RecipeDisplay recipeDisplay : recipe.display()) {
                if (recipeDisplay.isEnabled(enabledFeatures)) {
                    int nextDisplayId = startIndex + result.size();
                    RecipeDisplayId id = new RecipeDisplayId(nextDisplayId);
                    RecipeDisplayEntry entry = new RecipeDisplayEntry(id, recipeDisplay, groupId, recipe.recipeBookCategory(), placementCheck);
                    result.add(new RecipeManager.ServerDisplayInfo(entry, recipeHolder));
                }
            }
        }
        return result;
    }

    @ModifyReturnValue(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;", at = @At("RETURN"))
    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(Optional<RecipeHolder<T>> original, final RecipeType<T> type, final I input, final Level level) {
        // Check custom recipes first to take precedence over vanilla recipes
        return customRecipeProxies.getRecipesFor(type, input, level).findFirst().or(() -> original);
    }

    @ModifyReturnValue(method = "byKey", at = @At("RETURN"))
    public Optional<RecipeHolder<?>> byKeyIncludingCustomRecipes(Optional<RecipeHolder<?>> original, final ResourceKey<Recipe<?>> recipeId) {
        return original.or(() -> Optional.ofNullable(this.customRecipeProxies.byKey(recipeId)));
    }

    @ModifyReturnValue(method = "byKeyTyped", at = @At("RETURN"))
    private <T extends Recipe<?>> @Nullable RecipeHolder<T> byKeyTypedIncludingCustomRecipes(RecipeHolder<T> original, final RecipeType<T> type, final ResourceKey<Recipe<?>> recipeId) {
        if (original != null) {
            return original;
        }
        RecipeHolder<?> recipe = this.customRecipeProxies.byKey(recipeId);
        return recipe != null && recipe.value().getType().equals(type) ? (RecipeHolder<T>) recipe : null;
    }

    @ModifyReturnValue(method = "getRecipes", at = @At("RETURN"))
    public Collection<RecipeHolder<?>> getCustomRecipes(Collection<RecipeHolder<?>> original) {
        var customRecipes = customRecipeProxies.values();
        Collection<RecipeHolder<?>> merged = new ArrayList<>(original.size() + customRecipes.size());
        merged.addAll(customRecipes);
        merged.addAll(original);
        return Collections.unmodifiableCollection(merged);
    }

    @ModifyReturnValue(method = "getLearnableRecipes", at = @At("RETURN"))
    public Collection<RecipeHolder<?>> getCustomLearnableRecipes(Collection<RecipeHolder<?>> original) {
        Collection<RecipeHolder<?>> merged = new ArrayList<>(original.size() + customLearnableRecipes.size());
        merged.addAll(customLearnableRecipes);
        merged.addAll(original);
        return Collections.unmodifiableCollection(merged);
    }

    @Override
    public void customCrafting_registerProxyRecipes() {
        initProxyRecipes();

        finalizeRecipeLoading(ScafallProvider.Companion.get().getServer().getMinecraftServer().getWorldData().enabledFeatures());
    }

    @Unique
    private void initProxyRecipes() {
        CustomCraftingProvider.Companion.get().getLogger().info("Registering Proxy Recipes");
        customRecipeProxies = CustomRecipeProxyMap.Companion.create(RecipeRegistrationUtils.INSTANCE.collectProxyRecipes());
        customLearnableRecipes = customRecipeProxies.values().stream().filter((r) -> !r.value().isSpecial()).toList();
    }

}
