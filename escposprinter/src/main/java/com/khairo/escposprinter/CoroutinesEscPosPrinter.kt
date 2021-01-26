package com.khairo.escposprinter

import android.content.Context
import com.khairo.escposprinter.connection.tcp.TcpDeviceConnection
import com.khairo.escposprinter.exceptions.EscPosBarcodeException
import com.khairo.escposprinter.exceptions.EscPosEncodingException
import com.khairo.escposprinter.exceptions.EscPosParserException
import com.khairo.escposprinter.textparser.*
import com.khairo.exeption.PrintingException.FINISH_NO_PRINTER
import com.khairo.exeption.onException

class CoroutinesEscPosPrinter(
    var printer: CoroutinesEscPosPrinterCommands?,
    printerDpi: Int,
    printerWidthMM: Float,
    printerNbrCharactersPerLine: Int
) : EscPosPrinterSize(printerDpi, printerWidthMM, printerNbrCharactersPerLine) {

    /**
     * Create new instance of EscPosPrinter.
     *
     * @param printerConnection           Instance of class which implement DeviceConnection
     * @param printerDpi                  DPI of the connected printer
     * @param printerWidthMM              Printing width in millimeters
     * @param printerNbrCharactersPerLine The maximum number of characters that can be printed on a line.
     */
    constructor(
        printerConnection: TcpDeviceConnection?,
        printerDpi: Int,
        printerWidthMM: Float,
        printerNbrCharactersPerLine: Int
    ) : this(
        printerConnection?.let { CoroutinesEscPosPrinterCommands(it) },
        printerDpi,
        printerWidthMM,
        printerNbrCharactersPerLine
    )

    /**
     * Create new instance of EscPosPrinter.
     *
     * @param printerConnection           Instance of class which implement DeviceConnection
     * @param printerDpi                  DPI of the connected printer
     * @param printerWidthMM              Printing width in millimeters
     * @param printerNbrCharactersPerLine The maximum number of characters that can be printed on a line.
     * @param charsetEncoding             Set the charset encoding.
     */
    constructor(
        printerConnection: TcpDeviceConnection?,
        printerDpi: Int,
        printerWidthMM: Float,
        printerNbrCharactersPerLine: Int,
        charsetEncoding: EscPosCharsetEncoding?
    ) : this(
        printerConnection?.let { CoroutinesEscPosPrinterCommands(it, charsetEncoding) },
        printerDpi,
        printerWidthMM,
        printerNbrCharactersPerLine
    )

    /**
     * Close the connection with the printer.
     *
     * @return Fluent interface
     */
    suspend fun disconnectPrinter(): CoroutinesEscPosPrinter {
        if (printer != null) {
            printer!!.disconnect()
            printer = null
        }
        return this
    }
    /**
     * Print a formatted text. Read the README.md for more information about text formatting options.
     *
     * @param text        Formatted text to be printed.
     * @param mmFeedPaper millimeter distance feed paper at the end.
     * @return Fluent interface
     */
    /**
     * Print a formatted text. Read the README.md for more information about text formatting options.
     *
     * @param text Formatted text to be printed.
     * @return Fluent interface
     */
    @JvmOverloads
    @Throws(
        EscPosParserException::class,
        EscPosEncodingException::class,
        EscPosBarcodeException::class
    )
    suspend fun printFormattedText(
        context: Context,
        text: String?,
        mmFeedPaper: Float = 20f
    ): CoroutinesEscPosPrinter {
        return this.printFormattedText(context, text, mmToPx(mmFeedPaper))
    }

    /**
     * Print a formatted text. Read the README.md for more information about text formatting options.
     *
     * @param text          Formatted text to be printed.
     * @param dotsFeedPaper distance feed paper at the end.
     * @return Fluent interface
     */
    @Throws(
        EscPosParserException::class,
        EscPosEncodingException::class,
        EscPosBarcodeException::class
    )
    suspend fun printFormattedText(
        context: Context,
        text: String?,
        dotsFeedPaper: Int
    ): CoroutinesEscPosPrinter {
        if (printer == null || printerNbrCharactersPerLine == 0) {
            return this
        }

        val textParser = CoroutinesPrinterTextParser(this)
        val linesParsed = textParser
            .setFormattedText(text!!)
            .parse()
        printer!!.reset()
        for (line in linesParsed) {
            val columns = line!!.columns
            var lastElement: CoroutinesIPrinterTextParserElement? = null
            for (column in columns) {
                val elements = column!!.elements
                for (element in elements) {
                    element!!.print(printer)
                    lastElement = element
                }
            }
            if (lastElement is CoroutinesPrinterTextParserString) {
                printer!!.newLine(context)
            }
        }
        printer!!.feedPaper(context, dotsFeedPaper)
        return this
    }
    /**
     * Print a formatted text and cut the paper. Read the README.md for more information about text formatting options.
     *
     * @param text        Formatted text to be printed.
     * @param mmFeedPaper millimeter distance feed paper at the end.
     * @return Fluent interface
     */
    /**
     * Print a formatted text and cut the paper. Read the README.md for more information about text formatting options.
     *
     * @param text Formatted text to be printed.
     * @return Fluent interface
     */
    @JvmOverloads
    @Throws(
        EscPosParserException::class,
        EscPosEncodingException::class,
        EscPosBarcodeException::class
    )
    suspend fun printFormattedTextAndCut(
        context: Context,
        text: String?,
        mmFeedPaper: Float = 20f
    ): CoroutinesEscPosPrinter {
        return this.printFormattedTextAndCut(context, text, mmToPx(mmFeedPaper))
    }

    /**
     * Print a formatted text and cut the paper. Read the README.md for more information about text formatting options.
     *
     * @param text          Formatted text to be printed.
     * @param dotsFeedPaper distance feed paper at the end.
     * @return Fluent interface
     */
    @Throws(
        EscPosParserException::class,
        EscPosEncodingException::class,
        EscPosBarcodeException::class
    )
    suspend fun printFormattedTextAndCut(
        context: Context,
        text: String?,
        dotsFeedPaper: Int
    ): CoroutinesEscPosPrinter {
        if (printer == null || printerNbrCharactersPerLine == 0) {
            onException(context, FINISH_NO_PRINTER)
            return this
        }

        this.printFormattedText(context, text, dotsFeedPaper)
        printer!!.cutPaper(context)
        return this
    }

    /**
     * @return Charset encoding
     */
    val encoding: EscPosCharsetEncoding
        get() = printer!!.getCharsetEncoding()!!

}
