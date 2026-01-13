package com.wolfyscript.customcrafting.editor.ui.recipe_editor.crafting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeCraftingModel
import com.wolfyscript.customcrafting.editor.ui.recipe_editor.state.UIIngredientPreview
import com.wolfyscript.customcrafting.editor.ui.recipe_editor.state.toPreview
import com.wolfyscript.customcrafting.editor.ui.withCraftingModel
import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxWidth
import com.wolfyscript.viewportl.gui.compose.modifier.height
import com.wolfyscript.viewportl.gui.compose.modifier.width
import com.wolfyscript.viewportl.gui.elements.Button
import com.wolfyscript.viewportl.gui.elements.Column
import com.wolfyscript.viewportl.gui.elements.Icon
import com.wolfyscript.viewportl.gui.elements.Row
import com.wolfyscript.viewportl.gui.model.Store
import com.wolfyscript.viewportl.gui.model.store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.UUID

private fun RecipeCraftingModel.CraftingFormulaModel<*>.toUIState(): FormulaStore.FormulaState {
    return when(this) {
        is RecipeCraftingModel.CraftingFormulaModel.Shaped -> {
            FormulaStore.FormulaState.Shaped(
                FormulaStore.FormulaState.Shaped.Shape(shape.symmetry, shape.trim),
                ingredients.map { it?.toPreview() },
            )
        }

        is RecipeCraftingModel.CraftingFormulaModel.Shapeless -> {
            FormulaStore.FormulaState.Shapeless(
                ingredients.mapNotNull { it.toPreview() },
            )
        }

        else -> error("Unknown formula model: $this")
    }
}

private class FormulaStore(val viewer: UUID) : Store() {

    val formulaState: StateFlow<FormulaState> = MutableStateFlow(withCraftingModel(viewer) {
        it.formula.toUIState()
    })

    interface FormulaState {

        data class Shapeless(
            val ingredients: List<UIIngredientPreview>,
        ) : FormulaState

        data class Shaped(
            val shape: Shape,
            val ingredients: List<UIIngredientPreview?>,
        ): FormulaState {

            data class Shape(
                val symmetry: CraftingFormula.Shaped.ShapeSymmetry,
                val trim: Boolean
            )

        }

    }

    @Deprecated("Temporary! updating should be moved to the yet to be implemented domain repository")
    fun updateFormulaState() {
        storeCoroutineScope.launch {
            (formulaState as MutableStateFlow).update {
                withCraftingModel(viewer) { it.formula.toUIState() }
            }
        }
    }

}

/**
 * An advanced view to create the formula of crafting recipes.
 *
 * It provides a grid of selection buttons (instead of slot inputs).
 * These allow to pick ingredients added in the previous [CraftingPath.Advanced.AddIngredients] tab.
 */
@Composable
fun FormulaPageAdvanced() {
    // TODO:
    // - Set Formula Type (shapeless, shaped)
    // - Shaped:
    //   - Select Ingredients for each slot
    //   - Set shape (symmetry, trim)
    // - Shapeless:
    //   - add/remove ingredients

    val store = store<FormulaStore>(Key.customCrafting("crafting/formula_advanced")) { FormulaStore(it) }

    val formulaState by store.formulaState.collectAsState()

    Row(Modifier.fillMaxWidth().height(4.slots), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {

        // 3x3 Grid of selector buttons (not slot inputs!)
        Column(Modifier.height(3.slots)) {
            repeat(3) { row ->
                Row(Modifier.width(3.slots)) {
                    repeat(3) { column ->
                        Button(onClick = {
                            val index = row * 3 + column
                            // TODO: Select ingredient from the collection
                        }) {
                            Icon(stack = ItemStack(Items.BARRIER).snapshot())
                        }
                    }
                }
            }
        }

        Row(Modifier.width(3.slots)) {
            // Formula type selection & type specific settings
            Button(onClick = {
                // TODO: Toggle formula type
            }) {
                Icon(stack = ItemStack(Items.CRAFTER).snapshot())
            }

            when (val state = formulaState) {
                is FormulaStore.FormulaState.Shapeless -> {
                    // No options, so far
                }

                is FormulaStore.FormulaState.Shaped -> {
                    Button(onClick = {  }) {
                        if (state.shape.trim) {
                            Icon(stack = ItemStack(Items.SHEARS).snapshot())
                        } else {
                            Icon(stack = ItemStack(Items.PAPER).snapshot())
                        }
                    }
                }
            }
        }
    }
}

/**
 * A simpler version of crafting editor view that is similar to the v4 legacy UI.
 *
 * It provides an empty grid of slot input slots, that accept any item, detecting third-party ones.
 *
 * (The shift-click interaction on ingredients from v4 that allowed for more advanced features is not available)
 */
@Composable
fun FormulaPageSimple() {
    Row(Modifier.fillMaxWidth().height(4.slots)) {

        // 3x3 Grid of slot inputs -> creating simple basic ingredients
        repeat(3) { row ->
            repeat(3) { column ->

            }
        }

        // Formula type selection & type specific settings

    }
}