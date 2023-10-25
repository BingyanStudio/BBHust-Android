package com.bingyan.bbhust.ext.ext.internal

import com.bingyan.bbhust.ext.ext.Ext
import org.commonmark.node.Node
import org.commonmark.node.SourceSpans
import org.commonmark.node.Text
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun


class ExtDelimiterProcessor : DelimiterProcessor {
    override fun getOpeningCharacter(): Char {
        return '@'
    }

    override fun getClosingCharacter(): Char {
        return '@'
    }

    override fun getMinLength(): Int {
        return 1
    }

    override fun process(openingRun: DelimiterRun, closingRun: DelimiterRun): Int {
        return if (openingRun.length() >= 1 && closingRun.length() >= 1) {
            // Use exactly two delimiters even if we have more, and don't care about internal openers/closers.
            val opener: Text = openingRun.opener

            // Wrap nodes between delimiters in ins.
            val literal = opener.next
            if (literal is Text && literal.literal.contains(":")) {
                val typ = literal.literal.substringBefore(":")
                val params = literal.literal.substringAfter(":")
                val ext: Node = Ext(type = typ, params = params)
                val sourceSpans = SourceSpans()
                sourceSpans.addAllFrom(openingRun.getOpeners(1))
                sourceSpans.addAllFrom(closingRun.getClosers(1))
                ext.sourceSpans = sourceSpans.sourceSpans
                opener.insertAfter(ext)
                literal.unlink()
                opener.unlink()
                return 1
            }
            0
        } else {
            0
        }
    }
}