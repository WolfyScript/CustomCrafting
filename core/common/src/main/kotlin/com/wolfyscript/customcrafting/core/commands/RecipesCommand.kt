package com.wolfyscript.customcrafting.core.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.wolfyscript.customcrafting.CustomCrafting
import com.wolfyscript.customcrafting.CustomCraftingProvider
import com.wolfyscript.customcrafting.commands.SUCCESS_RESULT
import com.wolfyscript.customcrafting.recipes.RecipeManagerCommon
import com.wolfyscript.customcrafting.util.CUSTOMCRAFTING_NAMESPACE
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.toScafall
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.IdentifierArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.permissions.Permissions

object RecipesCommand {

    const val ROOT_NAME = "recipes"

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        sequenceOf(ROOT_NAME, "cc:$ROOT_NAME", "${Key.CUSTOMCRAFTING_NAMESPACE}:$ROOT_NAME").forEach { alias ->
            dispatcher.register(
                Commands.literal(alias).requires { it.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) }.apply {
                    then(Commands.literal("reload").executes { reload(CustomCraftingProvider.get()) })
                    then(Commands.literal("status").executes { ctx ->
                        printStatus(ctx, CustomCraftingProvider.get())
                        return@executes SUCCESS_RESULT
                    })
                    then(
                        Commands.literal("disable")
                            .then(Commands.argument("recipe", IdentifierArgument.id()).executes { ctx ->
                                val recipeKey = IdentifierArgument.getId(ctx, "recipe").toScafall()
                                CustomCraftingProvider.get().server!!.recipeManager.disableRecipe(recipeKey)

                                ctx.source.sendSuccess({ Component.literal("Disabled Recipe $recipeKey") }, false)
                                return@executes SUCCESS_RESULT
                            }.suggests { ctx, builder ->
                                (CustomCraftingProvider.get().server!!.recipeManager as RecipeManagerCommon).recipesLoadedByCC
                                    .map { it.toString() }
                                    .filter { it.startsWith(builder.remaining) }
                                    .forEach { builder.suggest(it) }

                                return@suggests builder.buildFuture()
                            })
                    )
                    then(
                        Commands.literal("enable")
                            .then(Commands.argument("recipe", IdentifierArgument.id()).executes { ctx ->
                                val recipeKey = IdentifierArgument.getId(ctx, "recipe").toScafall()
                                CustomCraftingProvider.get().server!!.recipeManager.enableRecipe(recipeKey)

                                ctx.source.sendSuccess({ Component.literal("Enabled Recipe $recipeKey") }, false)
                                return@executes SUCCESS_RESULT
                            }.suggests { ctx, builder ->
                                CustomCraftingProvider.get().server!!.recipeManager.disabledRecipes
                                    .map { it.toString() }
                                    .filter { it.startsWith(builder.remaining) }
                                    .forEach { builder.suggest(it) }

                                return@suggests builder.buildFuture()
                            })
                    )
                }
            )
        }
    }

    private fun reload(customCrafting: CustomCrafting): Int {
        ScafallProvider.get().scheduler.asyncTask(ScafallProvider.get().modInfo) {
            customCrafting.server!!.resourceManager.resourceLoader.loadResources()
        }
        return SUCCESS_RESULT
    }

    private fun printStatus(ctx: CommandContext<CommandSourceStack>, customCrafting: CustomCrafting) {
        val recipeManager = customCrafting.server!!.recipeManager as RecipeManagerCommon

        val totalRecipeCount = recipeManager.recipes().count()
        val ccRecipesCount = recipeManager.recipesLoadedByCC.size
        val thirdPartyRecipeCount = totalRecipeCount - ccRecipesCount
        val disabledRecipeCount = recipeManager.disabledRecipes.size
        val failedCount = recipeManager.invalidRecipes.size

        val message = """
            <green>Loaded Recipes: <b>$totalRecipeCount</b>
              CustomCrafting: $ccRecipesCount
              3rd-Parties: $thirdPartyRecipeCount
            </green>    
            <red>Failed to load: <b>$failedCount</b></red>
            
            <gray>Disabled Recipes: <b>$disabledRecipeCount</b></gray>
            """.trimIndent()

        ctx.source.sendSuccess({
            message.deser().vanilla()
        }, false)
    }

}