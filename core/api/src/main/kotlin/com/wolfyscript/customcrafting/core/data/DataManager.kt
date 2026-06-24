package com.wolfyscript.customcrafting.core.data

import com.wolfyscript.customcrafting.core.CustomCrafting
import java.io.File

interface DataManager {

    companion object {

        const val DATA_PATH = ".data"

        fun createNewForDir(customCrafting: CustomCrafting, directory: File): DataManager {
            return DataManagerImpl(directory)
        }

    }

    val storageDir: File

}