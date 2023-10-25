package com.bingyan.bbhust.ext.tip.internal

import com.bingyan.bbhust.ext.tip.Tip
import org.commonmark.node.Node
import org.commonmark.renderer.html.HtmlNodeRendererContext
import org.commonmark.renderer.html.HtmlWriter

class TipHtmlNodeRenderer(private val context: HtmlNodeRendererContext) : TipNodeRenderer() {
    private val html: HtmlWriter = context.writer

    override fun render(node: Node) {
        if (node is Tip) {
            val attributes =
                context.extendAttributes(
                    node,
                    "tip",
                    mapOf("type" to node.type, "title" to node.title)
                )
            html.tag("tip", attributes)
            renderChildren(node)
            html.tag("/tip")
        }
    }

    private fun renderChildren(parent: Node) {
        var node = parent.firstChild
        while (node != null) {
            val next = node.next
            context.render(node)
            node = next
        }
    }
}