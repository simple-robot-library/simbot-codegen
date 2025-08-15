package love.forte.simbot.codegen

import love.forte.codegentle.common.naming.isEmpty
import love.forte.codegentle.common.naming.toRelativePath
import love.forte.codegentle.kotlin.KotlinFile

fun KotlinFile.toRelativePath(
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
        packageName.toRelativePath(separator) + separator + filenameWithExtension
    }
}
