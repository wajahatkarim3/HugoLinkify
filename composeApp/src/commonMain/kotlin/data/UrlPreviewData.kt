package data

data class UrlPreviewData(
    val host: String,
    val thumbnailUrl: String?,
    val title: String,
    val description: String
)

object URLConstants {
    const val PROTOCOL: String = "http://"
    const val PROTOCOL_S: String = "https://"
    const val ROOT_URL_FAVOR_ICON: String = "https://www.google.com/s2/favicons?domain="

    fun isValidUrl(url: String): Boolean {
        val validUrlRegex = Regex("[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")
        return url.matches(validUrlRegex)
    }
}