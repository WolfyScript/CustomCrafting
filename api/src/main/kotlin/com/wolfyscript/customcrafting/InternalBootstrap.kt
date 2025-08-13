package com.wolfyscript.customcrafting

import com.wolfyscript.scafall.loader.module.Module
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
internal class InternalBootstrap(innerJarClassloader: ClassLoader) : CustomCraftingBoostrap(innerJarClassloader) {

    override val registered: Boolean
        get() = CustomCraftingProvider.registered()

    override fun register(module: Module<CustomCrafting>) {
        CustomCraftingProvider.register(module.bridge)
    }


}