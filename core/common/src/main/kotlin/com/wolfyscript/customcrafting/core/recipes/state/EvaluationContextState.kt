package com.wolfyscript.customcrafting.core.recipes.state

import com.wolfyscript.customcrafting.core.recipes.EvaluationContext

object EvaluationContextState {

    private var context: EvaluationContext? = null
    val current: EvaluationContext? get() = context

    fun enter(context: EvaluationContext) {
        this.context = context
    }

    fun exit() {
        context = null
    }

    fun run(context: EvaluationContext, block: EvaluationContext.() -> Unit) {
        enter(context)
        block(context)
        exit()
    }

}