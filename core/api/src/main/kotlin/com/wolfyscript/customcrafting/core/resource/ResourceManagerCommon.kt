package com.wolfyscript.customcrafting.core.resource

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.wolfyscript.customcrafting.core.CustomCrafting
import com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientDeserializerModifier
import com.wolfyscript.customcrafting.core.recipe.ingredient.IngredientSerializerModifier
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper
import com.wolfyscript.scafall.config.jackson.registerScafallModule
import java.io.File

internal class ResourceManagerCommon(val customCrafting: CustomCrafting, val directory: File) : ResourceManager {

    companion object {
        const val RESOURCES_PATH = "resources"
    }

    override val resourceLoader: ResourceLoader = ResourceLoaderImpl(
        customCrafting,
        customCrafting.configurationManager.resourceSettings,
        File(directory, RESOURCES_PATH)
    )
    override val backupManager: BackupManager = BackupManagerImpl(
        customCrafting,
        this,
        customCrafting.configurationManager.resourceSettings.backup
    )

    override val jacksonObjectMapper: ObjectMapper = HoconMapper()

    init {
        jacksonObjectMapper.registerModule(SimpleModule("IngredientModifier").apply {
            setSerializerModifier(IngredientSerializerModifier())
            setDeserializerModifier(IngredientDeserializerModifier())
        })

        jacksonObjectMapper.registerKotlinModule()
        jacksonObjectMapper.registerScafallModule()
    }

    override fun loadResources() {
        resourceLoader.loadResources()
    }
}