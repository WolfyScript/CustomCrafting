package com.wolfyscript.customcrafting.fabric.mixin;

import com.wolfyscript.customcrafting.fabric.inject.GrindstoneResultSlotsExt;
import com.wolfyscript.customcrafting.recipes.CustomRecipeGrinding;
import com.wolfyscript.customcrafting.recipes.EvaluationContextImpl;
import com.wolfyscript.customcrafting.recipes.data.RecipeEvaluationResult;
import com.wolfyscript.customcrafting.recipes.process.ProcessGrinding;
import com.wolfyscript.scafall.ScafallProvider;
import com.wolfyscript.scafall.identifier.Key;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
public class GrindstoneResultSlotsMixin implements GrindstoneResultSlotsExt {

//    TODO: getting "InvalidMixinException @Shadow field this$0 was not located in the target class ... Using refmap fabric-refmap.json"
//    @Final
//    @Shadow(remap = false) // remap does not bypass issue // aliases for synthetic fields could work, but use bypass via init for now.
//    GrindstoneMenu this$0;

    @Shadow
    @Final
    ContainerLevelAccess val$access;
    @Unique
    private GrindstoneMenu grindstoneMenu;
    @Unique
    @Nullable
    private RecipeEvaluationResult<RecipeEvaluationResult.GrindingRecipeData, CustomRecipeGrinding> resultInfo;

    /**
     * Bypass for error thrown by @Shadow field this$0
     */
    @Inject(at = @At("TAIL"), method = "<init>")
    private void initGrindstoneMenu(GrindstoneMenu this$0, Container container, int slot, int x, int y, ContainerLevelAccess par6, CallbackInfo ci) {
        grindstoneMenu = this$0;
    }

    @Inject(at = @At("HEAD"), method = "onTake", cancellable = true)
    private void takeCustomRecipeOutput(Player player, ItemStack stack, CallbackInfo ci) {
        if (resultInfo == null || resultInfo.getRecipe().getValue() == null) {
            return;
        }
        ci.cancel();

        var recipe = resultInfo.getRecipe().getValue();
        var data = resultInfo.getData();
        var wrapper = ScafallProvider.Companion.get().getMinecraftWrapper();
        var lvl = player.level();
        var key = Key.Companion.key(lvl.dimension().location().getNamespace(), lvl.dimension().location().getPath());
        var context = new EvaluationContextImpl(wrapper.wrapMcPlayer(player), wrapper.wrapVec3(player.position(), key));

        var totalYield = data.getYield() - data.getPenalty();
        if (totalYield > 0) {
            val$access.execute((level, blockPos) -> {
                if (level instanceof ServerLevel) {
                    ExperienceOrb.award((ServerLevel)level, Vec3.atCenterOf(blockPos), totalYield);
                }
                level.levelEvent(1042, blockPos, 0);
            });
        }

        if (recipe.getProcess() instanceof ProcessGrinding.FixedResultProcessGrinding fixedResultProcess) {
            fixedResultProcess.getResult().runActions(context, 1);
        }

        var base = data.bySlot(0);
        if (base != null) {
            grindstoneMenu.getSlot(0).getItem().shrink(base.getMatchedItemStackRef().getAmount());
        }

        var addition = data.bySlot(1);
        if (addition != null) {
            grindstoneMenu.getSlot(1).getItem().shrink(addition.getMatchedItemStackRef().getAmount());
        }

        resultInfo = null;
    }

    @Override
    public void setResultInfo(@Nullable RecipeEvaluationResult<RecipeEvaluationResult.GrindingRecipeData, CustomRecipeGrinding> resultInfo) {
        this.resultInfo = resultInfo;
    }

    @Override
    public @Nullable RecipeEvaluationResult<RecipeEvaluationResult.GrindingRecipeData, CustomRecipeGrinding> getResultInfo() {
        return resultInfo;
    }
}
