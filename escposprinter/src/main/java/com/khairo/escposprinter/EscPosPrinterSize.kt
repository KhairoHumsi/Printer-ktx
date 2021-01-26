package com.khairo.escposprinter

import android.graphics.Bitmap
import kotlin.math.roundToInt

open class EscPosPrinterSize constructor(
    /**
     * Get the printer DPI
     *
     * @return int
     */
    var printerDpi: Int,
    /**
     * Get the printing width in millimeters
     *
     * @return float
     */
    var printerWidthMM: Float,
    /**
     * Get the maximum number of characters that can be printed on a line.
     *
     * @return int
     */
    var printerNbrCharactersPerLine: Int
) {

    /**
     * Get the printing width in dot
     *
     * @return int
     */
    var printerWidthPx: Int
        protected set

    /**
     * Get the number of dot that a printed character contain
     *
     * @return int
     */
    var printerCharSizeWidthPx: Int
        protected set

    /**
     * Convert from millimeters to dot the mmSize variable.
     *
     * @param mmSize Distance in millimeters to be converted
     * @return int
     */
    fun mmToPx(mmSize: Float): Int = (mmSize * printerDpi.toFloat() / INCH_TO_MM).roundToInt()

    /**
     * Convert Bitmap object to ESC/POS image.
     *
     * @param oldBitmap Instance of Bitmap
     * @return Bytes contain the image in ESC/POS command
     */
    fun bitmapToBytes(oldBitmap: Bitmap): ByteArray {
        var bitmap = oldBitmap
        var isSizeEdit = false
        var bitmapWidth = bitmap.width
        var bitmapHeight = bitmap.height
        val maxWidth = printerWidthPx
        val maxHeight = 256
        if (bitmapWidth > maxWidth) {
            bitmapHeight =
                (bitmapHeight.toFloat() * maxWidth.toFloat() / bitmapWidth.toFloat()).roundToInt()
            bitmapWidth = maxWidth
            isSizeEdit = true
        }
        if (bitmapHeight > maxHeight) {
            bitmapWidth =
                (bitmapWidth.toFloat() * maxHeight.toFloat() / bitmapHeight.toFloat()).roundToInt()
            bitmapHeight = maxHeight
            isSizeEdit = true
        }
        if (isSizeEdit)
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, false)

        return EscPosPrinterCommands.bitmapToBytes(bitmap)
    }

    companion object {
        const val INCH_TO_MM = 25.4f
    }

    init {
        val printingWidthPx = mmToPx(printerWidthMM)
        printerWidthPx = printingWidthPx + printingWidthPx % 8
        printerCharSizeWidthPx = printingWidthPx / printerNbrCharactersPerLine
    }
}