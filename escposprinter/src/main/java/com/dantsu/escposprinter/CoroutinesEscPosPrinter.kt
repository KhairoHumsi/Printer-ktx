package com.dantsu.escposprinter

import com.dantsu.escposprinter.connection.tcp.TcpDeviceConnection
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import com.dantsu.escposprinter.textparser.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CoroutinesEscPosPrinter(
    printer: CoroutinesEscPosPrinterCommands?,
    printerDpi: Int,
    printerWidthMM: Float,
    printerNbrCharactersPerLine: Int
) : EscPosPrinterSize(printerDpi, printerWidthMM, printerNbrCharactersPerLine) {
    private var printer: CoroutinesEscPosPrinterCommands? = null

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
    ) {
    }

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
    ) {
    }

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
        EscPosConnectionException::class,
        EscPosParserException::class,
        EscPosEncodingException::class,
        EscPosBarcodeException::class
    )
    suspend fun printFormattedText(text: String?, mmFeedPaper: Float = 20f): CoroutinesEscPosPrinter {
        return this.printFormattedText(text, mmToPx(mmFeedPaper))
    }

    /**
     * Print a formatted text. Read the README.md for more information about text formatting options.
     *
     * @param text          Formatted text to be printed.
     * @param dotsFeedPaper distance feed paper at the end.
     * @return Fluent interface
     */
    @Throws(
        EscPosConnectionException::class,
        EscPosParserException::class,
        EscPosEncodingException::class,
        EscPosBarcodeException::class
    )
    suspend fun printFormattedText(text: String?, dotsFeedPaper: Int): CoroutinesEscPosPrinter {
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
                printer!!.newLine()
            }
        }
        printer!!.feedPaper(dotsFeedPaper)
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
        EscPosConnectionException::class,
        EscPosParserException::class,
        EscPosEncodingException::class,
        EscPosBarcodeException::class
    )
    suspend fun printFormattedTextAndCut(text: String?, mmFeedPaper: Float = 20f): CoroutinesEscPosPrinter {
        return this.printFormattedTextAndCut(text, mmToPx(mmFeedPaper))
    }

    /**
     * Print a formatted text and cut the paper. Read the README.md for more information about text formatting options.
     *
     * @param text          Formatted text to be printed.
     * @param dotsFeedPaper distance feed paper at the end.
     * @return Fluent interface
     */
    @Throws(
        EscPosConnectionException::class,
        EscPosParserException::class,
        EscPosEncodingException::class,
        EscPosBarcodeException::class
    )
    suspend fun printFormattedTextAndCut(text: String?, dotsFeedPaper: Int): CoroutinesEscPosPrinter {
        if (printer == null || printerNbrCharactersPerLine == 0) {
            return this
        }
        this.printFormattedText(text, dotsFeedPaper)
        printer!!.cutPaper()
        return this
    }

    /**
     * @return Charset encoding
     */
    val encoding: EscPosCharsetEncoding
        get() = printer!!.getCharsetEncoding()!!

    /**
     * Create new instance of EscPosPrinter.
     *
     * @param printer                     Instance of CoroutinesEscPosPrinterCommands
     * @param printerDpi                  DPI of the connected printer
     * @param printerWidthMM              Printing width in millimeters
     * @param printerNbrCharactersPerLine The maximum number of characters that can be printed on a line.
     */
    init {
        GlobalScope.launch {
            if (printer != null) {
                this@CoroutinesEscPosPrinter.printer = printer.connect()
            }
        }
    }
}
