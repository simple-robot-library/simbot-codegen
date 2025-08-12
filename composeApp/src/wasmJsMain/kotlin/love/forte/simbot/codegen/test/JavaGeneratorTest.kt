package love.forte.simbot.codegen.test

import jszip.JSZip
import love.forte.simbot.codegen.gen.core.*
import love.forte.simbot.codegen.gen.core.context.GenerationContextImpl
import love.forte.simbot.codegen.gen.core.generators.java.JavaSourceCodeGeneratorImpl

/**
 * Java 代码生成器测试。
 * 
 * 验证 JavaSourceCodeGeneratorImpl 的基本功能。
 */

/**
 * 测试 Java Spring 代码生成。
 */
suspend fun testJavaSpringGeneration() {
    println("Testing Java Spring code generation...")
    
    try {
        // 创建生成上下文
        val context = GenerationContextImpl(
            projectName = "test-project",
            packageName = "com.example.test",
            language = ProgrammingLanguage.Java("17", JavaStyle.BLOCKING),
            framework = Framework.Spring("3.2.0"),
            components = emptyList(),
            dependencies = emptyList()
        )
        
        // 创建 Java 生成器
        val javaGenerator = JavaSourceCodeGeneratorImpl()
        
        // 创建 ZIP 容器
        val zip = JSZip()
        val projectDir = zip.folder("test-project") ?: throw IllegalStateException("无法创建项目目录")
        val srcDir = projectDir.folder("src") ?: throw IllegalStateException("无法创建 src 目录")
        val mainDir = srcDir.folder("main") ?: throw IllegalStateException("无法创建 main 目录")
        val javaDir = mainDir.folder("java") ?: throw IllegalStateException("无法创建 java 目录")
        val packageDir = createPackageDir(javaDir, context.packageName)
        
        // 生成应用入口
        javaGenerator.generateApplicationEntry(packageDir, context)
        
        // 生成事件处理器
        javaGenerator.generateEventHandlers(packageDir, context)
        
        println("✓ Java Spring code generation successful")
        
        // 验证生成的文件
        val mainAppFile = packageDir.file("MainApplication.java")
        val handleDir = packageDir.folder("handle")
        val eventHandlerFile = handleDir?.file("EventHandlers.java")
        
        if (mainAppFile == null) {
            println("✗ MainApplication.java not generated")
        } else {
            println("✓ MainApplication.java generated")
        }
        
        if (eventHandlerFile == null) {
            println("✗ EventHandlers.java not generated")
        } else {
            println("✓ EventHandlers.java generated")
        }
        
    } catch (e: Exception) {
        println("✗ Java Spring generation failed: ${e.message}")
        e.printStackTrace()
    }
}

/**
 * 测试 Java 异步风格代码生成。
 */
suspend fun testJavaAsyncGeneration() {
    println("Testing Java Async code generation...")
    
    try {
        // 创建生成上下文 - 使用异步风格
        val context = GenerationContextImpl(
            projectName = "test-async-project",
            packageName = "com.example.async",
            language = ProgrammingLanguage.Java("17", JavaStyle.ASYNC),
            framework = Framework.Spring("3.2.0"),
            components = emptyList(),
            dependencies = emptyList()
        )
        
        // 创建 Java 生成器
        val javaGenerator = JavaSourceCodeGeneratorImpl()
        
        // 创建 ZIP 容器
        val zip = JSZip()
        val projectDir = zip.folder("test-async-project") ?: throw IllegalStateException("无法创建项目目录")
        val srcDir = projectDir.folder("src") ?: throw IllegalStateException("无法创建 src 目录")
        val mainDir = srcDir.folder("main") ?: throw IllegalStateException("无法创建 main 目录")
        val javaDir = mainDir.folder("java") ?: throw IllegalStateException("无法创建 java 目录")
        val packageDir = createPackageDir(javaDir, context.packageName)
        
        // 生成应用入口
        javaGenerator.generateApplicationEntry(packageDir, context)
        
        // 生成事件处理器
        javaGenerator.generateEventHandlers(packageDir, context)
        
        println("✓ Java Async code generation successful")
        
    } catch (e: Exception) {
        println("✗ Java Async generation failed: ${e.message}")
        e.printStackTrace()
    }
}

/**
 * 测试 Java Core 框架代码生成。
 */
suspend fun testJavaCoreGeneration() {
    println("Testing Java Core framework code generation...")
    
    try {
        // 创建生成上下文 - 使用 Core 框架
        val context = GenerationContextImpl(
            projectName = "test-core-project",
            packageName = "com.example.core",
            language = ProgrammingLanguage.Java("17", JavaStyle.BLOCKING),
            framework = Framework.Core,
            components = emptyList(),
            dependencies = emptyList()
        )
        
        // 创建 Java 生成器
        val javaGenerator = JavaSourceCodeGeneratorImpl()
        
        // 创建 ZIP 容器
        val zip = JSZip()
        val projectDir = zip.folder("test-core-project") ?: throw IllegalStateException("无法创建项目目录")
        val srcDir = projectDir.folder("src") ?: throw IllegalStateException("无法创建 src 目录")
        val mainDir = srcDir.folder("main") ?: throw IllegalStateException("无法创建 main 目录")
        val javaDir = mainDir.folder("java") ?: throw IllegalStateException("无法创建 java 目录")
        val packageDir = createPackageDir(javaDir, context.packageName)
        
        // 生成应用入口
        javaGenerator.generateApplicationEntry(packageDir, context)
        
        // 生成事件处理器
        javaGenerator.generateEventHandlers(packageDir, context)
        
        println("✓ Java Core code generation successful")
        
    } catch (e: Exception) {
        println("✗ Java Core generation failed: ${e.message}")
        e.printStackTrace()
    }
}

/**
 * 创建包目录结构。
 */
private fun createPackageDir(baseDir: JSZip, packageName: String): JSZip {
    var currentDir = baseDir
    packageName.split('.').forEach { pkg ->
        currentDir = currentDir.folder(pkg) ?: throw IllegalStateException("无法创建包目录: $pkg")
    }
    return currentDir
}

/**
 * 运行所有测试。
 */
suspend fun runJavaGeneratorTests() {
    println("=== Java Code Generator Tests ===")
    
    testJavaSpringGeneration()
    println()
    
    testJavaAsyncGeneration()
    println()
    
    testJavaCoreGeneration()
    println()
    
    println("=== Tests completed ===")
}