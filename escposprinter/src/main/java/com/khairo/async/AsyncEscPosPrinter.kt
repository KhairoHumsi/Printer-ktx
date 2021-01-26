package com.khairo.async

import com.khairo.escposprinter.EscPosPrinterSize
import com.khairo.escposprinter.connection.DeviceConnection

class AsyncEscPosPrinter(val printerConnection: DeviceConnection, printerDpi: Int, printerWidthMM: Float, printerNbrCharactersPerLine: Int) : EscPosPrinterSize(printerDpi, printerWidthMM, printerNbrCharactersPerLine) {
    var textToPrint = ""

    fun setTextToPrint(textToPrint: String): AsyncEscPosPrinter {
        this.textToPrint = textToPrint
        return this
    }
}
