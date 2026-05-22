package com.wolfyscript.customcrafting.core.recipe

import com.wolfyscript.scafall.identifier.Key
import java.lang.ref.WeakReference

internal class RecipeReferenceImpl<T: CustomRecipe<*,*>>(
    override val key: Key,
    recipe: T
) : RecipeReference<T> {

    private val ref = WeakReference(recipe)
    override val type: RecipeType<*> = recipe.type

    override val value: T?
        get() {
            return ref.get()
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RecipeReferenceImpl<*>) return false

        if (key != other.key) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }


}