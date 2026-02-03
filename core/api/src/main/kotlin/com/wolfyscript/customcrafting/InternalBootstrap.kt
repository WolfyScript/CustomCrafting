package com.wolfyscript.customcrafting

import com.wolfyscript.customcrafting.core.CustomCrafting
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
internal class InternalBootstrap(innerJarClassloader: ClassLoader) : CustomCraftingBoostrap(innerJarClassloader) {

    override val registered: Boolean
        get() = CustomCraftingProvider.registered()

    override fun register(module: CustomCrafting) {
        CustomCraftingProvider.register(module)
    }

    override fun onCompleted(module: CustomCrafting) {
        CustomCraftingProvider.notifyListeners()
    }

}