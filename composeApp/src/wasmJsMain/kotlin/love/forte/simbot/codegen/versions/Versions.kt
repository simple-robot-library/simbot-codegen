package love.forte.simbot.codegen.versions

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import web.http.Request
import web.http.fetch
import web.http.text

// https://docs.github.com/zh/rest/releases/releases?apiVersion=2022-11-28#get-the-latest-release

@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
    isLenient = true
    ignoreUnknownKeys = true
    allowTrailingComma = true
}

@OptIn(ExperimentalWasmJsInterop::class)
suspend fun fetchLatest(owner: String, repo: String): GitHubRelease {
    val req = Request(url = "https://api.github.com/repos/$owner/$repo/releases/latest")
    req.headers.append("X-GitHub-Api-Version", "2022-11-28")
    req.headers.append("Accept", "application/vnd.github.v3+json")
    req.headers.append("User-Agent", "simbot-codegen/1.0")

    val fetchResponse = fetch(req)

    // Check if response is ok
    if (!fetchResponse.ok) {
        throw RuntimeException("GitHub API request failed: ${fetchResponse.status} ${fetchResponse.statusText}")
    }

    val responseText = fetchResponse.text()

    // Check if we got valid JSON
    if (responseText.isBlank()) {
        throw RuntimeException("Empty response from GitHub API")
    }

    return json.decodeFromString(responseText)
}

@Serializable
data class GitHubRelease(
    val id: String,
    val url: String,
    @SerialName("assets_url")
    val assetsUrl: String,
    @SerialName("upload_url")
    val uploadUrl: String,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("tag_name")
    val tagName: String,
    val draft: Boolean,
    val prerelease: Boolean,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("published_at")
    val publishedAt: String,
)
