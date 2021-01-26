package com.khairo.exeption

import android.app.AlertDialog
import android.content.Context
import com.khairo.exeption.PrintingException.FINISH_BARCODE_ERROR
import com.khairo.exeption.PrintingException.FINISH_ENCODING_ERROR
import com.khairo.exeption.PrintingException.FINISH_NO_PRINTER
import com.khairo.exeption.PrintingException.FINISH_PARSER_ERROR
import com.khairo.exeption.PrintingException.FINISH_PRINTER_DISCONNECTED
import com.khairo.exeption.PrintingException.FINISH_SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference

//try {
//    var deviceConnection: TcpDeviceConnection? = printerData.printerConnection
////            if (deviceConnection == null)
////                deviceConnection = BluetoothPrintersConnections.selectFirstPaired()
//
//    if (deviceConnection == null) return onPostExecute(CoroutinesEscPosPrint.FINISH_NO_PRINTER)
//
//    val context = weakContext.get() ?: return
//    val printer = com.khairo.escposprinter.CoroutinesEscPosPrinter(
//        context,
//        deviceConnection,
//        printerData.printerDpi,
//        printerData.printerWidthMM,
//        printerData.printerNbrCharactersPerLine,
//        EscPosCharsetEncoding("Arabic", 22)
//    )
//
//    printer.printFormattedTextAndCut(printerData.textToPrint).apply { disconnectPrinter() }
//
//} catch (e: EscPosConnectionException) {
//    e.printStackTrace()
//    return onPostExecute(CoroutinesEscPosPrint.FINISH_PRINTER_DISCONNECTED)
//} catch (e: EscPosParserException) {
//    e.printStackTrace()
//    return onPostExecute(CoroutinesEscPosPrint.FINISH_PARSER_ERROR)
//} catch (e: EscPosEncodingException) {
//    e.printStackTrace()
//    return onPostExecute(CoroutinesEscPosPrint.FINISH_ENCODING_ERROR)
//} catch (e: EscPosBarcodeException) {
//    e.printStackTrace()
//    return onPostExecute(CoroutinesEscPosPrint.FINISH_BARCODE_ERROR)
//}
//return onPostExecute(FINISH_SUCCESS)

fun onException(context: Context, result: Int) {
    val weakContext: WeakReference<Context> = WeakReference(context)

    val newContext = weakContext.get() ?: return
    runBlocking(Dispatchers.Main) {
        when (result) {
            FINISH_SUCCESS -> AlertDialog.Builder(newContext)
                .setTitle("Success")
                .setMessage("Congratulation ! The text is printed !")
                .show()
            FINISH_NO_PRINTER -> AlertDialog.Builder(newContext)
                .setTitle("No printer")
                .setMessage("The application can't find any printer connected.")
                .show()
            FINISH_PRINTER_DISCONNECTED -> AlertDialog.Builder(newContext)
                .setTitle("Broken connection")
                .setMessage("Unable to connect the printer.")
                .show()
            FINISH_PARSER_ERROR -> AlertDialog.Builder(newContext)
                .setTitle("Invalid formatted text")
                .setMessage("It seems to be an invalid syntax problem.")
                .show()
            FINISH_ENCODING_ERROR -> AlertDialog.Builder(newContext)
                .setTitle("Bad selected encoding")
                .setMessage("The selected encoding character returning an error.")
                .show()
            FINISH_BARCODE_ERROR -> AlertDialog.Builder(newContext)
                .setTitle("Invalid barcode")
                .setMessage("Data send to be converted to barcode or QR code seems to be invalid.")
                .show()
            else -> AlertDialog.Builder(newContext)
                .setTitle("Unknown error")
                .setMessage("Unknown error.")
                .show()
        }
    }
}

object PrintingException {

    const val FINISH_SUCCESS = 1
    const val FINISH_NO_PRINTER = 2
    const val FINISH_PRINTER_DISCONNECTED = 3
    const val FINISH_PARSER_ERROR = 4
    const val FINISH_ENCODING_ERROR = 5
    const val FINISH_BARCODE_ERROR = 6
}