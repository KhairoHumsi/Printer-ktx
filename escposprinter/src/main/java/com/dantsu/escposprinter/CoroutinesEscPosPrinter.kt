package com.dantsu.escposprinter

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.dantsu.escposprinter.connection.tcp.TcpDeviceConnection
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import com.dantsu.escposprinter.textparser.*
import com.dantsu.exeption.PrintingException.FINISH_NO_PRINTER
import com.dantsu.exeption.onException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoroutinesEscPosPrinter(
    context: Context,
    printer: CoroutinesEscPosPrinterCommands?,
    printerDpi: Int,
    printerWidthMM: Float,
    printerNbrCharactersPerLine: Int,
    coroutineScope: LifecycleCoroutineScope
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
        context: Context,
        printerConnection: TcpDeviceConnection?,
        printerDpi: Int,
        printerWidthMM: Float,
        printerNbrCharactersPerLine: Int,
        coroutineScope: LifecycleCoroutineScope
    ) : this(
        context,
        printerConnection?.let { CoroutinesEscPosPrinterCommands(it) },
        printerDpi,
        printerWidthMM,
        printerNbrCharactersPerLine,
        coroutineScope
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
        context: Context,
        printerConnection: TcpDeviceConnection?,
        printerDpi: Int,
        printerWidthMM: Float,
        printerNbrCharactersPerLine: Int,
        charsetEncoding: EscPosCharsetEncoding?,
        coroutineScope: LifecycleCoroutineScope
    ) : this(
        context,
        printerConnection?.let { CoroutinesEscPosPrinterCommands(it, charsetEncoding) },
        printerDpi,
        printerWidthMM,
        printerNbrCharactersPerLine,
        coroutineScope
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
        Log.d("dsgsdzfgdfgd", "333333333333: ${printer!!.isConnected()}")
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

    init {
        coroutineScope.launch(Dispatchers.IO) {
            if (this@CoroutinesEscPosPrinter.printer == null)
                if (printer != null) {
                    Log.d("dsgsdzfgdfgd", "3546564564: $")
                    this@CoroutinesEscPosPrinter.printer = printer.connect(context)
                } else Log.d(
                    "dsgsdzfgdfgd",
                    "3546564564: asdada ${this@CoroutinesEscPosPrinter.printer}"
                )
        }
    }
}