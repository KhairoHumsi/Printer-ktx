package com.dantsu.escposprinter.textparser

import com.dantsu.escposprinter.EscPosPrinterCommands
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import java.util.*

class CoroutinesPrinterTextParserQRCode(
    printerTextParserColumn: CoroutinesPrinterTextParserColumn, textAlign: String?,
    qrCodeAttributes: Hashtable<String, String>, data: String
) : CoroutinesPrinterTextParserImg(
    printerTextParserColumn,
    textAlign,
    initConstructor(printerTextParserColumn, qrCodeAttributes, data)
) {
    companion object {
        @Throws(EscPosParserException::class, EscPosBarcodeException::class)
        private fun initConstructor(
            printerTextParserColumn: CoroutinesPrinterTextParserColumn,
            qrCodeAttributes: Hashtable<String, String>,
            oldData: String
        ): ByteArray {
            var data = oldData
            val printer = printerTextParserColumn.line.textParser.printer
            data = data.trim { it <= ' ' }
            var size = printer.mmToPx(20f)
            if (qrCodeAttributes.containsKey(PrinterTextParser.ATTR_QRCODE_SIZE)) {
                val qrCodeAttribute = qrCodeAttributes[PrinterTextParser.ATTR_QRCODE_SIZE]
                    ?: throw EscPosParserException("Invalid QR code attribute : " + PrinterTextParser.ATTR_QRCODE_SIZE)
                size = try {
                    printer.mmToPx(qrCodeAttribute.toFloat())
                } catch (nfe: NumberFormatException) {
                    throw EscPosParserException("Invalid QR code " + PrinterTextParser.ATTR_QRCODE_SIZE + " value")
                }
            }
            return EscPosPrinterCommands.QRCodeDataToBytes(data, size)
        }
    }
}
