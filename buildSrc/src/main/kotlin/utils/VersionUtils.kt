package utils

import kotlin.collections.joinToString
import kotlin.collections.mapIndexed
import kotlin.text.indexOfFirst
import kotlin.text.split
import kotlin.text.substring
import kotlin.text.toInt
import kotlin.to

fun String.convertToEpochVer(prefixFactor: Int = 1000): String {
    val prefixMapping = mapOf("a" to 1, "alpha" to 1, "b" to 2, "beta" to 2, "rc" to 6, "lts" to 9)
    val split = split('-')
    val verSections = split[0].split('.')
    val epochVer = verSections.mapIndexed { index, section ->
        val prefix = section.substring(0, section.indexOfFirst { it >= '0' && it <= '9' })
        val encoded = prefixMapping[prefix]
        if (encoded != null) {
            val verNum = section.substring(prefix.length).toInt()
            return@mapIndexed "${(encoded * prefixFactor) + verNum}"
        }
        if (index == 0) {
            // Use default release encoding for the first number
            val verNum = section.substring(prefix.length).toInt()
            return@mapIndexed "${(8 * prefixFactor) + verNum}"
        }
        return@mapIndexed section
    }.joinToString(".")

    if (split.size > 1) {
        println("Converted version: $this -> $epochVer-${split[1]}")
        return "${epochVer}-${split[1]}"
    }
    println("Converted version: $this -> $epochVer")
    return epochVer
}
