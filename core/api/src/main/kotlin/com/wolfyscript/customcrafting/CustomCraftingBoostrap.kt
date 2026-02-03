package com.wolfyscript.customcrafting

import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.scafall.loader.module.StandaloneInternalBootstrap
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
abstract class CustomCraftingBoostrap(innerJarClassloader: ClassLoader) :
    StandaloneInternalBootstrap<CustomCrafting>(CustomCrafting::class.java, innerJarClassloader) {

    companion object {
        const val PATH_TO_INTERNAL_BOOTSTRAP: String = "com.wolfyscript.customcrafting.InternalBootstrap"
    }

}