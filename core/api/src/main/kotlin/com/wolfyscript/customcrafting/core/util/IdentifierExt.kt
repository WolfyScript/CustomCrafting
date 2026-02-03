package com.wolfyscript.customcrafting.core.util

import com.wolfyscript.scafall.identifier.Key

const val CUSTOMCRAFTING_NAMESPACE = "customcrafting"

val Key.Companion.CUSTOMCRAFTING_NAMESPACE
    get() = "customcrafting"

fun Key.Companion.customCrafting(value: String): Key {
    return Key.key("customcrafting", value)
}