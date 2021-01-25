package com.khairo.printer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import com.dantsu.async.CoroutinesEscPosPrinter
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.khairo.printer.R
import java.text.SimpleDateFormat
import java.util.*

fun String.replaceNonstandardDigits(): String {
    if (this.isEmpty()) {
        return this
    }
    val builder = StringBuilder()
    for (element in this) {
        if (element.isNonstandardDigit()) {
            val numericValue = Character.getNumericValue(element)
            if (numericValue >= 0) {
                builder.append(numericValue)
            }
        } else {
            builder.append(element)
        }
    }
    return builder.toString()
}

fun Char.isNonstandardDigit(): Boolean {
    return Character.isDigit(this) && this !in '0'..'9'
}

@SuppressLint("SimpleDateFormat")
fun String.getDateTime(): String = SimpleDateFormat(this).format(Date()).replaceNonstandardDigits()


@SuppressLint("UseCompatLoadingForDrawables")
fun Context.printViaWifi(
    printer: CoroutinesEscPosPrinter,
    orderId: Int,
    body: String,
    totalBill: Float,
    tax: Int,
    customer: String = "",
    barcode: String
): CoroutinesEscPosPrinter {

    var test =
        "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
            printer,
            getDrawable(R.drawable.logo)
        ) + "</img>\n" +
                "[L]\n" +
                "[C]<u><font size='big'>ORDER N°$orderId</font></u>\n" +
                "[L]\n" +
                "[C]<u type='double'>${"'on' yyyy-MM-dd 'at' HH:mm:ss".getDateTime()}</u>\n" +
                "[C]================================\n" +
                "[L]\n" +
                "[L]    <b>Items</b>[R][R]<b>Qty</b>[R][R]<b>Price</b>\n" +
                "[L][R]\n" +
                "$body\n" +
                "[L][R]\n" +
                "[C]--------------------------------\n" +
                "[R] TOTAL :[R]${totalBill} $\n"

    test += if (tax != 0) "[R] TAX :[R]${tax} %\n" + "[R] GRAND TOTAL :[R]${totalBill * (tax / 100f) + totalBill} $\n" else ""

    test += "[L][R]\n" +
            "$customer\n" +
            "[C]<barcode type='128' height='10'>$barcode</barcode>\n" +
            "[L]\n" +
            "[C]<u><font size='big'>VISIT HIS SITE</font></u>\n" +
            "[L]\n" +
            "[L]\n" +
            "[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>\n" +
            "[L]\n" +
            "[L]\n" +
            "[L]\n" +
            "[L]\n" +
            "[L]\n"

    return printer.setTextToPrint(test)
}
