package com.wolfyscript.customcrafting.core.recipe.procedure

internal class ProcedureEnchantRemovalImpl(
    override val baseEnchants: ProcedureEnchantRemovalIngredientImpl = ProcedureEnchantRemovalIngredientImpl(),
    override val additionEnchants: ProcedureEnchantRemovalIngredientImpl = ProcedureEnchantRemovalIngredientImpl(),
) : ProcedureEnchantRemoval