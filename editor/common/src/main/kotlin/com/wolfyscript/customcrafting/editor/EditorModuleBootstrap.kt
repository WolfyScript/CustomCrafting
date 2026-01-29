package com.wolfyscript.customcrafting.editor

import com.wolfyscript.scafall.loader.module.StandaloneInternalBootstrap

class EditorModuleBootstrap(val classLoader: ClassLoader) : StandaloneInternalBootstrap<EditorModule>(
    EditorModule::class.java, classLoader) {

    override val registered: Boolean
        get() = TODO("Not yet implemented")

    override fun register(module: EditorModule) {
        TODO("Not yet implemented")
    }

}