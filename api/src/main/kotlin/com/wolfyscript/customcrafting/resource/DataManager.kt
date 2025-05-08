package com.wolfyscript.customcrafting.resource

import com.fasterxml.jackson.databind.ObjectMapper

interface DataManager {

    val resourceLoader: ResourceLoader

    val jacksonObjectMapper: ObjectMapper

    fun loadData()

}