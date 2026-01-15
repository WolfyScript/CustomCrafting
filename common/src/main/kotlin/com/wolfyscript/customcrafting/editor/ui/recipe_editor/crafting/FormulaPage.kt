package com.wolfyscript.customcrafting.editor.ui.recipe_editor.crafting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.domain.recipes.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeCraftingModel
import com.wolfyscript.customcrafting.editor.domain.usecase.RecipeCraftingUseCases
import com.wolfyscript.customcrafting.editor.recipeEditor
import com.wolfyscript.customcrafting.editor.ui.recipe_editor.state.UIIngredientPreview
import com.wolfyscript.customcrafting.editor.ui.recipe_editor.state.toPreview
import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.util.customCrafting
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.scafall.wrappers.unwrap
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxWidth
import com.wolfyscript.viewportl.gui.compose.modifier.height
import com.wolfyscript.viewportl.gui.compose.modifier.width
import com.wolfyscript.viewportl.gui.elements.*
import com.wolfyscript.viewportl.gui.model.Store
import com.wolfyscript.viewportl.gui.model.store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.BundleContents
import net.minecraft.world.item.component.ItemLore
import java.util.*

private fun RecipeCraftingModel.CraftingFormulaModel<*>.toUIState(): FormulaStore.FormulaState {
    return when (this) {
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

private class FormulaStore(
    val viewer: UUID,
    val getFormula: RecipeCraftingUseCases.Formula.Get,
    val setFormulaType: RecipeCraftingUseCases.Formula.SetType,
    val assignIngredient: RecipeCraftingUseCases.Formula.AssignIngredient,
    val unassignIngredient: RecipeCraftingUseCases.Formula.UnassignIngredient,
    val getIngredientCollection: RecipeCraftingUseCases.IngredientCollection.GetUseCase,
) : Store() {

    val formulaState: StateFlow<FormulaState> = MutableStateFlow(getFormula.get().toUIState())

    interface FormulaState {

        fun getIngredient(index: Int): UIIngredientPreview?

        data class Shapeless(
            val ingredients: List<UIIngredientPreview>,
        ) : FormulaState {

            override fun getIngredient(index: Int): UIIngredientPreview? {
                if (ingredients.size > index) {
                    return ingredients[index]
                }
                return null
            }
        }

        data class Shaped(
            val shape: Shape,
            val ingredients: List<UIIngredientPreview?>,
        ) : FormulaState {

            override fun getIngredient(index: Int): UIIngredientPreview? {
                if (ingredients.size > index) {
                    return ingredients[index]
                }
                return null
            }

            data class Shape(
                val symmetry: CraftingFormula.Shaped.ShapeSymmetry,
                val trim: Boolean,
            )

        }

    }

    fun toggleFormulaType() {
        if (formulaState.value is FormulaState.Shapeless) {
            setFormulaType.set(CraftingFormula.Shaped::class.java)
        } else {
            setFormulaType.set(CraftingFormula.Shapeless::class.java)
        }
        updateFormulaState()
    }

    fun setIngredientForSlot(index: Int, ingredientIndex: Int) {
        assignIngredient.assign(index, getIngredientCollection.get()[ingredientIndex])
        updateFormulaState()
    }

    fun resetIngredientForSlot(index: Int) {
        unassignIngredient.unassign(index)
        updateFormulaState()
    }

    fun getIngredientCollectionIcons(): List<ItemStack> {
        val stacks = mutableListOf<ItemStack>()
        stacks.add(ItemStack(Items.BARRIER).apply {
            set(DataComponents.ITEM_NAME, "<red><b>Reset (Empty)".deser().vanilla())
            set(DataComponents.MAX_STACK_SIZE, 1)
            set(
                DataComponents.LORE, ItemLore(
                    listOf("<!i><white>Resets slot item to air".deser().vanilla())
                )
            )
        })
        getIngredientCollection.getCollection().ingredients.mapNotNullTo(stacks) {
            if (it is IngredientModel.CustomIngredientModel) {
                return@mapNotNullTo it.stacks.firstOrNull()?.create()?.unwrap()
            }
            null
        }
        return stacks
    }

    @Deprecated("Temporary! updating should be moved to the yet to be implemented domain repository")
    fun updateFormulaState() {
        storeCoroutineScope.launch {
            (formulaState as MutableStateFlow).update {
                getFormula.get().toUIState()
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

    val store = store<FormulaStore>(Key.customCrafting("crafting/formula_advanced")) {
        val session = CustomCraftingProvider.get().server!!.recipeEditor.getOrCreateSession(it).getOrThrow()
        FormulaStore(
            it,
            RecipeCraftingUseCases.Formula.Get(session),
            RecipeCraftingUseCases.Formula.SetType(session),
            RecipeCraftingUseCases.Formula.AssignIngredient(session),
            RecipeCraftingUseCases.Formula.UnassignIngredient(session),
            RecipeCraftingUseCases.IngredientCollection.GetUseCase(session),
        )
    }

    val formulaState by store.formulaState.collectAsState()
    val isShapeless = formulaState is FormulaStore.FormulaState.Shapeless

    Row(
        Modifier.fillMaxWidth().height(4.slots),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 3x3 Grid of selector buttons (not slot inputs!)
        Column(Modifier.height(3.slots)) {
            repeat(3) { row ->
                Row(Modifier.width(3.slots)) {
                    repeat(3) { column ->
                        val index = row * 3 + column
                        ScrollSelect(onSubmit = {
                            if (it >= 1) {
                                store.setIngredientForSlot(index, it-1)
                            } else {
                                store.resetIngredientForSlot(index)
                            }
                        }, icon = formulaState.getIngredient(index).let { ingredientPreview ->
                            val stack = ingredientPreview?.icon?.unwrap()
                            ItemStack(
                                Items.RED_BUNDLE,
                                stack?.count ?: 1
                            ).apply {
                                if (stack != null) {
                                    applyComponents(stack.components)
                                } else {
                                    set(DataComponents.ITEM_MODEL, BuiltInRegistries.ITEM.getKey(Items.AIR))
                                }
                                set(DataComponents.ITEM_NAME, "Slot $index".deser().vanilla())
                                update(DataComponents.LORE, ItemLore(emptyList())) {
                                    it.withLineAdded(
                                        "<!i><yellow>Scroll <white>Select ingredient".deser().vanilla()
                                    ).withLineAdded(
                                        "<!i><yellow><key:key.use> <white>Submit selection".deser().vanilla()
                                    )
                                }
                                set(DataComponents.BUNDLE_CONTENTS, BundleContents(store.getIngredientCollectionIcons()))
                            }.snapshot()
                        })
                    }
                }
            }
        }

        Row(Modifier.width(3.slots)) {
            // Formula type selection & type specific settings
            Button(onClick = {
                store.toggleFormulaType()
            }) {
                Icon(stack = ItemStack(Items.CRAFTER).apply {
                    set(DataComponents.ITEM_NAME, (if (isShapeless) "Shapeless" else "Shaped").deser().vanilla())
                }.snapshot())
            }

            when (val state = formulaState) {
                is FormulaStore.FormulaState.Shapeless -> {
                    // No options, so far
                }

                is FormulaStore.FormulaState.Shaped -> {
                    Button(onClick = { }) {
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