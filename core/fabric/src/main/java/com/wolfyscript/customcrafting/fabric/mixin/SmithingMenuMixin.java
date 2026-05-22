package com.wolfyscript.customcrafting.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wolfyscript.customcrafting.fabric.inject.CCResultContainerExt;
import com.wolfyscript.customcrafting.fabric.inject.RecipeInputSmithingCustomExt;
import com.wolfyscript.customcrafting.core.recipe.CustomRecipeSmithing;
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContext;
import com.wolfyscript.customcrafting.core.recipe.data.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.core.recipe.data.RecipeInput;
import com.wolfyscript.customcrafting.core.recipe.evaluation.EvaluationContextState;
import com.wolfyscript.scafall.identifier.Key;
import com.wolfyscript.scafall.wrappers.minecraft.ItemStackWrappersKt;
import com.wolfyscript.scafall.wrappers.minecraft.PlayerWrappersKt;
import com.wolfyscript.scafall.wrappers.minecraft.PositionWrappersKt;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {

    @Shadow
    @Final
    private Level level;

    private SmithingMenuMixin(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(menuType, containerId, inventory, access, slotDefinition);
    }

    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"
        ),
        method = "createResult"
    )
    private void addCustomInput(CallbackInfo ci, @Local SmithingRecipeInput smithingRecipeInput) {
        RecipeInput.SmithingRecipeInput customInput = RecipeInput.SmithingRecipeInput.Companion.of(
            ItemStackWrappersKt.wrap(getItems().get(0)),
            ItemStackWrappersKt.wrap(getItems().get(1)),
            ItemStackWrappersKt.wrap(getItems().get(2))
        );
        ((RecipeInputSmithingCustomExt)(Object) smithingRecipeInput).setCustomInput(customInput);
    }

    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
            shift = At.Shift.AFTER
        ),
        method = "createResult"
    )
    private void addRecipeEvalResult(CallbackInfo ci, @Local SmithingRecipeInput smithingRecipeInput) {
        var result = ((RecipeInputSmithingCustomExt)(Object) smithingRecipeInput).getResultInfo();
        if (!(resultSlots instanceof CCResultContainerExt resultSlotsExt)) return;
        resultSlotsExt.setResultInfo(result);
    }

    @Inject(
        at = @At(value = "HEAD"),
        method = "createResult"
    )
    private void enterEvalContext(CallbackInfo ci) {
        EvaluationContextState.INSTANCE.enter(EvaluationContext.of(PlayerWrappersKt.wrap(player), PositionWrappersKt.wrap(player.position(), Key.fromMc(level.dimension().identifier()))));
    }

    @Inject(
        method = "createResult",
        at = @At("TAIL")
    )
    private void exitEvalContext(CallbackInfo ci) {
        EvaluationContextState.INSTANCE.exit();
    }

    @Inject(
        method = "onTake",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/ResultContainer;awardUsedRecipes(Lnet/minecraft/world/entity/player/Player;Ljava/util/List;)V",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void shrinkCustomRecipe(Player player, ItemStack stack, CallbackInfo ci) {
        var resultInfo = ((CCResultContainerExt) resultSlots).getResultInfo();
        if (resultInfo == null || resultInfo.getRecipe().getValue() == null) return;
        ci.cancel(); // Return before vanilla logic

        var context = EvaluationContext.of(PlayerWrappersKt.wrap(player), PositionWrappersKt.wrap(player.position(), Key.fromMc(level.dimension().identifier())));
        shrinkCustomIngredient(0, context, resultInfo);
        shrinkCustomIngredient(1, context, resultInfo);
        shrinkCustomIngredient(2, context, resultInfo);

        var recipe = resultInfo.getRecipe().getValue();
        if (recipe instanceof CustomRecipeSmithing customRecipe) {
            customRecipe.getResult().runActions(context, 1);
        }

        this.access.execute((level, blockPos) -> level.levelEvent(1044, blockPos, 0));
    }

    @Unique
    private void shrinkCustomIngredient(int index, EvaluationContext context, RecipeEvaluationResult<?,?> resultInfo) {
        var data = resultInfo.getData();
        var ingredientData = data.bySlot(index);
        if (ingredientData != null) {
            var existing = inputSlots.getItem(index);
            if (!existing.isEmpty()) {
                existing = ingredientData.getSelectedIngredient().shrink(
                    ItemStackWrappersKt.wrap(existing),
                    1,
                    ingredientData.getMatchedItemStackRef(),
                    context,
                    resultInfo
                ).unwrap();
                inputSlots.setItem(index, existing);
            }
        }
    }
}
