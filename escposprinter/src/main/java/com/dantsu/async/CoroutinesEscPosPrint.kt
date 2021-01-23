package com.dantsu.async

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.dantsu.escposprinter.EscPosCharsetEncoding
import com.dantsu.escposprinter.connection.tcp.TcpDeviceConnection
import com.dantsu.exeption.PrintingException.FINISH_NO_PRINTER
import com.dantsu.exeption.onException
import java.lang.ref.WeakReference

class CoroutinesEscPosPrint(
    private val context: Context,
    private val coroutineScope: LifecycleCoroutineScope
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
        val printer = com.dantsu.escposprinter.CoroutinesEscPosPrinter(
            context,
            deviceConnection,
            printerData.printerDpi,
            printerData.printerWidthMM,
            printerData.printerNbrCharactersPerLine,
            EscPosCharsetEncoding("Arabic", 22),
            coroutineScope
        )

        Log.d("dsgsdzfgdfgd", "11111: ${deviceConnection.isConnected()}")
        printer.printFormattedTextAndCut(context, printerData.textToPrint)
            .apply { disconnectPrinter() }
    }
}
