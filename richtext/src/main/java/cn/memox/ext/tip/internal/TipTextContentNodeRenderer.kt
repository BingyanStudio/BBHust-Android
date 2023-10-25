package com.bingyan.bbhust.ext.tip.internal

import org.commonmark.node.Node
import org.commonmark.renderer.text.TextContentNodeRendererContext

class TipTextContentNodeRenderer(private val context: TextContentNodeRendererContext) :
    TipNodeRenderer() {
    override fun render(node: Node) {
        renderChildren(node)
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