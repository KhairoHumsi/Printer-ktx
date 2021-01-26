package com.khairo.async

import android.content.Context
import com.khairo.escposprinter.connection.bluetooth.BluetoothPrintersConnections

class AsyncBluetoothEscPosPrint(context: Context?) : AsyncEscPosPrint(context) {
    override fun doInBackground(vararg printersData: AsyncEscPosPrinter): Int {
        if (printersData.isEmpty()) return FINISH_NO_PRINTER

        var printerData: AsyncEscPosPrinter = printersData[0]

        printerData = AsyncEscPosPrinter(
            BluetoothPrintersConnections.selectFirstPaired()!!,
            printerData.printerDpi,
            printerData.printerWidthMM,
            printerData.printerNbrCharactersPerLine
        )
        printerData.textToPrint = printerData.textToPrint
        return super.doInBackground(*printersData)
    }
}