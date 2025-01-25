package httpclient

import extension.ktor.httpclient.call
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*

class HttpClientTest : StringSpec({
    val httpClient = HttpClient(CIO)

    "call api with get method" {
        val result = httpClient.call<String>(CHERHY_GITHUB_URL, HttpMethod.Get)

        result shouldNotBe null
        result shouldContain "lolmageap"
    }

    "call api with post method" {
        val result = httpClient.call<String>(CHERHY_GITHUB_URL, HttpMethod.Post)

        result shouldNotBe null
        result.trim() shouldBe "Cookies must be enabled to use GitHub."
    }
})

private const val CHERHY_GITHUB_URL = "https://github.com/lolmageap"