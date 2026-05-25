package com.wolfyscript.customcrafting

import com.wolfyscript.customcrafting.core.CustomCrafting
import org.jetbrains.annotations.ApiStatus

/**
 * A provider for the CustomCrafting instance.
 * This class is used to access the CustomCrafting instance and register listeners for when it becomes available.
 */
class CustomCraftingProvider {

    companion object {

        private var instance: CustomCrafting? = null
        private val listeners = mutableListOf<(CustomCrafting) -> Unit>()

        /**
         * Retrieves the CustomCrafting instance.
         *
         * @return The CustomCrafting instance.
         * @throws IllegalStateException if the CustomCrafting instance has not been initialized.
         */
        fun get(): CustomCrafting {
            return instance ?: throw IllegalStateException("CustomCrafting not initialized")
        }

        /**
         * Checks if the CustomCrafting instance has been registered.
         *
         * @return True if the CustomCrafting instance has been registered, false otherwise.
         */
        fun registered(): Boolean {
            return instance != null
        }

        /**
         * Executes the given action with the CustomCrafting instance if it is available.
         *
         * @param action The action to execute with the CustomCrafting instance.
         */
        fun ifAvailable(action: (CustomCrafting) -> Unit) {
            if (registered()) {
                action(get())
            }
        }

        /**
         * Executes the given listener with the CustomCrafting instance when it is available.
         * If the CustomCrafting instance is not available, the listener is registered to be executed when it becomes available.
         *
         * @param listener The listener to execute with the CustomCrafting instance.
         */
        fun whenReady(listener: (CustomCrafting) -> Unit) {
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
