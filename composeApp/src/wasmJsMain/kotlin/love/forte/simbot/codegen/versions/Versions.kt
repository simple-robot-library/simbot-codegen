package love.forte.simbot.codegen.versions

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.JsClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.use
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.w3c.fetch.Request
import org.w3c.fetch.Response

// https://docs.github.com/zh/rest/releases/releases?apiVersion=2022-11-28#get-the-latest-release

@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
    isLenient = true
    ignoreUnknownKeys = true
    allowTrailingComma = true
}

val Client by lazy {
    createClient()
}

fun createClient(): HttpClient {
    return HttpClient(JsClient()) {
        engine {
            this.pipelining = true
        }
        install(ContentNegotiation) {
            json(json)
        }
    }
}

suspend fun queryLatest(owner: String, repo: String): GitHubRelease? {
    return createClient().use {
        // val resp = it.get("https://api.github.com/repos/$owner/$repo/releases/latest") {
        val resp = it.get("https://api.forte.love/me/description?code=1") {
            // headers {
            //     this["X-GitHub-Api-Version"] = "2022-11-28"
            // }
        }

        null
    }
}

suspend fun fetchLatest(owner: String, repo: String): GitHubRelease {
    val req = Request(input = "https://api.github.com/repos/$owner/$repo/releases/latest".toJsString())
    req.headers.append("X-GitHub-Api-Version", "2022-11-28")

    val fetchResponse = window.fetch(input = req)
        .await<Response>()
    println(fetchResponse)
    val fetchBody = fetchResponse.text().await<JsString>()
    println(fetchBody)
    return json.decodeFromString(fetchBody.toString())

    // return createClient().use {
    //     it.get("https://api.github.com/repos/$owner/$repo/releases/latest") {
    //         headers {
    //             this["X-GitHub-Api-Version"] = "2022-11-28"
    //         }
    //     }.body<GitHubRelease>()
    // }
}

private fun toJSON(o: JsAny): String =
    js("JSON.stringify(o)")

// private fun

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
