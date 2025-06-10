package com.wolfyscript.customcrafting.util

import com.wolfyscript.customcrafting.CustomCraftingCommon
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun getResourceAsStream(pathToResource: String): InputStream? {
    return CustomCraftingCommon::class.java.classLoader.getResourceAsStream(pathToResource)
}

fun exportResource(pathToResource: String, outDest: File) {
    if (!outDest.exists()) {
        if (!outDest.parentFile.mkdirs() || !outDest.createNewFile()) {
            return
        }
    }

    val inputStream = getResourceAsStream(pathToResource)
    if (inputStream == null) {
        return
    }
    val outputStream = FileOutputStream(outDest)

    inputStream.use { input ->
        outputStream.use { out ->
            input.copyTo(out)
        }
    }

}