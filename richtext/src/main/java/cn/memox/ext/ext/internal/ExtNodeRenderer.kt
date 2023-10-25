package com.bingyan.bbhust.ext.ext.internal

import com.bingyan.bbhust.ext.ext.Ext
import org.commonmark.node.Node
import org.commonmark.renderer.NodeRenderer

abstract class ExtNodeRenderer : NodeRenderer {
    override fun getNodeTypes(): Set<Class<out Node>> {
        return setOf<Class<out Node>>(Ext::class.java)
    }
}