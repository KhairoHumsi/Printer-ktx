package com.khairo.escposprinter.barcode

import com.khairo.escposprinter.EscPosPrinterCommands
import com.khairo.escposprinter.EscPosPrinterSize

class BarcodeEAN13(
    printerSize: EscPosPrinterSize,
    code: String,
    widthMM: Float,
    heightMM: Float,
    textPosition: Int
) : BarcodeNumber(
    printerSize,
    EscPosPrinterCommands.BARCODE_TYPE_EAN13,
    code,
    widthMM,
    heightMM,
    textPosition
) {
    override fun getCodeLength(): Int {
        return 13
    }
}