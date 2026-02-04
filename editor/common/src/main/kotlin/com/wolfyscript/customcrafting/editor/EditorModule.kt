package com.wolfyscript.customcrafting.editor

import com.wolfyscript.customcrafting.core.registry.CustomCraftingRegistries
import com.wolfyscript.customcrafting.core.util.ModuleImpl
import com.wolfyscript.scafall.loader.module.Client
import com.wolfyscript.scafall.loader.module.Module
import org.jetbrains.annotations.ApiStatus

@ModuleImpl(EditorModuleImpl::class)
interface EditorModule : Module<EditorServer, Client> {

    val registries: CustomCraftingRegistries

    companion object {

        private var instance: EditorModule? = null

        fun get(): EditorModule {
            return instance ?: throw IllegalStateException("EditorModule not initialized")
        }

        fun registered(): Boolean {
            return instance != null
        }

        @JvmSynthetic
        @ApiStatus.Internal
        internal fun register(module: EditorModule) {
            if (registered()) {
                throw IllegalStateException("EditorModule already registered")
            }
            this.instance = module
        }

    }

}
