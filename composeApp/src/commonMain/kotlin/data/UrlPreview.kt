package data

import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object UrlPreview {
    suspend fun fetchData(url: String): UrlPreviewData? {
        var previewHost = ""
        var previewTitle = ""
        var previewDescription = ""
        var previewImage = ""
        var previewUrl = ""

        try {
            val validUrl = if (url.startsWith(URLConstants.PROTOCOL) || url.startsWith(URLConstants.PROTOCOL_S)) {
                url
            } else {
                URLConstants.PROTOCOL + url
            }

            val host = Url(validUrl).host
            previewHost = host

            println(validUrl)
            println(host)

            val client = createHttpClient(createJson(), false)
            val doc: HttpResponse = client.get(validUrl)
            //println(doc.bodyAsText())

            val handler = KsoupHtmlHandler
                .Builder()
                .onOpenTag { name, attributes, isImplied ->
                    if (name == "meta") {
                        //println(name)
                        //println(attributes)
                        //println("--------------")

                        if (attributes.size == 2) {
                            val propertyKey = attributes.keys.elementAt(0)
                            val contentKey = attributes.keys.elementAt(1)
                            val property = attributes.values.elementAt(0)
                            val content = attributes.values.elementAt(1)

                            if (propertyKey == "property" && contentKey == "content") {
                                when(property) {
                                    "og:url" -> {
                                        previewUrl = host + content
                                    }

                                    "og:image" -> {
                                        previewImage = content
                                    }

                                    "og:title" -> {
                                        previewTitle = content
                                    }

                                    "og:description" -> {
                                        previewDescription = content
                                    }
                                }
                            }
                        }
                    }
                }
                .build()

            val ksoupHtmlParser = KsoupHtmlParser(handler = handler)
            ksoupHtmlParser.parseComplete(doc.bodyAsText())
            ksoupHtmlParser.end()

            // println(ksoupHtmlParser)

            val result = UrlPreviewData(
                host = previewHost,
                title = previewTitle.substring(0..50),          // SEO title should be 50-60 chars
                description = previewDescription.substring(0..160), // SEO desc should be max 160 chars
                thumbnailUrl = previewImage
            )
            // println(result)
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun createJson() = Json { isLenient = true; ignoreUnknownKeys = true }

    private fun createHttpClient(json: Json, enableNetworkLogs: Boolean) = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        if (enableNetworkLogs) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.NONE
            }
        }
    }
}