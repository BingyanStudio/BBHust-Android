package com.bingyan.bbhust.ext.tip

import org.commonmark.node.CustomBlock

/**
 * An ins node containing text and other inline nodes as children.
 */
data class Tip(
    var type: String? = null,
    var title: String? = null
) : CustomBlock()