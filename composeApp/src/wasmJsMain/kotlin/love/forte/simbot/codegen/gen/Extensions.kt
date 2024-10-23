package love.forte.simbot.codegen.gen

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

internal inline fun FileSpec.Builder.inControlFlow(
    name: String,
    newStatementEnd: Boolean = true,
    vararg args: Any,
    block: FileSpec.Builder.() -> Unit
) {
    beginControlFlow(name, *args)
    block()
    endControlFlow()
    if (newStatementEnd) {
        addStatement("")
    }
}

internal inline fun CodeBlock.Builder.inControlFlow(
    name: String,
    newStatementEnd: Boolean = true,
    vararg args: Any,
    block: CodeBlock.Builder.() -> Unit
) {
    beginControlFlow(name, *args)
    block()
    endControlFlow()
    if (newStatementEnd) {
        addStatement("")
    }
}

internal inline fun FileSpec.Builder.addType(
    builder: TypeSpec.Builder,
    block: TypeSpec.Builder.() -> Unit
) {
    addType(builder.apply(block).build())
}

internal inline fun FileSpec.Builder.addClass(
    name: String,
    block: TypeSpec.Builder.() -> Unit
) {
    addType(TypeSpec.classBuilder(name), block)
}


internal inline fun TypeSpec.Builder.addFunction(
    name: String,
    block: FunSpec.Builder.() -> Unit
) {
    addFunction(FunSpec.builder(name), block)
}

internal inline fun TypeSpec.Builder.addFunction(
    builder: FunSpec.Builder,
    block: FunSpec.Builder.() -> Unit
) {
    addFunction(builder.apply(block).build())
}

internal inline fun FileSpec.Builder.addFunction(
    name: String,
    block: FunSpec.Builder.() -> Unit
) {
    addFunction(FunSpec.builder(name), block)
}

internal inline fun FileSpec.Builder.addFunction(
    builder: FunSpec.Builder,
    block: FunSpec.Builder.() -> Unit
) {
    addFunction(builder.apply(block).build())
}

internal inline fun fileSpec(
    packageName: String,
    className: String,
    block: FileSpec.Builder.() -> Unit
): FileSpec = FileSpec.builder(packageName, className).apply(block).indent("    ").build()

internal inline fun fileScriptSpec(
    fileName: String,
    block: FileSpec.Builder.() -> Unit
): FileSpec = FileSpec.scriptBuilder(fileName).apply(block).indent("    ").build()
