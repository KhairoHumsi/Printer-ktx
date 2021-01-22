package com.dantsu.async

import android.app.AlertDialog
import android.content.Context
import com.dantsu.escposprinter.EscPosCharsetEncoding
import com.dantsu.escposprinter.connection.tcp.TcpDeviceConnection
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import java.lang.ref.WeakReference

class CoroutinesEscPosPrint(context: Context?) {
    private var weakContext: WeakReference<Context> = WeakReference(context)

    suspend fun execute(vararg printersData: CoroutinesEscPosPrinter) {
        if (printersData.isEmpty())
            return onPostExecute(FINISH_NO_PRINTER)

        val printerData = printersData[0]
        try {
            var deviceConnection: TcpDeviceConnection? = printerData.printerConnection
//            if (deviceConnection == null)
//                deviceConnection = BluetoothPrintersConnections.selectFirstPaired()

            if (deviceConnection == null) return onPostExecute(FINISH_NO_PRINTER)

            val printer = com.dantsu.escposprinter.CoroutinesEscPosPrinter(
                deviceConnection,
                printerData.printerDpi,
                printerData.printerWidthMM,
                printerData.printerNbrCharactersPerLine,
                EscPosCharsetEncoding("Arabic", 22)
            )

            printer.printFormattedTextAndCut(printerData.textToPrint).apply { disconnectPrinter() }

        } catch (e: EscPosConnectionException) {
            e.printStackTrace()
            return onPostExecute(FINISH_PRINTER_DISCONNECTED)
        } catch (e: EscPosParserException) {
            e.printStackTrace()
            return onPostExecute(FINISH_PARSER_ERROR)
        } catch (e: EscPosEncodingException) {
            e.printStackTrace()
            return onPostExecute(FINISH_ENCODING_ERROR)
        } catch (e: EscPosBarcodeException) {
            e.printStackTrace()
            return onPostExecute(FINISH_BARCODE_ERROR)
        }
        return onPostExecute(FINISH_SUCCESS)
    }

    private suspend fun onPostExecute(result: Int) {
        val context = weakContext.get() ?: return
        when (result) {
            FINISH_SUCCESS ->
                AlertDialog.Builder(context)
                    .setTitle("Success")
                    .setMessage("Congratulation ! The text is printed !")
                    .show()
            FINISH_NO_PRINTER -> AlertDialog.Builder(context)
                .setTitle("No printer")
                .setMessage("The application can't find any printer connected.")
                .show()
            FINISH_PRINTER_DISCONNECTED -> AlertDialog.Builder(context)
                .setTitle("Broken connection")
                .setMessage("Unable to connect the printer.")
                .show()
            FINISH_PARSER_ERROR -> AlertDialog.Builder(context)
                .setTitle("Invalid formatted text")
                .setMessage("It seems to be an invalid syntax problem.")
                .show()
            FINISH_ENCODING_ERROR -> AlertDialog.Builder(context)
                .setTitle("Bad selected encoding")
                .setMessage("The selected encoding character returning an error.")
                .show()
            FINISH_BARCODE_ERROR -> AlertDialog.Builder(context)
                .setTitle("Invalid barcode")
                .setMessage("Data send to be converted to barcode or QR code seems to be invalid.")
                .show()
        }
    }

    companion object {
        const val FINISH_SUCCESS = 1
        const val FINISH_NO_PRINTER = 2
        const val FINISH_PRINTER_DISCONNECTED = 3
        const val FINISH_PARSER_ERROR = 4
        const val FINISH_ENCODING_ERROR = 5
        const val FINISH_BARCODE_ERROR = 6
    }

}