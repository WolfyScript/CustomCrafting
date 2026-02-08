package com.wolfyscript.customcrafting.ui

import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistries
import com.wolfyscript.customcrafting.core.util.ModuleImpl
import com.wolfyscript.scafall.loader.module.Client
import com.wolfyscript.scafall.loader.module.Module
import com.wolfyscript.scafall.loader.module.Server
import org.jetbrains.annotations.ApiStatus

@ModuleImpl(UIModuleImpl::class)
interface UIModule : Module<Server, Client> {

    val registries: CustomCraftingRegistries

    companion object {
        private var instance: UIModule? = null

        fun get(): UIModule {
            return instance ?: throw IllegalStateException("UIModule not initialized")
        }

        fun registered(): Boolean {
            return instance != null
        }

        @JvmSynthetic
        @ApiStatus.Internal
        internal fun register(module: UIModule) {
            if (registered()) {
                throw IllegalStateException("UIModule already registered")
            }
            this.instance = module
        }
    }

}