package com.bingyan.bbhust.ext.ext.internal

import com.bingyan.bbhust.ext.ext.Ext
import org.commonmark.node.Node
import org.commonmark.renderer.html.HtmlNodeRendererContext
import org.commonmark.renderer.html.HtmlWriter

class ExtHtmlNodeRenderer(private val context: HtmlNodeRendererContext) : ExtNodeRenderer() {
    private val html: HtmlWriter = context.writer

    override fun render(node: Node) {
        if (node is Ext) {
            val attributes =
                context.extendAttributes(
                    node,
                    "ext",
                    mapOf("type" to node.type, "params" to node.params)
                )
            html.tag("ext", attributes)
            html.tag("/ext")
        }
    }
}