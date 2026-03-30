package com.wolfyscript.customcrafting.ui.editor.recipe_editor.recipeitem

import androidx.compose.runtime.*
import com.wolfyscript.customcrafting.core.util.customCrafting
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.IngredientMatcherExactModel
import com.wolfyscript.customcrafting.editor.domain.model.recipe.item.ItemIngredientMatcherModel
import com.wolfyscript.customcrafting.ui.editor.IngredientMatcherContext
import com.wolfyscript.customcrafting.ui.editor.IngredientMatcherCustomUIProvider
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.toKey
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.viewportl.gui.compose.layout.Alignment
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxSize
import com.wolfyscript.viewportl.gui.compose.modifier.fillMaxWidth
import com.wolfyscript.viewportl.gui.compose.modifier.height
import com.wolfyscript.viewportl.gui.elements.*
import com.wolfyscript.viewportl.gui.model.Store
import com.wolfyscript.viewportl.gui.model.store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.math.ceil

class ExactIngredientMatcherUIProvider : IngredientMatcherCustomUIProvider<IngredientMatcherExactModel> {

    override val modelType: Class<IngredientMatcherExactModel> = IngredientMatcherExactModel::class.java

    @Composable
    override fun IngredientMatcherContext<IngredientMatcherExactModel>.render(model: IngredientMatcherExactModel) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Icon(stack = ItemStack(Items.CYAN_CONCRETE).apply {
                set(DataComponents.ITEM_NAME, "This matcher requires no configuration!".deser().vanilla())
            }.snapshot())
        }
    }

}

class ItemIngredientMatcherUIProvider : IngredientMatcherCustomUIProvider<ItemIngredientMatcherModel> {

    override val modelType: Class<ItemIngredientMatcherModel> = ItemIngredientMatcherModel::class.java

    @Composable
    override fun IngredientMatcherContext<ItemIngredientMatcherModel>.render(model: ItemIngredientMatcherModel) {
        ItemIngredientMatcherMenu(
            model,
            updateMatcher = { updateMatcher(it) },
        )
    }

}

private const val ITEMS_PER_PAGE = 18

private data class AvailableDataComponentsState(
    val loading: Boolean,
    val totalPages: Int,
    val components: List<DataComponentDisplay>,
)

private data class DataComponentDisplay(
    val key: Key,
    val icon: Item,
)

private class ItemIngredientMatcherExactStore : Store() {

    val availableDataComponents: StateFlow<AvailableDataComponentsState>
        field = MutableStateFlow(AvailableDataComponentsState(loading = false, 0, components = emptyList()))

    fun setSelectionPage(page: Int) {
        getAvailableComponentsList(page * ITEMS_PER_PAGE, ITEMS_PER_PAGE)
    }

    private fun getAvailableComponentsList(fromIndex: Int, count: Int) {
        storeCoroutineScope.launch {
            availableDataComponents.update {
                AvailableDataComponentsState(loading = true, 0, emptyList())
            }
            availableDataComponents.update {
                try {
                    val components = BuiltInRegistries.DATA_COMPONENT_TYPE.entrySet().stream()
                        .sorted { entry, other -> entry.key.toString().compareTo(other.key.toString()) }
                        .skip(fromIndex.toLong())
                        .limit(count.toLong())
                        .map {
                            DataComponentDisplay(
                                it.key.identifier().toKey(),
                                DataComponentIcons.getIconFor(it.value)
                            )
                        }
                        .toList()
                    val pages = ceil(BuiltInRegistries.DATA_COMPONENT_TYPE.size() / ITEMS_PER_PAGE.toFloat()).toInt()
                    AvailableDataComponentsState(loading = false, pages, components)
                } catch (e: Exception) {
                    ScafallProvider.get().logger.error("Error while fetching data component types", e)
                    AvailableDataComponentsState(loading = false, 0, emptyList())
                }
            }
        }
    }
}

private enum class SelectedSubMenu {
    NONE,
    MUST_CONTAIN,
    MUST_NOT_CONTAIN,
}

@Composable
fun ItemIngredientMatcherMenu(
    matcher: ItemIngredientMatcherModel,
    updateMatcher: (newMatcher: ItemIngredientMatcherModel) -> Unit,
) {
    var selectedSubMenu by remember { mutableStateOf(SelectedSubMenu.NONE) }

    when (selectedSubMenu) {
        SelectedSubMenu.MUST_CONTAIN -> {
            DataComponentsList(
                components = { matcher.mustContain.toList() },
                onAdd = {
                    matcher.mustContain.add(it)
                    updateMatcher(matcher)
                },
                onRemove = {
                    matcher.mustContain.remove(it)
                    updateMatcher(matcher)
                },
                onComplete = { selectedSubMenu = SelectedSubMenu.NONE }
            )
        }

        SelectedSubMenu.MUST_NOT_CONTAIN -> {
            DataComponentsList(
                components = { matcher.mustNotContain.toList() },
                onAdd = {
                    matcher.mustNotContain.add(it)
                    updateMatcher(matcher)
                },
                onRemove = {
                    matcher.mustNotContain.remove(it)
                    updateMatcher(matcher)
                },
                onComplete = { selectedSubMenu = SelectedSubMenu.NONE }
            )
        }

        else -> {
            Column(Modifier.fillMaxWidth()) {
                Row {
                    Button(onClick = {
                        selectedSubMenu = SelectedSubMenu.MUST_CONTAIN
                    }) {
                        Icon(stack = Defaults.MustContainIcon)
                    }

                    Button(onClick = {
                        selectedSubMenu = SelectedSubMenu.MUST_NOT_CONTAIN
                    }) {
                        Icon(stack = Defaults.MustNotContainIcon)
                    }
                }
            }
        }
    }

}

@Composable
private fun DataComponentsList(
    components: () -> List<Key>,
    onAdd: (Key) -> Unit,
    onRemove: (Key) -> Unit,
    onComplete: () -> Unit,
) {
    val store = store(Key.customCrafting("ingredient_matcher/exact")) {
        ItemIngredientMatcherExactStore()
    }
    var selecting: Boolean by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth().height(4.slots)) {
        if (selecting) {
            val components by store.availableDataComponents.collectAsState()
            DataComponentSelection(
                components,
                onAdd = {
                    onAdd(it)
                    selecting = false
                },
                onPageChange = { store.setSelectionPage(it) }
            )
        } else {
            Paged(
                Modifier,
                ceil(components().size / ITEMS_PER_PAGE.toFloat()).toInt(),
                onPageChange = {},
                controlContent = {
                    Button(onClick = { onComplete() }) {
                        Icon(stack = Defaults.Done)
                    }
                    Button(onClick = {
                        selecting = true
                        store.setSelectionPage(0)
                    }) {
                        Icon(stack = Defaults.AddNew)
                    }
                }
            ) {
                // TODO: Create a "Flow Row (Grid)" Component that automatically arranges items in 2d
                Column(Modifier.fillMaxWidth().height(3.slots)) {
                    repeat(3) { row ->
                        Row(Modifier.fillMaxWidth()) {
                            repeat(6) { col ->
                                val index = row * 6 + col
                                components().getOrNull(index)?.let { key ->
                                    Button(onClick = { onRemove(key) }) {
                                        Icon(stack = ItemStack(Items.NAME_TAG).apply {
                                            set(DataComponents.ITEM_NAME, key.toString().deser().vanilla())
                                        }.snapshot())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DataComponentSelection(
    componentsState: AvailableDataComponentsState,
    onAdd: (Key) -> Unit,
    onPageChange: (Int) -> Unit,
) {
    Paged(Modifier, componentsState.totalPages, onPageChange = { onPageChange(it) }) {
        DataComponentsSelectionPage(
            componentsState,
            onSelect = {
                onAdd(it)
            }
        )
    }
}

@Composable
private fun DataComponentsSelectionPage(
    componentsState: AvailableDataComponentsState,
    onSelect: (Key) -> Unit,
) {
    if (componentsState.loading) {
        Column(
            Modifier.fillMaxWidth().height(3.slots),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                Modifier.fillMaxWidth().height(3.slots),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(stack = Defaults.LoadingIcon)
            }
        }
    } else {
        // TODO: Create a "Flow Row (Grid)" Component that automatically arranges items in 2d
        Column(Modifier.fillMaxWidth().height(3.slots)) {
            repeat(3) { row ->
                Row(Modifier.fillMaxWidth()) {
                    repeat(6) { col ->
                        val index = row * 6 + col
                        componentsState.components.getOrNull(index)?.let { component ->
                            Button(onClick = {
                                onSelect(component.key)
                            }) {
                                Icon(stack = ItemStack(component.icon).apply {
                                    set(DataComponents.ITEM_NAME, component.key.toString().deser().vanilla())
                                }.snapshot())
                            }
                        }
                    }
                }
            }
        }
    }
}

private object Defaults {

    val Done = ItemStack(Items.BARRIER).apply {
        set(DataComponents.ITEM_NAME, "<red>Done".deser().vanilla())
    }.snapshot()

    val MustContainIcon = ItemStack(Items.GREEN_BUNDLE).apply {
        set(DataComponents.ITEM_NAME, "<green>Must Contain".deser().vanilla())
        remove(DataComponents.BUNDLE_CONTENTS)
    }.snapshot()

    val MustNotContainIcon = ItemStack(Items.RED_BUNDLE).apply {
        set(DataComponents.ITEM_NAME, "<red>Must Not Contain".deser().vanilla())
        remove(DataComponents.BUNDLE_CONTENTS)
    }.snapshot()

    val LoadingIcon = ItemStack(Items.BLUE_CONCRETE).apply {
        set(DataComponents.ITEM_NAME, "<light_blue>Loading Components...".deser().vanilla())
    }.snapshot()

    val AddNew = ItemStack(Items.BOOKSHELF).apply {
        set(DataComponents.ITEM_NAME, "Add Data Component".deser().vanilla())
    }.snapshot()

}

object DataComponentIcons {

    private val iconMap: MutableMap<DataComponentType<*>, Item> = mutableMapOf()

    init {
        associate(DataComponents.ATTACK_RANGE, Items.COPPER_SPEAR)
        associate(DataComponents.ATTRIBUTE_MODIFIERS, Items.CHAIN_COMMAND_BLOCK)
        associate(DataComponents.BANNER_PATTERNS, Items.WHITE_BANNER)
        associate(DataComponents.BASE_COLOR, Items.SHIELD)
        associate(DataComponents.BEES, Items.BEE_NEST)
        associate(DataComponents.BLOCK_ENTITY_DATA, Items.SPAWNER)
        associate(DataComponents.BLOCK_STATE, Items.OAK_STAIRS)
        associate(DataComponents.BLOCKS_ATTACKS, Items.SHIELD)
        associate(DataComponents.BREAK_SOUND, Items.WOODEN_SWORD)
        associate(DataComponents.BUCKET_ENTITY_DATA, Items.TROPICAL_FISH_BUCKET)
        associate(DataComponents.BUNDLE_CONTENTS, Items.BUNDLE)
        associate(DataComponents.CAN_BREAK, Items.STONE_PICKAXE)
        associate(DataComponents.CAN_PLACE_ON, Items.COBBLESTONE)
        associate(DataComponents.CHARGED_PROJECTILES, Items.CROSSBOW)
        associate(DataComponents.CONSUMABLE, Items.GOLDEN_APPLE)
        associate(DataComponents.CONTAINER, Items.SHULKER_BOX)
        associate(DataComponents.CONTAINER_LOOT, Items.CHEST)
        associate(DataComponents.CUSTOM_DATA, Items.BARRIER)
        associate(DataComponents.CUSTOM_MODEL_DATA, Items.DIAMOND)
        associate(DataComponents.CUSTOM_NAME, Items.NAME_TAG)
        associate(DataComponents.DAMAGE, Items.IRON_AXE)
        associate(DataComponents.DAMAGE_RESISTANT, Items.NETHERITE_INGOT)
        associate(DataComponents.DAMAGE_TYPE, Items.STONE_SWORD)
        associate(DataComponents.DEATH_PROTECTION, Items.TOTEM_OF_UNDYING)
        associate(DataComponents.DEBUG_STICK_STATE, Items.DEBUG_STICK)
        //associate(DataComponents.DYE, Items.AIR)
        associate(DataComponents.DYED_COLOR, Items.LEATHER_CHESTPLATE)
        associate(DataComponents.ENCHANTABLE, Items.DIAMOND_BOOTS)
        associate(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, Items.EXPERIENCE_BOTTLE)
        associate(DataComponents.ENCHANTMENTS, Items.BOOK)
        associate(DataComponents.ENTITY_DATA, Items.ARMOR_STAND)
        associate(DataComponents.EQUIPPABLE, Items.SADDLE)
        associate(DataComponents.FIREWORK_EXPLOSION, Items.FIREWORK_STAR)
        associate(DataComponents.FIREWORKS, Items.FIREWORK_ROCKET)
        associate(DataComponents.FOOD, Items.COOKED_BEEF)
        associate(DataComponents.GLIDER, Items.ELYTRA)
        associate(DataComponents.INSTRUMENT, Items.GOAT_HORN)
        associate(DataComponents.INTANGIBLE_PROJECTILE, Items.ARROW)
        associate(DataComponents.ITEM_MODEL, Items.EMERALD)
        associate(DataComponents.ITEM_NAME, Items.NAME_TAG)
        associate(DataComponents.JUKEBOX_PLAYABLE, Items.MUSIC_DISC_5)
        associate(DataComponents.KINETIC_WEAPON, Items.IRON_SPEAR)
        associate(DataComponents.LOCK, Items.TRIPWIRE_HOOK)
        associate(DataComponents.LODESTONE_TRACKER, Items.COMPASS)
        associate(DataComponents.LORE, Items.PAPER)
        associate(DataComponents.MAP_COLOR, Items.MAP)
        associate(DataComponents.MAP_DECORATIONS, Items.MAP)
        associate(DataComponents.MAP_ID, Items.MAP)
        associate(DataComponents.MAX_DAMAGE, Items.DIAMOND_AXE)
        associate(DataComponents.MAX_STACK_SIZE, Items.EGG)
        associate(DataComponents.MINIMUM_ATTACK_CHARGE, Items.STONE_SPEAR)
        associate(DataComponents.NOTE_BLOCK_SOUND, Items.NOTE_BLOCK)
        associate(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, Items.OMINOUS_BOTTLE)
        associate(DataComponents.PIERCING_WEAPON, Items.DIAMOND_SPEAR)
        associate(DataComponents.POT_DECORATIONS, Items.DANGER_POTTERY_SHERD)
        associate(DataComponents.POTION_CONTENTS, Items.POTION)
        associate(DataComponents.POTION_DURATION_SCALE, Items.LINGERING_POTION)
        associate(DataComponents.PROFILE, Items.PLAYER_HEAD)
        associate(DataComponents.PROVIDES_BANNER_PATTERNS, Items.CREEPER_BANNER_PATTERN)
        associate(DataComponents.PROVIDES_TRIM_MATERIAL, Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE)
        associate(DataComponents.RARITY, Items.NETHER_STAR)
        associate(DataComponents.RECIPES, Items.KNOWLEDGE_BOOK)
        associate(DataComponents.REPAIR_COST, Items.EXPERIENCE_BOTTLE)
        associate(DataComponents.REPAIRABLE, Items.ANVIL)
        associate(DataComponents.STORED_ENCHANTMENTS, Items.ENCHANTED_BOOK)
        associate(DataComponents.SUSPICIOUS_STEW_EFFECTS, Items.SUSPICIOUS_STEW)
        associate(DataComponents.SWING_ANIMATION, Items.NETHERITE_SPEAR)
        associate(DataComponents.TOOL, Items.DIAMOND_SHOVEL)
        associate(DataComponents.TOOLTIP_DISPLAY, Items.ITEM_FRAME)
        associate(DataComponents.TOOLTIP_STYLE, Items.PAINTING)
        associate(DataComponents.TRIM, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE)
        associate(DataComponents.UNBREAKABLE, Items.BEDROCK)
        associate(DataComponents.USE_COOLDOWN, Items.ENDER_PEARL)
        associate(DataComponents.USE_EFFECTS, Items.GOLDEN_SPEAR)
        associate(DataComponents.USE_REMAINDER, Items.MILK_BUCKET)
        associate(DataComponents.WEAPON, Items.DIAMOND_SWORD)
        associate(DataComponents.WRITABLE_BOOK_CONTENT, Items.WRITABLE_BOOK)
        associate(DataComponents.WRITTEN_BOOK_CONTENT, Items.WRITTEN_BOOK)
    }

    private fun associate(dataComponentType: DataComponentType<*>, item: Item) {
        iconMap[dataComponentType] = item
    }

    fun getIconFor(dataComponentType: DataComponentType<*>): Item {
        return iconMap[dataComponentType] ?: return Items.COMMAND_BLOCK
    }

}