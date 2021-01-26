package com.khairo.escposprinter.barcode

import com.khairo.escposprinter.EscPosPrinterCommands
import com.khairo.escposprinter.EscPosPrinterSize
import com.khairo.escposprinter.exceptions.EscPosBarcodeException

class BarcodeUPCE(
    printerSize: EscPosPrinterSize,
    code: String,
    widthMM: Float,
    heightMM: Float,
    textPosition: Int
) : Barcode(
    printerSize,
    EscPosPrinterCommands.BARCODE_TYPE_UPCE,
    code,
    widthMM,
    heightMM,
    textPosition
) {
    override fun getCodeLength(): Int = 6

    override fun getColsCount(): Int = this.getCodeLength() * 7 + 16

    @Throws(EscPosBarcodeException::class)
    private fun checkCode() {
        val codeLength = this.getCodeLength()
        if (code.length < codeLength) {
            throw EscPosBarcodeException("Code is too short for the barcode type.")
        }
        try {
            code = code.substring(0, codeLength)
            for (i in 0 until codeLength) {
                code.substring(i, i + 1).toInt(10)
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            throw EscPosBarcodeException("Invalid barcode number")
        }
    }

    init {
        checkCode()
    }
}