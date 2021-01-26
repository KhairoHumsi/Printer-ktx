package com.khairo.escposprinter.barcode

import com.khairo.escposprinter.EscPosPrinterSize
import com.khairo.escposprinter.exceptions.EscPosBarcodeException

abstract class BarcodeNumber(
    printerSize: EscPosPrinterSize,
    barcodeType: Int,
    code: String,
    widthMM: Float,
    heightMM: Float,
    textPosition: Int
) : Barcode(printerSize, barcodeType, code, widthMM, heightMM, textPosition) {
    override fun getColsCount(): Int {
        return this.getCodeLength() * 7 + 11
    }

    @Throws(EscPosBarcodeException::class)
    private fun checkCode() {
        val codeLength = this.getCodeLength() - 1
        if (code.length < codeLength) {
            throw EscPosBarcodeException("Code is too short for the barcode type.")
        }
        try {
            val code = code.substring(0, codeLength)
            var totalBarcodeKey = 0
            for (i in 0 until codeLength) {
                val pos = codeLength - 1 - i
                var intCode = code.substring(pos, pos + 1).toInt(10)
                if (i % 2 == 0) {
                    intCode = 3 * intCode
                }
                totalBarcodeKey += intCode
            }
            var barcodeKey = (10 - totalBarcodeKey % 10).toString()
            if (barcodeKey.length == 2) {
                barcodeKey = "0"
            }
            this.code = code + barcodeKey
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            throw EscPosBarcodeException("Invalid barcode number")
        }
    }

    init {
        checkCode()
    }
}