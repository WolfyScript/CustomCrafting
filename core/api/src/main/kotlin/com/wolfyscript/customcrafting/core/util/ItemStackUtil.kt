package com.wolfyscript.customcrafting.core.util

import com.wolfyscript.scafall.items.ItemStackRef
import com.wolfyscript.scafall.wrappers.minecraft.unwrap
import net.minecraft.world.item.ItemStackTemplate

// TODO: Move this to scafall
fun ItemStackRef.toTemplate(): ItemStackTemplate {
    val stack = this.create().unwrap()
    return ItemStackTemplate(stack.item, stack.componentsPatch)
}
