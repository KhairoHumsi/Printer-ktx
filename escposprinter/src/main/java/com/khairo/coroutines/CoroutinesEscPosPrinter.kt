package com.khairo.coroutines

import com.khairo.escposprinter.EscPosPrinterSize
import com.khairo.escposprinter.connection.tcp.TcpDeviceConnection

class CoroutinesEscPosPrinter(
    val printerConnection: TcpDeviceConnection,
    printerDpi: Int,
    printerWidthMM: Float,
    printerNbrCharactersPerLine: Int
) : EscPosPrinterSize(printerDpi, printerWidthMM, printerNbrCharactersPerLine) {
    var textToPrint = ""

    fun setTextToPrint(textToPrint: String): CoroutinesEscPosPrinter {
        this.textToPrint = textToPrint
        return this
    }
}
