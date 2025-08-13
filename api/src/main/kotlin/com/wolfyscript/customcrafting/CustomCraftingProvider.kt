package com.wolfyscript.customcrafting

import com.wolfyscript.scafall.Scafall
import com.wolfyscript.scafall.ScafallProvider
import org.jetbrains.annotations.ApiStatus

class CustomCraftingProvider {

    companion object {

        private var instance: CustomCrafting? = null
        private val listeners = mutableListOf<(CustomCrafting)->Unit>()

        fun get(): CustomCrafting {
            return instance ?: throw IllegalStateException("CustomCrafting not initialized")
        }

        fun registered(): Boolean {
            return instance != null
        }

        fun ifAvailable(action: (CustomCrafting)->Unit) {
            if (registered()) {
                action(get())
            }
        }

        fun whenReady(listener: (CustomCrafting)->Unit) {
            if (registered()) {
                listener(get())
            } else {
                listeners.add(listener)
            }
        }

        internal fun notifyListeners() {
            listeners.removeAll {
                it(instance!!)
                true
            }
        }

        @JvmSynthetic
        @ApiStatus.Internal
        internal fun register(instance: CustomCrafting) {
            if (registered()) {
                throw IllegalStateException("CustomCrafting already initialized")
            }
            this.instance = instance
        }

    }

}