package com.khairo.escposprinter.textparser

import com.khairo.escposprinter.EscPosPrinterCommands
import com.khairo.escposprinter.barcode.*
import com.khairo.escposprinter.exceptions.EscPosEncodingException
import com.khairo.escposprinter.exceptions.EscPosParserException
import java.util.*

class PrinterTextParserBarcode(
    printerTextParserColumn: PrinterTextParserColumn,
    textAlign: String?,
    barcodeAttributes: Hashtable<String, String>,
    var code: String
) : IPrinterTextParserElement {
    private var barcode: Barcode? = null
    private val length: Int
    private var align: ByteArray

    /**
     * Get the barcode width in char length.
     *
     * @return int
     */
    @Throws(EscPosEncodingException::class)
    override fun length(): Int {
        return length
    }

    /**
     * Print barcode
     *
     * @param printerSocket Instance of EscPosPrinterCommands
     * @return this Fluent method
     */
    override fun print(printerSocket: EscPosPrinterCommands?): PrinterTextParserBarcode {
        printerSocket!!.setAlign(align).printBarcode(barcode!!)
        return this
    }

    init {
        val printer = printerTextParserColumn.line.textParser.printer
        code = code.trim { it <= ' ' }
        align = EscPosPrinterCommands.TEXT_ALIGN_LEFT
        when (textAlign) {
            PrinterTextParser.TAGS_ALIGN_CENTER -> align = EscPosPrinterCommands.TEXT_ALIGN_CENTER
            PrinterTextParser.TAGS_ALIGN_RIGHT -> align = EscPosPrinterCommands.TEXT_ALIGN_RIGHT
        }
        length = printer.printerNbrCharactersPerLine
        var height = 10f
        if (barcodeAttributes.containsKey(PrinterTextParser.ATTR_BARCODE_HEIGHT)) {
            val barCodeAttribute = barcodeAttributes[PrinterTextParser.ATTR_BARCODE_HEIGHT]
                ?: throw EscPosParserException("Invalid barcode attribute: " + PrinterTextParser.ATTR_BARCODE_HEIGHT)
            height = try {
                barCodeAttribute.toFloat()
            } catch (nfe: NumberFormatException) {
                throw EscPosParserException("Invalid barcode " + PrinterTextParser.ATTR_BARCODE_HEIGHT + " value")
            }
        }
        var width = 0f
        if (barcodeAttributes.containsKey(PrinterTextParser.ATTR_BARCODE_WIDTH)) {
            val barCodeAttribute = barcodeAttributes[PrinterTextParser.ATTR_BARCODE_WIDTH]
                ?: throw EscPosParserException("Invalid barcode attribute: " + PrinterTextParser.ATTR_BARCODE_WIDTH)
            width = try {
                barCodeAttribute.toFloat()
            } catch (nfe: NumberFormatException) {
                throw EscPosParserException("Invalid barcode " + PrinterTextParser.ATTR_BARCODE_WIDTH + " value")
            }
        }
        var textPosition = EscPosPrinterCommands.BARCODE_TEXT_POSITION_BELOW
        if (barcodeAttributes.containsKey(PrinterTextParser.ATTR_BARCODE_TEXT_POSITION)) {
            val barCodeAttribute = barcodeAttributes[PrinterTextParser.ATTR_BARCODE_TEXT_POSITION]
                ?: throw EscPosParserException("Invalid barcode attribute: " + PrinterTextParser.ATTR_BARCODE_TEXT_POSITION)
            when (barCodeAttribute) {
                PrinterTextParser.ATTR_BARCODE_TEXT_POSITION_NONE -> textPosition =
                    EscPosPrinterCommands.BARCODE_TEXT_POSITION_NONE
                PrinterTextParser.ATTR_BARCODE_TEXT_POSITION_ABOVE -> textPosition =
                    EscPosPrinterCommands.BARCODE_TEXT_POSITION_ABOVE
            }
        }
        var barcodeType: String? = PrinterTextParser.ATTR_BARCODE_TYPE_EAN13
        if (barcodeAttributes.containsKey(PrinterTextParser.ATTR_BARCODE_TYPE)) {
            barcodeType = barcodeAttributes[PrinterTextParser.ATTR_BARCODE_TYPE]
            if (barcodeType == null) {
                throw EscPosParserException("Invalid barcode attribute : " + PrinterTextParser.ATTR_BARCODE_TYPE)
            }
        }
        barcode = when (barcodeType) {
            PrinterTextParser.ATTR_BARCODE_TYPE_EAN8 -> BarcodeEAN8(
                printer,
                code,
                width,
                height,
                textPosition
            )
            PrinterTextParser.ATTR_BARCODE_TYPE_EAN13 -> BarcodeEAN13(
                printer,
                code,
                width,
                height,
                textPosition
            )
            PrinterTextParser.ATTR_BARCODE_TYPE_UPCA -> BarcodeUPCA(
                printer,
                code,
                width,
                height,
                textPosition
            )
            PrinterTextParser.ATTR_BARCODE_TYPE_UPCE -> BarcodeUPCE(
                printer,
                code,
                width,
                height,
                textPosition
            )
            PrinterTextParser.ATTR_BARCODE_TYPE_128 -> Barcode128(
                printer,
                code,
                width,
                height,
                textPosition
            )
            else -> throw EscPosParserException("Invalid barcode attribute : " + PrinterTextParser.ATTR_BARCODE_TYPE)
        }
    }
}
