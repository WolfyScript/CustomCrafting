package com.wolfyscript.customcrafting

import com.wolfyscript.scafall.loader.InnerJarClassloader
import com.wolfyscript.scafall.loader.module.Module
import com.wolfyscript.scafall.loader.module.StandaloneInternalBootstrap
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
abstract class CustomCraftingBoostrap(innerJarClassloader: InnerJarClassloader) :
    StandaloneInternalBootstrap<CustomCrafting>(CustomCraftingModule::class.java, innerJarClassloader) {

    companion object {
        const val PATH_TO_INTERNAL_BOOTSTRAP: String = "com.wolfyscript.customcrafting.InternalBootstrap"
    }

    interface CustomCraftingModule : Module<CustomCrafting>

}