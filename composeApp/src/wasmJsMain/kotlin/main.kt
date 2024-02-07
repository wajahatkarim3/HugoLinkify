import data.UrlPreview
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHeadingElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLLabelElement
import org.w3c.dom.HTMLParagraphElement

fun main() {
    val btnCreate = window.document.getElementById("btn_create_card") as HTMLInputElement
    btnCreate.addEventListener("click") {
        val inputUrl = document.getElementById("input_url") as HTMLInputElement
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            val urlPreviewData = UrlPreview.fetchData(inputUrl.value)
            println(urlPreviewData)

            urlPreviewData?.let {
                val shortcode = document.getElementById("linkcard_code") as HTMLLabelElement
                shortcode.textContent =
                    "{{% linkpreview title=\"${it.title}\"  subtitle=\"${it.description}\"  link=\"${it.host}\" thumbnail=\"${it.thumbnailUrl}\" %}}"

                // Copy Shortcode to Clipboard
                window.navigator.clipboard.writeText(shortcode.textContent!!)

                // Blogcard
                val title = document.getElementById("title") as HTMLHeadingElement
                title.textContent = it.title

                val summary = document.getElementById("summary") as HTMLParagraphElement
                summary.textContent = it.description

                val domain = document.getElementById("domain") as HTMLHeadingElement
                domain.textContent = it.host

                var photo = document.getElementById("photo") as HTMLDivElement
                photo.style.backgroundImage = "url(${it.thumbnailUrl})"
            }
        }
    }
}