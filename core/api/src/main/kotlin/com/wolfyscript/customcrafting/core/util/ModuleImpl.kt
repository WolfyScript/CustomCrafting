package com.wolfyscript.customcrafting.core.util

import com.wolfyscript.scafall.loader.module.Module
import kotlin.reflect.KClass

annotation class ModuleImpl(
    val implType: KClass<out Module<*,*>>
) {

}
