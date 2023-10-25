package com.bingyan.bbhust.ext.ext

import org.commonmark.node.CustomNode
import org.commonmark.node.Delimited


/**
 * An ins node containing text and other inline nodes as children.
 */

// @Type:Params
data class Ext(
    var type: String? = null,
    var params: String? = null
) : CustomNode(), Delimited {
    override fun getOpeningDelimiter() = "@"

    override fun getClosingDelimiter() = " "
}