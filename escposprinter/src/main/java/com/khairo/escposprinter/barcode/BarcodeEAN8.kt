package com.khairo.escposprinter.barcode

import com.khairo.escposprinter.EscPosPrinterCommands
import com.khairo.escposprinter.EscPosPrinterSize

class BarcodeEAN8(
    printerSize: EscPosPrinterSize,
    code: String,
    widthMM: Float,
    heightMM: Float,
    textPosition: Int
) : BarcodeNumber(
    printerSize,
    EscPosPrinterCommands.BARCODE_TYPE_EAN8,
    code,
    widthMM,
    heightMM,
    textPosition
) {
    override fun getCodeLength(): Int = 8
}