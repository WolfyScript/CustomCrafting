package com.wolfyscript.customcrafting.util

import com.wolfyscript.scafall.loader.module.Module
import kotlin.reflect.KClass

annotation class ModuleImpl(
    val implType: KClass<out Module<*,*>>
) {

}
