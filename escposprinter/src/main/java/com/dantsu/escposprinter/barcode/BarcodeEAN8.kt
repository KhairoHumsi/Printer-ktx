package com.dantsu.escposprinter.barcode

import com.dantsu.escposprinter.EscPosPrinterCommands
import com.dantsu.escposprinter.EscPosPrinterSize

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