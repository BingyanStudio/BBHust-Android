package com.bingyan.bbhust.ext.tip.internal

import com.bingyan.bbhust.ext.tip.Tip
import org.commonmark.node.Node
import org.commonmark.renderer.NodeRenderer

abstract class TipNodeRenderer : NodeRenderer {
    override fun getNodeTypes(): Set<Class<out Node>> {
        return setOf<Class<out Node>>(Tip::class.java)
    }
}