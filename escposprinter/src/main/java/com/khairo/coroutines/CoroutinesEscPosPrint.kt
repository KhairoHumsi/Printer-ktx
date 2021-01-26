package com.khairo.coroutines

import android.content.Context
import com.khairo.escposprinter.EscPosCharsetEncoding
import com.khairo.escposprinter.connection.tcp.TcpDeviceConnection
import com.khairo.exeption.PrintingException.FINISH_NO_PRINTER
import com.khairo.exeption.onException
import java.lang.ref.WeakReference

class CoroutinesEscPosPrint(
    private val context: Context
) {
    private var weakContext: WeakReference<Context> = WeakReference(context)

    suspend fun execute(vararg printersData: CoroutinesEscPosPrinter) {
        if (printersData.isEmpty())
            return onException(context, FINISH_NO_PRINTER)

        val printerData = printersData[0]

        var deviceConnection: TcpDeviceConnection? = printerData.printerConnection
//            if (deviceConnection == null)
//                deviceConnection = BluetoothPrintersConnections.selectFirstPaired()

        if (deviceConnection == null) return onException(context, FINISH_NO_PRINTER)

        val context = weakContext.get() ?: return
        val printer = com.khairo.escposprinter.CoroutinesEscPosPrinter(
            deviceConnection,
            printerData.printerDpi,
            printerData.printerWidthMM,
            printerData.printerNbrCharactersPerLine,
            EscPosCharsetEncoding("Arabic", 22)
        )

        printer.printFormattedTextAndCut(context, printerData.textToPrint)
            .apply { disconnectPrinter() }
    }
}
