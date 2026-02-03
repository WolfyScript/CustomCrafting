package com.wolfyscript.customcrafting.ui.editor.recipe_editor.crafting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.*
import com.wolfyscript.viewportl.gui.compose.viewProperties
import com.wolfyscript.viewportl.gui.elements.*
import com.wolfyscript.viewportl.gui.model.Store
import com.wolfyscript.viewportl.gui.model.store
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import kotlin.text.set

class CraftingRecipeStore : Store() {


}


@Composable
fun RecipeCraftingEditor(existingRecipeKey: Key? = null, topBackStack: SnapshotStateList<NavKey>) {
    viewProperties(Key.Companion.customCrafting("recipe_editor_crafting")) {
        size(9.slots, 6.slots)
        title("<b>Crafting</b>: <gold>${existingRecipeKey ?: "unnamed"}")
    }

    val craftingRecipeStore = store(key = Key.Companion.customCrafting("recipe_crafting")) {
        CraftingRecipeStore()
    }

    val craftingBackStack = remember { mutableStateListOf<NavKey>(CraftingPath.Advanced.AddIngredients) }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.height(5.slots).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            // Main Content
            NavigationRoot(craftingBackStack) {
                composable<CraftingPath.Advanced.AddIngredients> { AddIngredientsPage() }
                composable<CraftingPath.Advanced.Formula> { FormulaPageAdvanced() }
                composable<CraftingPath.Advanced.Result> { ResultPage() }
                composable<CraftingPath.Advanced.Conditions> { ConditionsPage() }
                composable<CraftingPath.Advanced.ExtraProperties> { ExtraPropertiesPage() }
                composable<CraftingPath.Advanced.Saving> { SavingPage() }
            }
        }
        Row(Modifier.requireHeight(1.slots).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                topBackStack.removeLast()
            }) {
                Icon(stack = ItemStack(Items.BARRIER).apply {
                    set(
                        DataComponents.ITEM_NAME,
                        "<red><b>Back".deser().vanilla()
                    )
                }.snapshot())
            }
            Row {
                BottomNavButton(
                    Defaults.IngredientsCollectionTab,
                    CraftingPath.Advanced.AddIngredients,
                    craftingBackStack
                )
                BottomNavButton(
                    Defaults.FormulaTab,
                    CraftingPath.Advanced.Formula,
                    craftingBackStack
                )
                BottomNavButton(
                    Defaults.ResultTab,
                    CraftingPath.Advanced.Result,
                    craftingBackStack
                )
                BottomNavButton(
                    Defaults.ConditionsTab,
                    CraftingPath.Advanced.Conditions,
                    craftingBackStack
                )
                BottomNavButton(
                    Defaults.CommonSettingsTab,
                    CraftingPath.Advanced.ExtraProperties,
                    craftingBackStack
                )
                BottomNavButton(
                    Defaults.SaveTab,
                    CraftingPath.Advanced.Saving,
                    craftingBackStack
                )
            }
        }
    }
}

@Composable
private fun BottomNavButton(stack: ItemStackSnapshot, path: NavKey, backStack: SnapshotStateList<NavKey>) {
    Button(onClick = {
        backStack[0] = path
    }) {
        Icon(stack = stack)
    }
}

private object Defaults {

    val IngredientsCollectionTab = ItemStack(Items.BOOKSHELF).apply {
        set(DataComponents.ITEM_NAME, "<light_purple><b>Ingredients".deser().vanilla())
        set(DataComponents.LORE, ItemLore(listOf(
            "Collection of Ingredients to be".deser().vanilla(),
            "used in the Recipe Formula.".deser().vanilla()
        )))
    }.snapshot()

    val FormulaTab = ItemStack(Items.BREWING_STAND).apply {
        set(
            DataComponents.ITEM_NAME,
            "<light_purple><b>Formula".deser().vanilla()
        )
        set(DataComponents.LORE, ItemLore(listOf(
            "Associate Recipe slots with".deser().vanilla(),
            "ingredients from previous step.".deser().vanilla(),
        )))
    }.snapshot()

    val ResultTab = ItemStack(Items.ITEM_FRAME).apply {
        set(
            DataComponents.ITEM_NAME,
            "<light_purple><b>Result".deser().vanilla()
        )
        set(DataComponents.LORE, ItemLore(listOf(
            "Configure what the recipe produces.".deser().vanilla(),
            "e.g. Choices, Modifiers, Actions".deser().vanilla(),
        )))
    }.snapshot()

    val ConditionsTab = ItemStack(Items.COMMAND_BLOCK).apply {
        set(
            DataComponents.ITEM_NAME,
            "<light_purple><b>Conditions".deser().vanilla()
        )
        set(DataComponents.LORE, ItemLore(listOf(
            "Configure requirements for".deser().vanilla(),
            "the recipe to work.".deser().vanilla(),
        )))
    }.snapshot()

    val CommonSettingsTab = ItemStack(Items.PAPER).apply {
        set(
            DataComponents.ITEM_NAME,
            "<light_purple><b>Common Settings".deser().vanilla()
        )
        set(DataComponents.LORE, ItemLore(listOf(
            "Configure common settings".deser().vanilla(),
            "such as group, etc.".deser().vanilla(),
        )))
    }.snapshot()

    val SaveTab = ItemStack(Items.WRITABLE_BOOK).apply {
        set(
            DataComponents.ITEM_NAME,
            "<light_purple><b>Save".deser().vanilla()
        )
        set(DataComponents.LORE, ItemLore(listOf(
            "Save and Export the recipe".deser().vanilla(),
        )))
    }.snapshot()

}