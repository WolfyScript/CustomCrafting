package com.wolfyscript.customcrafting

import org.jetbrains.annotations.ApiStatus

class CustomCraftingProvider {

    companion object {

        private var instance: CustomCrafting? = null

        fun get(): CustomCrafting {
            return instance ?: throw IllegalStateException("CustomCrafting not initialized")
        }

        fun registered(): Boolean {
            return instance != null
        }

        @JvmSynthetic
        @ApiStatus.Internal
        fun register(instance: CustomCrafting) {
            if (registered()) {
                throw IllegalStateException("CustomCrafting already initialized")
            }
            this.instance = instance
        }

    }

}