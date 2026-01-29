package com.wolfyscript.customcrafting.ui.editor.recipe_editor.crafting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.RecipeCraftingModel
import com.wolfyscript.customcrafting.editor.domain.usecase.RecipeCraftingUseCases
import com.wolfyscript.customcrafting.editor.recipeEditor
import com.wolfyscript.customcrafting.recipes.CraftingFormula
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.state.UIIngredientPreview
import com.wolfyscript.customcrafting.ui.editor.recipe_editor.state.toPreview
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
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.BundleContents
import net.minecraft.world.item.component.ItemLore
import java.util.*
import kotlin.text.set

private fun RecipeCraftingModel.CraftingFormulaModel<*>.toUIState(collection: RecipeCraftingModel.IngredientCollectionModel): FormulaStore.FormulaState {
    return when (this) {
        is RecipeCraftingModel.CraftingFormulaModel.Shaped -> {
            FormulaStore.FormulaState.Shaped(
                FormulaStore.FormulaState.Shaped.Shape(shape.symmetry, shape.trim),
                ingredientRefs.map { it?.resolveFor(collection)?.toPreview() },
            )
        }

        is RecipeCraftingModel.CraftingFormulaModel.Shapeless -> {
            FormulaStore.FormulaState.Shapeless(
                ingredientRefs.mapNotNull { it.resolveFor(collection)?.toPreview() },
            )
        }

        else -> error("Unknown formula model: $this")
    }
}

private class FormulaStore(
    val viewer: UUID,
    private val getFormula: RecipeCraftingUseCases.Formula.Get,
    private val setFormulaType: RecipeCraftingUseCases.Formula.SetType,
    private val assignIngredient: RecipeCraftingUseCases.Formula.AssignIngredient,
    private val unassignIngredient: RecipeCraftingUseCases.Formula.UnassignIngredient,
    private val toggleTrimShape: RecipeCraftingUseCases.Formula.ToggleTrimShape,
    private val toggleShapeSymmetry: RecipeCraftingUseCases.Formula.ToggleShapeSymmetry,
    private val getIngredientCollection: RecipeCraftingUseCases.IngredientCollection.Get,
) : Store() {

    val formulaState: StateFlow<FormulaState>
        field = MutableStateFlow(getFormula.get().toUIState(getIngredientCollection.getCollection()))

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

    fun toggleTrimShape() {
        toggleTrimShape.toggle()
        updateFormulaState()
    }

    fun setIngredientForSlot(index: Int, ingredientIndex: Int) {
        assignIngredient.assign(index, ingredientIndex)
        updateFormulaState()
    }

    fun resetIngredientForSlot(index: Int) {
        unassignIngredient.unassign(index)
        updateFormulaState()
    }

    fun toggleShapeSymmetry(horizontal: Boolean = false, vertical: Boolean = false, rotate: Boolean = false) {
        toggleShapeSymmetry.toggle(horizontal, vertical, rotate)
        updateFormulaState()
    }

    fun getIngredientCollectionIcons(): List<ItemStack> {
        val stacks = mutableListOf<ItemStack>()
        stacks.add(FormulaPageDefaults.IngredientScrollSelectReset)
        getIngredientCollection.getCollection().ingredients.mapNotNullTo(stacks) {
            if (it is IngredientModel.CustomIngredientModel) {
                return@mapNotNullTo it.choices.stacks.firstOrNull()?.create()?.unwrap()
            }
            null
        }
        return stacks
    }

    fun updateFormulaState() {
        storeCoroutineScope.launch {
            formulaState.update {
                getFormula.get().toUIState(getIngredientCollection.getCollection())
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
    // - Shaped:
    //   - Set shape (symmetry, trim)

    val store = store<FormulaStore>(Key.Companion.customCrafting("crafting/formula_advanced")) {
        val session = CustomCraftingProvider.get().server!!.recipeEditor.getOrCreateSession(it).getOrThrow()
        FormulaStore(
            it,
            RecipeCraftingUseCases.Formula.Get(session),
            RecipeCraftingUseCases.Formula.SetType(session),
            RecipeCraftingUseCases.Formula.AssignIngredient(session),
            RecipeCraftingUseCases.Formula.UnassignIngredient(session),
            RecipeCraftingUseCases.Formula.ToggleTrimShape(session),
            RecipeCraftingUseCases.Formula.ToggleShapeSymmetry(session),
            RecipeCraftingUseCases.IngredientCollection.Get(session),
        )
    }

    val formulaState by store.formulaState.collectAsState()
    val isShapeless = formulaState is FormulaStore.FormulaState.Shapeless

    Column(Modifier.height(4.slots)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            // Formula type selection & type specific settings
            Button(onClick = {
                store.toggleFormulaType()
            }) {
                if (isShapeless) {
                    Icon(stack = FormulaPageDefaults.ShapelessIcon)
                } else {
                    Icon(stack = FormulaPageDefaults.ShapedIcon)
                }
            }
        }

        Row(
            Modifier.fillMaxWidth().height(3.slots),
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
                                    store.setIngredientForSlot(index, it - 1)
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
                                        // We copy all the components of the original stack and then override some
                                        applyComponents(stack.components)
                                    } else {
                                        set(DataComponents.ITEM_MODEL, BuiltInRegistries.ITEM.getKey(Items.AIR))
                                        set(DataComponents.ITEM_NAME, "Slot $index".deser().vanilla())
                                    }
                                    update(DataComponents.LORE, ItemLore.EMPTY) {
                                        ItemLore(it.lines.toMutableList().apply {
                                            addAll(FormulaPageDefaults.IngredientScrollSelectInteractionLore)
                                        })
                                    }
                                    set(
                                        DataComponents.BUNDLE_CONTENTS,
                                        BundleContents(store.getIngredientCollectionIcons())
                                    )
                                }.snapshot()
                            })
                        }
                    }
                }
            }

            Column(
                Modifier.width(3.slots).height(3.slots),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (val state = formulaState) {
                    is FormulaStore.FormulaState.Shapeless -> {
                        // No options, so far
                    }

                    is FormulaStore.FormulaState.Shaped -> {
                        Button(onClick = { store.toggleTrimShape() }) {
                            if (state.shape.trim) {
                                Icon(stack = FormulaPageDefaults.TrimShapeIcon)
                            } else {
                                Icon(stack = FormulaPageDefaults.KeepShapeIcon)
                            }
                        }

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Button(onClick = {
                                store.toggleShapeSymmetry(horizontal = true)
                            }) {
                                Icon(stack = if (state.shape.symmetry.horizontal) FormulaPageDefaults.SymmetryHorizontalEnabled else FormulaPageDefaults.SymmetryHorizontalDisabled)
                            }
                            Button(onClick = {
                                store.toggleShapeSymmetry(vertical = true)
                            }) {
                                Icon(stack = if(state.shape.symmetry.vertical) FormulaPageDefaults.SymmetryVerticalEnabled else FormulaPageDefaults.SymmetryVerticalDisabled)
                            }
                            if (state.shape.symmetry.vertical && state.shape.symmetry.horizontal) {
                                Button(onClick = {
                                    store.toggleShapeSymmetry(rotate = true)
                                }) {
                                    Icon(stack = if (state.shape.symmetry.rotate) FormulaPageDefaults.SymmetryRotateEnabled else FormulaPageDefaults.SymmetryRotateDisabled)
                                }
                            }
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

private object FormulaPageDefaults {

    val IngredientScrollSelectInteractionLore = listOf(
        Component.empty(),
        "<!i><yellow>Scroll <white>Select ingredient".deser().vanilla(),
        "<!i><yellow><key:key.use> <white>Submit selection".deser().vanilla()
    )

    val IngredientScrollSelectReset = ItemStack(Items.BARRIER).apply {
        set(DataComponents.ITEM_NAME, "<red><b>Reset (Empty)".deser().vanilla())
        set(DataComponents.MAX_STACK_SIZE, 1)
    }

    val ShapelessIcon = ItemStack(Items.CRAFTER).apply {
        set(DataComponents.ITEM_NAME, "Shapeless".deser().vanilla())
        set(
            DataComponents.LORE, ItemLore(
                listOf(
                    "<white>Ingredients can be placed anywhere".deser().vanilla(),
                    "<white>in no specific order or shape.".deser().vanilla(),
                )
            )
        )
    }.snapshot()

    val ShapedIcon = ItemStack(Items.CRAFTER).apply {
        set(DataComponents.ITEM_NAME, "Shaped".deser().vanilla())
        set(
            DataComponents.LORE, ItemLore(
                listOf(
                    "<white>Ingredients must be placed".deser().vanilla(),
                    "<white>in the shape of the recipe.".deser().vanilla(),
                )
            )
        )
    }.snapshot()

    val KeepShapeIcon = ItemStack(Items.PAPER).apply {
        set(DataComponents.ITEM_NAME, "Keep Shape Size".deser().vanilla())
        set(
            DataComponents.LORE, ItemLore(
                listOf(
                    "<white>Keeps shape size at max width & height.".deser().vanilla(),
                    "<white>Items must be placed <b>exactly</b>".deser().vanilla(),
                    "<white>where they are in the recipe.".deser().vanilla()
                )
            )
        )
    }.snapshot()

    val TrimShapeIcon = ItemStack(Items.SHEARS).apply {
        set(DataComponents.ITEM_NAME, "Trim Shape Size".deser().vanilla())
        set(
            DataComponents.LORE, ItemLore(
                listOf(
                    "<white>Trims shape size to fit ingredients.".deser().vanilla(),
                    "<white>Allows placing smaller shapes".deser().vanilla(),
                    "<white>anywhere into the grid.".deser().vanilla()
                )
            )
        )
    }.snapshot()

    val SymmetryHorizontalEnabled = ItemStack(Items.ITEM_FRAME).apply {
        set(DataComponents.ITEM_NAME, "Mirror Horizontally".deser().vanilla())
    }.snapshot()

    val SymmetryHorizontalDisabled = ItemStack(Items.ITEM_FRAME).apply {
        set(DataComponents.ITEM_NAME, "Fixed Horizontally".deser().vanilla())
    }.snapshot()

    val SymmetryVerticalEnabled = ItemStack(Items.ITEM_FRAME).apply {
        set(DataComponents.ITEM_NAME, "Mirror Vertically".deser().vanilla())
    }.snapshot()

    val SymmetryVerticalDisabled = ItemStack(Items.ITEM_FRAME).apply {
        set(DataComponents.ITEM_NAME, "Fixed Vertically".deser().vanilla())
    }.snapshot()

    val SymmetryRotateEnabled = ItemStack(Items.ITEM_FRAME).apply {
        set(DataComponents.ITEM_NAME, "Mirror Both Axis (Rotate)".deser().vanilla())
        set(DataComponents.LORE, ItemLore(listOf(
            "Allows crafting the recipe".deser().vanilla(),
            "with the shape being rotated.".deser().vanilla(),
            "e.g. flipped Horizontally + Vertically".deser().vanilla()
        )))
    }.snapshot()

    val SymmetryRotateDisabled = ItemStack(Items.ITEM_FRAME).apply {
        set(DataComponents.ITEM_NAME, "Mirror Single Axis".deser().vanilla())
        set(DataComponents.LORE, ItemLore(listOf(
            "Shape may be mirrored horizontally".deser().vanilla(),
            "or vertically, but not both.".deser().vanilla(),
        )))
    }.snapshot()
}