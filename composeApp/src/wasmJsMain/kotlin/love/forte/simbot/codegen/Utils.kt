package love.forte.simbot.codegen

import love.forte.codegentle.common.naming.isEmpty
import love.forte.codegentle.common.naming.nameSequence
import love.forte.codegentle.java.JavaFile
import love.forte.codegentle.kotlin.KotlinFile

fun KotlinFile.toRelativePath0(
    filename: String = type.name,
    isScript: Boolean = false,
    separator: String = "/"
): String {
    val filenameWithExtension = if (filename.contains('.')) {
        filename
    } else {
        filename + if (isScript) ".kts" else ".kt"
    }

    val packageName = this.packageName
    return if (packageName.isEmpty()) {
        filenameWithExtension
    } else {
        packageName.nameSequence().joinToString(separator = separator) { it.name } + separator + filenameWithExtension
    }
}

fun JavaFile.toRelativePath0(filename: String = type.name ?: "", separator: String = "/"): String {
    val filenameWithExtension = if (filename.contains('.')) {
        filename
    } else {
        "$filename.java"
    }

    val packageName = this.packageName
    return if (packageName.isEmpty()) {
        filenameWithExtension
    } else {
        packageName.nameSequence().joinToString(separator = separator) { it.name } + separator + filenameWithExtension
    }
}
