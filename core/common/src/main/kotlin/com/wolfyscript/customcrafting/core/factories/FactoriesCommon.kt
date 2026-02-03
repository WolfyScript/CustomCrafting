package com.wolfyscript.customcrafting.core.factories

import com.wolfyscript.customcrafting.factories.Factories
import com.wolfyscript.customcrafting.factories.RecipeFactory

class FactoriesCommon : Factories {

    override val recipeFactory: RecipeFactory = RecipeFactoryCommon()

}