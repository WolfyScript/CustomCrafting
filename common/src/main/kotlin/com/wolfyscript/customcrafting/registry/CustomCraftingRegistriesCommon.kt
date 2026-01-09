package com.wolfyscript.customcrafting.registry

import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeModel
import com.wolfyscript.customcrafting.editor.domain.recipes.RecipeTypeSpecificStateFactories
import com.wolfyscript.customcrafting.editor.domain.RecipeCraftingStateFactory
import com.wolfyscript.customcrafting.recipes.*
import com.wolfyscript.customcrafting.recipes.actions.CommandResultAction
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientConsumer
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientConsumers
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientMatcher
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientMatchers
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientRemainder
import com.wolfyscript.customcrafting.recipes.ingredient.IngredientRemainders
import com.wolfyscript.scafall.config.jackson.registerTypeRegistry
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.Registry
import com.wolfyscript.scafall.registry.RegistryKey
import com.wolfyscript.scafall.registry.RegistryReference
import com.wolfyscript.scafall.registry.RegistrySimple
import com.wolfyscript.scafall.wrappers.snapshot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class CustomCraftingRegistriesCommon : CustomCraftingRegistries {

    private val rootRegistry = RegistrySimple<Registry<*>>(CustomCraftingRegistryTypes.root)

    fun initRegistries() {
        createRegistry(CustomCraftingRegistryTypes.recipeTypes) {
            RegistrySimple<RecipeType<*>>(it).apply {
                register(RecipeTypes.crafting.key.key, RecipeTypeImpl(CustomRecipeCrafting::class.java, ItemStack(Items.CRAFTING_TABLE).snapshot()))
                register(RecipeTypes.cooking.key.key, RecipeTypeImpl(CustomRecipeCooking::class.java, ItemStack(Items.FURNACE).snapshot()))
                register(RecipeTypes.mixing.key.key, RecipeTypeImpl(CustomRecipeMixing::class.java, ItemStack(Items.CAULDRON).snapshot()))
                register(RecipeTypes.repairing.key.key, RecipeTypeImpl(CustomRecipeRepairing::class.java, ItemStack(Items.ANVIL).snapshot()))
                register(RecipeTypes.smithing.key.key, RecipeTypeImpl(CustomRecipeSmithing::class.java, ItemStack(Items.SMITHING_TABLE).snapshot()))
                register(RecipeTypes.stonecutting.key.key, RecipeTypeImpl(CustomRecipeStonecutting::class.java, ItemStack(Items.STONECUTTER).snapshot()))
                register(RecipeTypes.grinding.key.key, RecipeTypeImpl(CustomRecipeGrinding::class.java, ItemStack(Items.GRINDSTONE).snapshot()))
            }
        }

        createRegistry(CustomCraftingRegistryTypes.recipeConditionTypes) { RegistrySimple(it) }
        createRegistry(CustomCraftingRegistryTypes.recipeItemTransmuters) { RegistrySimple(it) }
        createRegistry(CustomCraftingRegistryTypes.resultActions) {
            RegistrySimple<Class<out ResultAction>>(it).apply {
                register(ResultActions.command.key.key, CommandResultAction::class.java)
            }
        }

        createRegistry(CustomCraftingRegistryTypes.ingredientConsumers) {
            RegistrySimple<Class<out IngredientConsumer>>(it).apply {
                register(IngredientConsumers.consume.key.key, IngredientConsumerConsumeImpl::class.java)
                register(IngredientConsumers.replace.key.key, IngredientConsumerReplaceImpl::class.java)
                register(IngredientConsumers.keep.key.key, IngredientConsumerKeepImpl::class.java)
            }
        }
        createRegistry(CustomCraftingRegistryTypes.ingredientMatchers) {
            RegistrySimple<Class<out IngredientMatcher>>(it).apply {
                register(IngredientMatchers.exact.key.key, IngredientMatcherExactImpl::class.java)
                register(IngredientMatchers.item.key.key, IngredientMatcherItemImpl::class.java)
            }
        }
        createRegistry(CustomCraftingRegistryTypes.ingredientRemainders) {
            RegistrySimple<Class<out IngredientRemainder>>(it).apply {
                register(IngredientRemainders.custom.key.key, IngredientRemainderCustomImpl::class.java)
                register(IngredientRemainders.default.key.key, IngredientRemainderDefaultImpl::class.java)
            }
        }

        createRegistry(CustomCraftingRegistryTypes.recipeTypeSpecificModelFactories) {
            RegistrySimple<RecipeModel.RecipeTypeSpecificModel.Factory<*>>(it).apply {
                register(RecipeTypeSpecificStateFactories.crafting.key.key, RecipeCraftingStateFactory())
            }
        }
        createRegistry(CustomCraftingRegistryTypes.conditionStores) { RegistrySimple(it) }
        createRegistry(CustomCraftingRegistryTypes.recipeItemTransmuterStores) { RegistrySimple(it) }
        createRegistry(CustomCraftingRegistryTypes.resultActionStores) { RegistrySimple(it) }

        registerJacksonTypes()
    }

    fun registerJacksonTypes() {
        registerTypeRegistry(
            ResultAction::class.java,
            get(CustomCraftingRegistryTypes.resultActions.key).getOrThrow()
        )
        registerTypeRegistry(
            IngredientConsumer::class.java,
            get(CustomCraftingRegistryTypes.ingredientConsumers.key).getOrThrow()
        )
        registerTypeRegistry(
            IngredientMatcher::class.java,
            get(CustomCraftingRegistryTypes.ingredientMatchers.key).getOrThrow()
        )
        registerTypeRegistry(
            IngredientRemainder::class.java,
            get(CustomCraftingRegistryTypes.ingredientRemainders.key).getOrThrow()
        )

    }

    fun <T> createRegistry(type: RegistryReference<T>, loader: (key: Key) -> Registry<T>) {
        val registry = loader(type.key.registry)
        rootRegistry.register(type.key.registry, registry)
    }

    override fun <T> get(type: RegistryKey<T>): Result<Registry<T>> {
        val registry = rootRegistry[type.registry]
        if (registry == null) {
            return Result.failure(IllegalArgumentException("No registry found for ${type.registry}"))
        }
        return Result.success(registry as Registry<T>)
    }

}