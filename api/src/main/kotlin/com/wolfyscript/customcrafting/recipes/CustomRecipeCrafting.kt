package com.wolfyscript.customcrafting.recipes

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

interface CustomRecipeCrafting : CustomRecipe<CustomRecipeCrafting> {

    val formula: CraftingFormula

    val result: RecipeResult

}

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "type")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
// This could in theory be expanded to allow custom types from a type registry, but for now there is no need for that
@JsonSubTypes(
    JsonSubTypes.Type(value = CraftingFormula.Shapeless::class, name = "shapeless"),
    JsonSubTypes.Type(value = CraftingFormula.Shaped::class, name = "shaped")
)
@JsonPropertyOrder(value = ["type"])
interface CraftingFormula {

    val ingredients: List<Ingredient>

    /**
     * A crafting formula with a list of ingredients that can be arranged in any order
     */
    interface Shapeless : CraftingFormula {

    }

    /**
     * A crafting formula that requires ingredients to be arranged in a specified shape.
     *
     * The shape may allow ingredients to be arranged mirrored (see [ShapeSymmetry]).
     */
    interface Shaped : CraftingFormula {

        val shape: Shape

        val symmetry: ShapeSymmetry

        interface ShapeSymmetry {
            val horizontal: Boolean
            val vertical: Boolean
            val rotate: Boolean
        }

        interface Shape {

            /**
             * A list of rows each with character keys that are associated with ingredients.
             * The length of the list is fixed at 3, and so is the length of each row.
             */
            val rows: List<String>

        }
        
    }
    
}
