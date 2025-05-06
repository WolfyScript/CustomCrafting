package com.wolfyscript.customcrafting.recipes

import com.wolfyscript.scafall.wrappers.world.Location
import com.wolfyscript.scafall.wrappers.world.entity.Player

interface EvaluationContext {

    val player: Player?

    val location: Location?

}