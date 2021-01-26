package com.khairo.escposprinter.barcode

import com.khairo.escposprinter.EscPosPrinterCommands
import com.khairo.escposprinter.EscPosPrinterCommands.*
import com.khairo.escposprinter.EscPosPrinterSize

class Barcode128(
    printerSize: EscPosPrinterSize,
    code: String,
    widthMM: Float,
    heightMM: Float,
    textPosition: Int
) : Barcode(
    printerSize,
    BARCODE_TYPE_128,
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