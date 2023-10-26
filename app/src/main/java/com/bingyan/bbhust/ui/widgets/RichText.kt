package com.bingyan.bbhust.ui.widgets

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.bingyan.bbhust.ui.theme.AppTypography
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.ui.theme.purple
import com.bingyan.bbhust.ui.theme.themeColor
import com.bingyan.bbhust.utils.MarkdownEngine
import com.bingyan.bbhust.utils.dp2px
import com.bingyan.bbhust.utils.keep
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

object RichText {

    @Composable
    fun RenderText(text: String) {
        var html by keep(v = "")
        LaunchedEffect(text) {
            html = MarkdownEngine.parse(text)?.replace("×tamp", "&timestamp") ?: "渲染失败"
        }
        Render(html = html)
    }

    @Composable
    fun Render(html: String) {
        println("HTML: $html")
        ProvideTextStyle(
            value = TextStyle(
                lineHeight = 30.sp,
                fontSize = 16.sp,
                color = colors.textPrimary,
                fontWeight = FontWeight.W400
            )
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HandleChildren(self = Jsoup.parse(html).body())
            }
        }
    }


    @Composable
    private fun HandleChildren(self: Element) {
        if (self.tagName() == "blockquote") {
            Log.i("HandleChildren", "$self\n\nnodes:${self.childrenSize()},\n${
                self.childNodes().joinToString(separator = "\n") {
                    "node:$it"
                }
            }")
        }
        self.childNodes().filter { it.toString().isNotBlank() }.forEach { child ->
            if (child is Element) {
                when (child.tagName()) {
                    "p" -> Paragraph(child)
                    "blockquote" -> Blockquote(child)
                    "img" -> CacheImage(
                        src = child.attr("src"),
                        contentDescription = child.attr("alt"),
                        style = Style.Large,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .padding(2.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.FillWidth,
                    )

                    "tip" -> TipBlock(child)
                    "ext" -> ExtBlock(child)
                    "center" -> Center(child)
                    "pre" -> Pre(child)
                    "code" -> Code(child)
                    "h1", "h2", "h3", "h4", "h5", "h6" -> Heading(child)
                    "a" -> Link(child)
                    "ol" -> OrderedList(child)
                    "ul" -> DisorderedList(child)
                    "hr" -> Divider()
                    "table" -> Table(child)
                    else -> Text(text = "unknown tag ${child.tagName()}")
                }
            } else {
                Text(text = child.toString())
            }
        }
    }

    @Composable
    private fun Table(self: Element) {
        val headers =
            self.childNodes().filterIsInstance<Element>().find { node -> node.tagName() == "thead" }
        val body =
            self.childNodes().filterIsInstance<Element>().find { node -> node.tagName() == "tbody" }
        StaggeredVerticalGrid(
            Modifier
                .fillMaxWidth()
                .border(1.dp, colors.primary),
            maxRows = headers?.childNodeSize() ?: body?.childNodes()
                ?.mapNotNull { it.childNodeSize() }?.max() ?: return
        ) {
            //表头
            headers?.getElementsByTag("th")?.forEach { head ->
                Box(
                    modifier =
                    Modifier
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    HandleText(self = head)
                }
            }
            body?.getElementsByTag("td")?.forEach { cell ->
                Box(
                    modifier =
                    Modifier
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    HandleText(self = cell)
                }
            }

        }
    }

    @Composable
    private fun DisorderedList(self: Element) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            self.childNodes().forEach { child ->
                if (child is Element && child.tagName() == "li") {
                    ListItem(self = child, prefix = "• ")
                } else {
                    if (child.toString().isNotBlank()) Text(text = "• $child")
                }
            }
        }
    }

    @Composable
    private fun OrderedList(self: Element) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            var index = 0
            self.childNodes().forEach { child ->
                if (child is Element && child.tagName() == "li") {
                    ListItem(self = child, prefix = "${++index}. ")
                } else {
                    if (child.toString().isNotBlank()) Text(text = "${++index}. $child")
                }
            }
        }
    }


    @Composable
    private fun ListItem(self: Element, prefix: String) {
        Column {
            HandleText(self, prefix)
        }
    }

    @Composable
    private fun Paragraph(self: Element) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            HandleText(self = self)
        }
    }

    @Composable
    private fun TipBlock(self: Element) {
        val type = (self.attr("type") ?: "tip").lowercase()
        val title = (self.attr("title") ?: type).run {
            if (isNotBlank()) {
                first().uppercase() + filterIndexed { index, _ -> index != 0 }
            } else "Tip"
        }
        val color = when (type) {
            "info" -> colors.info
            "warn" -> colors.warn
            "error" -> colors.error
            "success" -> colors.success
            else -> themeColor
        }
        Column(
            Modifier
                .border(1.dp, color, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.1f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            ProvideTextStyle(value = LocalTextStyle.current.copy(color = color)) {
                Text(text = title, fontWeight = FontWeight.W700)
                HandleChildren(self = self)
            }
        }
    }

    @Composable
    private fun ExtBlock(self: Element) {
        val type = (self.attr("type") ?: "tip").lowercase()
        val params = self.attr("params") ?: return
        when (type) {
            "music" -> {
                if (params.contains("#")) {
                    val (id, typ) = params.split("#")
//                    MusicCard(type = typ, id = id)
                }
            }
        }
    }

    @Composable
    private fun Blockquote(self: Element) {
        Column(
            Modifier
                .background(themeColor.copy(alpha = 0.1f))
                .drawWithContent {
                    drawRect(themeColor, size = Size(4.dp2px, this.size.height))
                    drawContent()
                }
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()) {
            ProvideTextStyle(value = LocalTextStyle.current.copy(color = themeColor)) {
                HandleChildren(self = self)
            }
        }
    }

    @OptIn(ExperimentalTextApi::class)
    @Composable
    private fun HandleText(self: Element, prefix: String = "") {
        var builder = AnnotatedString.Builder().also {
            it.append(prefix)
        }
        val map = hashMapOf<String, InlineTextContent>()

        @Composable
        fun cutAndNew() {
            val text = builder.toAnnotatedString()
            builder = AnnotatedString.Builder()
            if (text.isNotBlank()) AnnotatedText(text, map)
            map.clear()
        }

        fun inlineCode(code: String) {
            val tag = "code:$code"
            println("$tag,len:${code.length}")
            map[tag] = InlineTextContent(
                Placeholder(
                    width = (code.length / 2 + 1).em,//(code.length * 16.sp2px).px2sp,
                    height = 22.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = code,
                        color = Color.DarkGray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
            builder.appendInlineContent(tag, code)
        }
        self.childNodes().filter { it.toString().isNotBlank() }.forEach { child ->
            Log.i("HandleText", "$self \n=========\n$child\n=========")
            if (child is Element) {
                when (child.tagName()) {
                    "p" -> {
                        cutAndNew()
                        Paragraph(child)
                    }

                    "img" -> {
                        cutAndNew()
                        CacheImage(
                            src = child.attr("src"),
                            contentDescription = child.attr("alt"),
                            style = Style.Large,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
                                .defaultMinSize(minHeight = 32.dp)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.FillWidth,
                        )
                    }

                    "ext" -> {
                        cutAndNew()
                        ExtBlock(child)
                    }

                    "center" -> {
                        cutAndNew()
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            HandleText(self = child, prefix = prefix)
                        }
                    }

                    "h1", "h2", "h3", "h4", "h5", "h6" -> {
                        cutAndNew()
                        Heading(child)
                    }

                    "ol" -> {
                        cutAndNew()
                        OrderedList(child)
                    }

                    "ul" -> {
                        cutAndNew()
                        DisorderedList(child)
                    }


                    "a" -> builder.run {
                        pushUrlAnnotation(UrlAnnotation(child.attr("href")))
                        withStyle(SpanStyle(color = purple)) {
                            append(child.text())
                        }
                        pop()
                    }

                    "br" -> {
                        builder.append("\n")
                    }

                    "strong" -> builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        HandleText(self = child, prefix = prefix)
                    }

                    "em" -> builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        HandleText(self = child, prefix = prefix)
                    }

                    "del" -> builder.withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                        HandleText(self = child, prefix = prefix)
                    }

                    "code" -> inlineCode(child.text())

                    else -> builder.append(child.outerHtml().trim())
                }
            } else if (child is TextNode) {
                builder.append(text = child.text().trim())
            }
        }
        cutAndNew()
    }

    @OptIn(ExperimentalTextApi::class)
    @Composable
    fun AnnotatedText(
        annotatedString: AnnotatedString, map: HashMap<String, InlineTextContent>
    ) {
        val uriHandler = LocalUriHandler.current
        val style = LocalTextStyle.current

        ClickableText(
            text = annotatedString,
            modifier = Modifier.padding(vertical = Gap.Small),
            inlineContent = map,
            style = style
        ) { offset ->
            // We check if there is an *URL* annotation attached to the text
            // at the clicked position
            annotatedString.getUrlAnnotations(
                start = offset, end = offset
            ).firstOrNull()?.let { annotation ->
                // If yes, we log its value
                uriHandler.openUri(annotation.item.url)
            }
        }
    }

    @Composable
    private fun Center(self: Element) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HandleChildren(self)
        }
    }

    @OptIn(ExperimentalTextApi::class)
    @Composable
    private fun Link(self: Element) {
        val href = self.attr("href")
        val text = self.text()
        val annotation = buildAnnotatedString {
            pushUrlAnnotation(UrlAnnotation(href))
            withStyle(SpanStyle(color = purple)) {
                append(text)
            }
            pop()
        }
        val uriHandler = LocalUriHandler.current
        val style = LocalTextStyle.current
        ClickableText(
            text = annotation, modifier = Modifier, style = style
        ) { offset ->
            // We check if there is an *URL* annotation attached to the text
            // at the clicked position
            annotation.getUrlAnnotations(
                start = offset, end = offset
            ).firstOrNull()?.let { annotation ->
                // If yes, we log its value
                uriHandler.openUri(annotation.item.url)
            }
        }
    }

    @Composable
    private fun Heading(self: Element) {
//        val style = when (self.tagName()) {
//            "h1" -> h1
//            "h2" -> h2
//            "h3" -> h3
//            "h4" -> h4
//            "h5" -> h5
//            "h6" -> h6
//            else -> h6
//        }
        val style = AppTypography.body
        ProvideTextStyle(value = style) {
            HandleChildren(self)
        }
    }

    @Composable
    private fun Pre(self: Element) {
        val style = LocalTextStyle.current
        ProvideTextStyle(
            value = style.copy(
                color = Color.DarkGray,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontFamily = FontFamily.Monospace
            )
        ) {
            HandleChildren(self)
        }
    }

    @Composable
    private fun Code(self: Element) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .background(Color.DarkGray)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = self.text(),
                fontFamily = FontFamily.Monospace,
                color = Color.LightGray,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

/*
HTML 目前包含 <p>, <img>, <center>,<pre>,<code> 标签
<someTag>
 Text()
</someTag>

 */