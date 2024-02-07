import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.CustomTextField
import data.URLConstants
import data.UrlPreview
import io.ktor.http.Url
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.skiko.ClipboardManager

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LinkifyView() {
    MaterialTheme {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            HowToUseSection(
                modifier = Modifier.weight(1f)
            )

            LinkifySection(
                modifier = Modifier.weight(1f).background(color = Color(0xFFEEEEEE))
            )
        }
    }
}

@Composable
fun LinkifySection(
    modifier: Modifier
) {
    var txtUrl by remember { mutableStateOf("") }
    var isValidUrl by remember { mutableStateOf(true) }
    var previewImage by remember { mutableStateOf("") }
    var previewTitle by remember { mutableStateOf("") }
    var previewDescription by remember { mutableStateOf("") }
    var previewHost by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxHeight()
            .padding(36.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "The URL for which you want preview.",
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp,
            modifier = Modifier
        )

        OutlinedTextField(
            value = txtUrl,
            modifier = Modifier.fillMaxWidth()
                .padding(top = 4.dp)
                .onKeyEvent { event ->
                    println(event.isMetaPressed)
                    if (event.isCtrlPressed && event.key == Key.V) {
                        println(event.type)
                        return@onKeyEvent true
                    }
                    else if (event.isMetaPressed && event.key == Key.V) {
                        println(event.type)
                        return@onKeyEvent true
                    }
                    false
                },
            onValueChange = { txtUrl = it },
            placeholder = { Text(text = "https://...") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            isError = isValidUrl.not()
        )

        Button(
            onClick = {
                isValidUrl = URLConstants.isValidUrl(txtUrl)
                if (txtUrl.isEmpty() || URLConstants.isValidUrl(txtUrl).not()) {
                    return@Button
                }

                coroutineScope.launch {
                    val urlPreviewData = UrlPreview.fetchData(txtUrl)

                    urlPreviewData?.let {
                        previewHost = urlPreviewData.host
                        previewImage = urlPreviewData.thumbnailUrl ?: ""
                        previewTitle = urlPreviewData.title
                        previewDescription = urlPreviewData.description
                    }

                }
            },
            modifier = Modifier.fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text(
                text = "Generate Link Preview",
                modifier = Modifier.padding(8.dp)
            )
        }

        LinkPreviewBox(
            image = previewImage,
            title = previewTitle,
            hostUrl = previewHost,
            description = previewDescription
        )

        ShortCodeBox(
            image = previewImage,
            title = previewTitle,
            hostUrl = previewHost,
            description = previewDescription
        )

        CreditFooter()
    }
}

@Composable
fun LinkPreviewBox(
    image: String = "https://ik.imagekit.io/z23yf8euyq/Articles/Screen_Time/image.png",
    title: String = "Your Blog Post Title",
    description: String = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ad eum dolorum architecto obcaecati enim dicta praesentium, quam nobis! Neque ad aliquam facilis numquam. Veritatis, sit",
    hostUrl: String = "wajahatkarim.com"
) {
    Row (
        modifier = Modifier.fillMaxWidth()
            .padding(top = 20.dp)
            .height(intrinsicSize = IntrinsicSize.Max)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(size = 4.dp)
            ),
        verticalAlignment = Alignment.Top
    ) {

        Box(modifier = Modifier.weight(0.25f)
            .aspectRatio(3/2f)
            .background(color = Color.Gray)) {

        }

        Column(
            modifier = Modifier.weight(0.75f)
                .background(color = Color.White)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp),
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = description,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp, end = 12.dp),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Normal,
            )

            Text(
                text = hostUrl,
                modifier = Modifier.padding(start = 12.dp, top = 10.dp, end = 12.dp),
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ShortCodeBox(
    image: String = "https://ik.imagekit.io/z23yf8euyq/Articles/Screen_Time/image.png",
    title: String = "Your Blog Post Title",
    description: String = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ad eum dolorum architecto obcaecati enim dicta praesentium, quam nobis! Neque ad aliquam facilis numquam. Veritatis, sit",
    hostUrl: String = "wajahatkarim.com"
) {

    Text(
        text = "The LinkCard Code will be generated here. Copy this code in your website.",
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
        modifier = Modifier.padding(top = 20.dp)
    )

    /*
    {{% linkpreview title="Meet Wajahat Karim, Pakistan's first Android Google Developer Expert (GDE)" subtitle="Wajahat Karim an open-source contributor, android developer, and writer has now become a Google Developer Expert in the field of Android for his..." link="https://www.techjuice.pk/meet-wajahat-karim-pakistans-first-android-google-developer-expert-gde/" thumbnail="https://cdn.techjuice.pk/wp-content/uploads/2020/02/IMG-20191218-WA0008-1024x682.jpg" %}}
     */

    Box(
        modifier = Modifier.padding(top = 4.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(size = 4.dp)
            )
            .background(color = Color(0xFF595959))
            .padding(12.dp),
    ) {
        Text(
            text = """
            {{% linkpreview title="${title}" subtitle="${description}" link="${hostUrl}" thumbnail="${image}" %}}
        """.trimIndent(),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Thin,
            color = Color.White
        )
    }

}

@Composable
fun CreditFooter() {
    Text(
        text = "Built with ❤ by Wajahat Karim",
        fontSize = 8.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        color = Color.Black,
        modifier = Modifier.padding(top = 30.dp)
    )
}

@Composable
fun HowToUseSection(
    modifier: Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight()
            .padding(36.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hugo Linkify",
            fontSize = 48.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Craft stunning link previews effortlessly with HugoLinkify,\nthe ultimate Hugo-inspired link card generator for web developers.",
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            lineHeight = 25.sp,
            modifier = Modifier.padding(top = 18.dp)
        )

        Text(
            text = "Here's how it works:",
            fontSize = 15.6.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 40.dp)
        )

        Text(
            text = "1. Copy this shortcode into your website's shortcodes.\n" +
                "2. Use this website to generate preview link card of any given URL.\n" +
                "3. Copy the new code of the given URL into your blog posts.\n" +
                "4. You're done!\n",
            fontSize = 14.4.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp,
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}
