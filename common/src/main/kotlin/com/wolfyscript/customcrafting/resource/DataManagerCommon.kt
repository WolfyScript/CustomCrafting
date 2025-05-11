package com.wolfyscript.customcrafting.resource

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.wolfyscript.customcrafting.CustomCraftingCommon
import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.recipes.Ingredient
import com.wolfyscript.customcrafting.recipes.IngredientImpl
import com.wolfyscript.customcrafting.recipes.RecipeResult
import com.wolfyscript.customcrafting.recipes.RecipeResultImpl
import com.wolfyscript.customcrafting.recipes.ShapedCraftingFormulaImpl
import com.wolfyscript.customcrafting.recipes.ShapelessCraftingFormulaImpl
import com.wolfyscript.customcrafting.recipes.data.RecipeData
import com.wolfyscript.customcrafting.recipes.data.RecipeDataImpl
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper
import kotlin.jvm.java

class DataManagerCommon(val customCrafting: CustomCraftingCommon) : DataManager {
    
    override val resourceLoader: ResourceLoader = ResourceLoaderImpl(customCrafting, customCrafting.configurationManager.resourceSettings)

    override val jacksonObjectMapper: ObjectMapper = HoconMapper()

    init {
        val implementationTypeModule = SimpleModule("ImplementationTypeModule").apply {
            //

            // Recipe Components
            addAbstractTypeMapping(Ingredient::class.java, IngredientImpl::class.java)
            addAbstractTypeMapping(RecipeResult::class.java, RecipeResultImpl::class.java)
            addAbstractTypeMapping(RecipeData::class.java, RecipeDataImpl::class.java)

            // Register Crafting Recipe Type Implementations
            addAbstractTypeMapping(CraftingFormula.Shaped::class.java, ShapedCraftingFormulaImpl::class.java)
            addAbstractTypeMapping(CraftingFormula.Shapeless::class.java, ShapelessCraftingFormulaImpl::class.java)
            addAbstractTypeMapping(CraftingFormula.Shaped.Shape::class.java, ShapedCraftingFormulaImpl.ShapeImpl::class.java)
            addAbstractTypeMapping(CraftingFormula.Shaped.ShapeSymmetry::class.java, ShapedCraftingFormulaImpl.ShapeSymmetryImpl::class.java)

            // TODO: Could we use a custom annotation for this? Would it be better?

        }
        jacksonObjectMapper.registerModule(implementationTypeModule)


    }

    override fun loadData() {
        TODO("Not yet implemented")
    }
}