package com.wolfyscript.customcrafting.core.data

import java.io.File

internal class DataManagerImpl(rootDir: File) : DataManager {

    companion object {
        const val DATA_PATH = ".data"
    }

    override val storageDir: File = File(rootDir, DATA_PATH)

}