package com.dantsu.escposprinter.textparser

import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.EscPosPrinterCommands
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import java.io.UnsupportedEncodingException
import java.util.*

class PrinterTextParserString(
    printerTextParserColumn: PrinterTextParserColumn,
    var text: String,
    var textSize: ByteArray,
    var textColor: ByteArray,
    private var textReverseColor: ByteArray,
    private var textBold: ByteArray,
    private var textUnderline: ByteArray,
    private var textDoubleStrike: ByteArray
) : IPrinterTextParserElement {
    private val printer: EscPosPrinter = printerTextParserColumn.line.textParser.printer

    @Throws(EscPosEncodingException::class)
    override fun length(): Int {
        val charsetEncoding = printer.encoding
        val coef = if (Arrays.equals(
                textSize,
                EscPosPrinterCommands.TEXT_SIZE_DOUBLE_WIDTH
            ) || Arrays.equals(
                textSize, EscPosPrinterCommands.TEXT_SIZE_BIG
            )
        ) 2 else 1
        return if (charsetEncoding != null) {
            try {
                text.toByteArray(charset(charsetEncoding.name)).size * coef
            } catch (e: UnsupportedEncodingException) {
                throw EscPosEncodingException(e.message)
            }
        } else text.length * coef
    }

    /**
     * Print text
     *
     * @param printerSocket Instance of EscPosPrinterCommands
     * @return this Fluent method
     */
    @Throws(EscPosEncodingException::class)
    override fun print(printerSocket: EscPosPrinterCommands?): PrinterTextParserString {
        printerSocket!!.printText(
            text,
            textSize,
            textColor,
            textReverseColor,
            textBold,
            textUnderline,
            textDoubleStrike
        )
        return this
    }

}
