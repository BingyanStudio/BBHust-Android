package com.bingyan.bbhust.utils

import com.bingyan.bbhust.ext.ext.ExtExtension
import com.bingyan.bbhust.ext.tip.TipExtension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer


object MarkdownEngine {
    fun parse(string: String): String? {
        val extensions = listOf(
            TablesExtension.create(),
            AutolinkExtension.create(),
            TaskListItemsExtension.create(),
            StrikethroughExtension.create(),
            TipExtension.create(),
            ExtExtension.create()
        )

        val parser: Parser = Parser.builder().extensions(extensions).build()
        val document: Node = parser.parse(string)
        val renderer: HtmlRenderer = HtmlRenderer.builder().extensions(extensions).build()
        return renderer.render(document) // "<p>This is <em>Sparta</em></p>\n"
    }
}