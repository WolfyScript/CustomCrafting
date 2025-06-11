package com.wolfyscript.customcrafting.resource

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.recipes.*
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeDataImpl
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper
import com.wolfyscript.scafall.config.jackson.registerScafallModule
import java.io.File

class DataManagerCommon(val customCrafting: CustomCraftingCommon, val directory: File) : DataManager {

    override val resourceLoader: ResourceLoader = ResourceLoaderImpl(
        customCrafting,
        customCrafting.configurationManager.resourceSettings,
        File(directory, "resources")
    )

    override val jacksonObjectMapper: ObjectMapper = HoconMapper()

    init {
        val implementationTypeModule = SimpleModule("ImplementationTypeModule").apply {
            //
            addAbstractTypeMapping(RecipeType::class.java, RecipeTypeImpl::class.java)

            // Recipe Components
            addAbstractTypeMapping(Ingredient::class.java, IngredientImpl::class.java)
            addAbstractTypeMapping(RecipeResult::class.java, RecipeResultImpl::class.java)
            addAbstractTypeMapping(RecipeData::class.java, RecipeDataImpl::class.java)
            addAbstractTypeMapping(RecipeConditions::class.java, RecipeConditionsImpl::class.java)
            addAbstractTypeMapping(ResultModifier::class.java, ResultModifierImpl::class.java)
            addAbstractTypeMapping(
                ResultModifier.Transformation::class.java,
                ResultModifierImpl.ResultModifierTransformationImpl::class.java
            )
            addAbstractTypeMapping(RecipeChoices::class.java, RecipeChoicesImpl::class.java)

            // Register Crafting Recipe Type Implementations
            addAbstractTypeMapping(CustomRecipeCrafting::class.java, CustomRecipeCraftingImpl::class.java)
            addAbstractTypeMapping(CraftingFormula.Shaped::class.java, ShapedCraftingFormulaImpl::class.java)
            addAbstractTypeMapping(CraftingFormula.Shapeless::class.java, ShapelessCraftingFormulaImpl::class.java)
            addAbstractTypeMapping(
                CraftingFormula.Shaped.Shape::class.java,
                ShapedCraftingFormulaImpl.ShapeImpl::class.java
            )
            addAbstractTypeMapping(
                CraftingFormula.Shaped.ShapeSymmetry::class.java,
                ShapedCraftingFormulaImpl.ShapeSymmetryImpl::class.java
            )

            // Cooking Recipes
            addAbstractTypeMapping(CustomRecipeCooking::class.java, CustomRecipeCookingImpl::class.java)
            addAbstractTypeMapping(
                CustomRecipeCooking.WorkstationProcessing.Smelting::class.java,
                CustomRecipeCookingImpl.WorkstationProcessingSmelting::class.java
            )
            addAbstractTypeMapping(
                CustomRecipeCooking.WorkstationProcessing.Blasting::class.java,
                CustomRecipeCookingImpl.WorkstationProcessingBlasting::class.java
            )
            addAbstractTypeMapping(
                CustomRecipeCooking.WorkstationProcessing.Smoking::class.java,
                CustomRecipeCookingImpl.WorkstationProcessingSmoking::class.java
            )
            addAbstractTypeMapping(
                CustomRecipeCooking.WorkstationProcessing.Campfire::class.java,
                CustomRecipeCookingImpl.WorkstationProcessingCampfire::class.java
            )

            // Mixing Recipes
            addAbstractTypeMapping(CustomRecipeMixing::class.java, CustomRecipeMixingImpl::class.java)
            addAbstractTypeMapping(
                CustomRecipeMixing.CampfireRequirement::class.java,
                CustomRecipeMixingImpl.CampfireRequirementImpl::class.java
            )
            addAbstractTypeMapping(
                CustomRecipeMixing.FluidRequirement::class.java,
                CustomRecipeMixingImpl.FluidRequirementImpl::class.java
            )

            // Grinding Recipes
            addAbstractTypeMapping(CustomRecipeGrinding::class.java, CustomRecipeGrindingImpl::class.java)

            // Repairing Recipes
            addAbstractTypeMapping(CustomRecipeRepairing::class.java, CustomRecipeRepairingImpl::class.java)

            // Smithing Recipes
            addAbstractTypeMapping(CustomRecipeSmithing::class.java, CustomRecipeSmithingImpl::class.java)
            addAbstractTypeMapping(
                CustomRecipeSmithing.CopyOptions::class.java,
                CustomRecipeSmithingImpl.CopyOptionsImpl::class.java
            )

            // Stonecutting Recipes
            addAbstractTypeMapping(CustomRecipeStonecutting::class.java, CustomRecipeStonecuttingImpl::class.java)

            // TODO: Could we use a custom annotation for this? Would it be better?

        }
        jacksonObjectMapper.registerModule(implementationTypeModule)
        jacksonObjectMapper.registerKotlinModule()
        jacksonObjectMapper.registerScafallModule()
    }

    override fun loadData() {
        resourceLoader.loadResources()

        resourceLoader.verifyResources()
    }
}