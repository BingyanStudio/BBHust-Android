package com.bingyan.bbhust.ext.ext

import com.bingyan.bbhust.ext.ext.internal.ExtDelimiterProcessor
import com.bingyan.bbhust.ext.ext.internal.ExtHtmlNodeRenderer
import com.bingyan.bbhust.ext.ext.internal.ExtTextContentNodeRenderer
import org.commonmark.Extension
import org.commonmark.parser.Parser
import org.commonmark.parser.Parser.ParserExtension
import org.commonmark.renderer.html.HtmlRenderer
import org.commonmark.renderer.html.HtmlRenderer.HtmlRendererExtension
import org.commonmark.renderer.text.TextContentRenderer
import org.commonmark.renderer.text.TextContentRenderer.TextContentRendererExtension

/**
 * Extension for ins using :::
 *
 *
 * Create it with [.create] and then configure it on the builders
 * ([org.commonmark.parser.Parser.Builder.extensions],
 * [HtmlRenderer.Builder.extensions]).
 *
 *
 *
 * The parsed ins text regions are turned into [Ext] nodes.
 *
 */
class ExtExtension private constructor() : ParserExtension, HtmlRendererExtension,
    TextContentRendererExtension {
    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customDelimiterProcessor(ExtDelimiterProcessor())
    }

    override fun extend(rendererBuilder: HtmlRenderer.Builder) {
        rendererBuilder.nodeRendererFactory { context -> ExtHtmlNodeRenderer(context) }
    }

    override fun extend(rendererBuilder: TextContentRenderer.Builder) {
        rendererBuilder.nodeRendererFactory { context -> ExtTextContentNodeRenderer(context) }
    }

    companion object {
        fun create(): Extension {
            return ExtExtension()
        }
    }
}