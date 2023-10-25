package com.bingyan.bbhust.ext.tip.internal

import android.util.Log
import com.bingyan.bbhust.ext.tip.Tip
import org.commonmark.internal.DocumentBlockParser
import org.commonmark.node.Block
import org.commonmark.parser.block.AbstractBlockParser
import org.commonmark.parser.block.AbstractBlockParserFactory
import org.commonmark.parser.block.BlockContinue
import org.commonmark.parser.block.BlockStart
import org.commonmark.parser.block.MatchedBlockParser
import org.commonmark.parser.block.ParserState


class TipBlockProcessor(private val firstLine: CharSequence) : AbstractBlockParser() {
    private val REGEX_END = "^:{3}(\\s.*)?".toRegex()
    private val block: Tip

    init {
        val typeRaw = firstLine.toString().trim(':').trim()
        val type = typeRaw.substringBefore(" ").trim()
        val title = typeRaw.substringAfter(" ").trim()
        block = Tip(type = type, title = title)
    }

    override fun getBlock(): Block {
        return block
    }

    override fun isContainer(): Boolean = true
    override fun canContain(childBlock: Block?) = true

    override fun tryContinue(parserState: ParserState?): BlockContinue? {
        val line = parserState!!.line.content
        Log.i("tryContinue", "$line,index:${parserState.index},col:${parserState.column}")
        if (REGEX_END.matches(line)) {
            return BlockContinue.finished()
        }
        return BlockContinue.atIndex(parserState.index)
    }

    class Factory : AbstractBlockParserFactory() {
        private val REGEX_BEGIN = "^:{3}(\\s.*)?".toRegex()

        override fun tryStart(
            state: ParserState,
            matchedBlockParser: MatchedBlockParser
        ): BlockStart? {
            val line = state.line.content
            val parentParser = matchedBlockParser.matchedBlockParser
            // check whether this line is the first line of whole document or not
            return if (parentParser is DocumentBlockParser &&
                REGEX_BEGIN.matches(line)
            ) {
                BlockStart.of(TipBlockProcessor(line)).atIndex(line.length + 1)
            } else BlockStart.none()
        }
    }

}