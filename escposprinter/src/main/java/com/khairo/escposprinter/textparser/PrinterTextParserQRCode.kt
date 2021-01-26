package com.khairo.escposprinter.textparser


import com.khairo.escposprinter.EscPosPrinterCommands
import com.khairo.escposprinter.exceptions.EscPosBarcodeException
import com.khairo.escposprinter.exceptions.EscPosParserException
import java.util.*

class PrinterTextParserQRCode(
    printerTextParserColumn: PrinterTextParserColumn, textAlign: String?,
    qrCodeAttributes: Hashtable<String, String>, data: String
) : PrinterTextParserImg(
    printerTextParserColumn,
    textAlign,
    initConstructor(printerTextParserColumn, qrCodeAttributes, data)
) {
    companion object {
        @Throws(EscPosParserException::class, EscPosBarcodeException::class)
        private fun initConstructor(
            printerTextParserColumn: PrinterTextParserColumn,
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
