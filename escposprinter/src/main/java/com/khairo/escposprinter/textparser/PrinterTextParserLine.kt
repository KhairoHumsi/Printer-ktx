package com.khairo.escposprinter.textparser

import java.util.*
import java.util.regex.Pattern
import kotlin.math.floor

class PrinterTextParserLine(val textParser: PrinterTextParser, textLine: String) {
    val nbrColumns: Int
    var nbrCharColumn: Int
        private set
    var nbrCharForgetted: Int
        private set
    var nbrCharColumnExceeded: Int
        private set
    val columns: Array<PrinterTextParserColumn?>
    fun setNbrCharColumn(newValue: Int): PrinterTextParserLine {
        nbrCharColumn = newValue
        return this
    }

    fun setNbrCharForgetted(newValue: Int): PrinterTextParserLine {
        nbrCharForgetted = newValue
        return this
    }

    fun setNbrCharColumnExceeded(newValue: Int): PrinterTextParserLine {
        nbrCharColumnExceeded = newValue
        return this
    }

    init {
        val nbrCharactersPerLine = textParser.printer.printerNbrCharactersPerLine
        val pattern = Pattern.compile(PrinterTextParser.regexAlignTags)
        val matcher = pattern.matcher(textLine)
        val columnsList = ArrayList<String>()
        var lastPosition = 0
        while (matcher.find()) {
            val startPosition = matcher.start()
            if (startPosition > 0) {
                columnsList.add(textLine.substring(lastPosition, startPosition))
            }
            lastPosition = startPosition
        }
        columnsList.add(textLine.substring(lastPosition))
        nbrColumns = columnsList.size
        nbrCharColumn =
            floor((nbrCharactersPerLine.toFloat() / nbrColumns.toFloat()).toDouble())
                .toInt()
        nbrCharForgetted = nbrCharactersPerLine - nbrCharColumn * nbrColumns
        nbrCharColumnExceeded = 0
        columns = arrayOfNulls(nbrColumns)
        var i = 0
        for (column in columnsList) {
            columns[i++] = PrinterTextParserColumn(this, column)
        }
    }
}
