package com.wolfyscript.customcrafting.editor.ui.recipe_editor.crafting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.util.customCrafting
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

class CraftingRecipeStore : Store {



}


@Composable
fun RecipeCraftingEditor(existingRecipeKey: Key? = null, topBackStack: SnapshotStateList<NavKey>) {
    viewProperties(Key.customCrafting("recipe_editor_crafting")) {
        size(9.slots, 6.slots)
        title("<b>Crafting</b>: <gold>${existingRecipeKey ?: "unnamed"}")
    }

    val craftingRecipeStore = store(key = Key.customCrafting("recipe_crafting")) {
        CraftingRecipeStore()
    }

    val craftingBackStack = remember { mutableStateListOf<NavKey>(CraftingPaths.Advanced.AddIngredients) }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.height(5.slots).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            // Main Content
            NavHost(craftingBackStack, onBack = {}) {
                composable<CraftingPaths.Advanced.AddIngredients> { AddIngredientsPage() }
                composable<CraftingPaths.Advanced.Formula> { FormulaPageAdvanced() }
                composable<CraftingPaths.Advanced.Result> { ResultPage() }
                composable<CraftingPaths.Advanced.Conditions> { ConditionsPage() }
                composable<CraftingPaths.Advanced.ExtraProperties> { ExtraPropertiesPage() }
                composable<CraftingPaths.Advanced.Saving> { SavingPage() }
            }
        }
        Row(Modifier.requireHeight(1.slots).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                topBackStack.removeLast()
            }) {
                Icon(stack = ItemStack(Items.BARRIER).apply { set(DataComponents.ITEM_NAME, "<red><b>Back".deser().vanilla()) }.snapshot())
            }
            Row {
                // Bottom Nav
                BottomNavButton(
                    ItemStack(Items.BOOKSHELF).snapshot(),
                    CraftingPaths.Advanced.AddIngredients,
                    craftingBackStack
                )
                BottomNavButton(
                    ItemStack(Items.BREWING_STAND).snapshot(),
                    CraftingPaths.Advanced.Formula,
                    craftingBackStack
                )
                BottomNavButton(
                    ItemStack(Items.ITEM_FRAME).snapshot(),
                    CraftingPaths.Advanced.Result,
                    craftingBackStack
                )
                BottomNavButton(
                    ItemStack(Items.COMMAND_BLOCK).snapshot(),
                    CraftingPaths.Advanced.Conditions,
                    craftingBackStack
                )
                BottomNavButton(
                    ItemStack(Items.PAPER).snapshot(),
                    CraftingPaths.Advanced.ExtraProperties,
                    craftingBackStack
                )
                BottomNavButton(
                    ItemStack(Items.WRITABLE_BOOK).snapshot(),
                    CraftingPaths.Advanced.Saving,
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