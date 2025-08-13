package love.forte.simbot.codegen.gen

// libs.versions.toml
data class GradleCatalogVersionDependency(
    val dependencyName: String, // use in [dependencies]
    val group: String,
    val name: String,
    val version: GradleCatalogVersion?,
    val libRefPath: String = dependencyName.replace('-', '.'), // libs.[xxx.xxx.xxx]
    val configName: String = "implementation"
)

data class GradleCatalogVersion(val name: String?, val version: String)

data class GradleCatalogPlugin(
    val pluginName: String,
    val id: String,
    val version: GradleCatalogVersion?,
    val libRefPath: String = pluginName.replace('-', '.'),
)

/**
 * 生成 `libs.versions.toml` 中的内容。
 */
fun genGradleCatalogVersion(
    dependencies: List<GradleCatalogVersionDependency>,
    plugins: List<GradleCatalogPlugin> = emptyList(),
): String {
    val versions =
        (dependencies.asSequence().mapNotNull { it.version } + plugins.asSequence().mapNotNull { it.version })
            .filter { it.name != null }
            .associateBy { it.name!! }

    return buildString {
        if (versions.isNotEmpty()) {
            appendLine("[versions]")
            versions.forEach { (versionName, version) ->
                append(versionName).append(" = \"").append(version.version).appendLine('"')
            }
            appendLine()
        }

        // libraries
        if (dependencies.isNotEmpty()) {
            appendLine("[libraries]")
            dependencies.forEach { dependency ->
                append(dependency.dependencyName)
                // junit = { module = "junit:junit", version.ref = "junit" }
                append(" = { module = \"")
                    .append(dependency.group).append(":").append(dependency.name)
                append('"')

                val dependencyVersion = dependency.version
                if (dependencyVersion != null) {
                    append(", ").appendVersionOrRef(dependencyVersion)

                }
                appendLine(" }")
            }
            appendLine()
        }

        // plugins
        if (plugins.isNotEmpty()) {
            appendLine("[plugins]")
            plugins.forEach { plugin ->
                append(plugin.pluginName).append(" = { id = \"").append(plugin.id).append('\"')
                val pluginVersion = plugin.version
                if (pluginVersion != null) {
                    append(", ").appendVersionOrRef(pluginVersion)
                }
                appendLine(" }")
            }
            appendLine()
        }
    }
}

private fun Appendable.appendVersionOrRef(version: GradleCatalogVersion): Appendable {
    val (versionName, versionValue) = version
    return if (versionName != null) {
        // version.ref
        append("version.ref = \"").append(versionName).append('"')
    } else {
        // version
        append("version = \"").append(versionValue).append('"')
    }
}
