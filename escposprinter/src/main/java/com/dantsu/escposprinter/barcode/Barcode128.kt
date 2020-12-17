package com.dantsu.escposprinter.barcode

import com.dantsu.escposprinter.EscPosPrinterCommands
import com.dantsu.escposprinter.EscPosPrinterSize

class Barcode128(
    printerSize: EscPosPrinterSize,
    code: String,
    widthMM: Float,
    heightMM: Float,
    textPosition: Int
) : Barcode(
    printerSize,
    EscPosPrinterCommands.BARCODE_TYPE_128,
    code,
    widthMM,
    heightMM,
    textPosition
) {

    override fun getCodeLength(): Int {
        return code.length
    }

    override fun getColsCount(): Int {
        return (this.getCodeLength() + 5) * 11
    }
}