package com.khairo.escposprinter.textparser

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.khairo.escposprinter.CoroutinesEscPosPrinterCommands
import com.khairo.escposprinter.EscPosPrinterCommands
import com.khairo.escposprinter.EscPosPrinterSize
import com.khairo.escposprinter.exceptions.EscPosEncodingException
import kotlin.experimental.and

open class CoroutinesPrinterTextParserImg(
    printerTextParserColumn: CoroutinesPrinterTextParserColumn,
    textAlign: String?,
    image: ByteArray
) : CoroutinesIPrinterTextParserElement {
    private val length: Int
    private val image: ByteArray

    /**
     * Create new instance of PrinterTextParserImg.
     *
     * @param printerTextParserColumn Parent PrinterTextParserColumn instance.
     * @param textAlign               Set the image alignment. Use PrinterTextParser.TAGS_ALIGN_... constants.
     * @param hexadecimalString       Hexadecimal string of the image data.
     */
    constructor(
        printerTextParserColumn: CoroutinesPrinterTextParserColumn,
        textAlign: String?,
        hexadecimalString: String
    ) : this(printerTextParserColumn, textAlign, hexadecimalStringToBytes(hexadecimalString)) {
    }

    /**
     * Get the image width in char length.
     *
     * @return int
     */
    @Throws(EscPosEncodingException::class)
    override fun length(): Int {
        return length
    }


    /**
     * Print image
     *
     * @param printerSocket Instance of EscPosPrinterCommands
     * @return this Fluent method
     */
    override suspend fun print(printerSocket: CoroutinesEscPosPrinterCommands?): CoroutinesIPrinterTextParserElement? {
        printerSocket!!.printImage(image)
        return this
    }

    companion object {
        /**
         * Convert Drawable instance to a hexadecimal string of the image data.
         *
         * @param printerSize A EscPosPrinterSize instance that will print the image.
         * @param drawable    Drawable instance to be converted.
         * @return A hexadecimal string of the image data. Empty string if Drawable cannot be cast to BitmapDrawable.
         */
        fun bitmapToHexadecimalString(printerSize: EscPosPrinterSize, drawable: Drawable?): String {
            return if (drawable is BitmapDrawable) {
                bitmapToHexadecimalString(
                    printerSize,
                    drawable
                )
            } else ""
        }

        /**
         * Convert BitmapDrawable instance to a hexadecimal string of the image data.
         *
         * @param printerSize    A EscPosPrinterSize instance that will print the image.
         * @param bitmapDrawable BitmapDrawable instance to be converted.
         * @return A hexadecimal string of the image data.
         */
        fun bitmapToHexadecimalString(
            printerSize: EscPosPrinterSize,
            bitmapDrawable: BitmapDrawable
        ): String {
            return bitmapToHexadecimalString(printerSize, bitmapDrawable.bitmap)
        }

        /**
         * Convert Bitmap instance to a hexadecimal string of the image data.
         *
         * @param printerSize A EscPosPrinterSize instance that will print the image.
         * @param bitmap      Bitmap instance to be converted.
         * @return A hexadecimal string of the image data.
         */
        fun bitmapToHexadecimalString(printerSize: EscPosPrinterSize, bitmap: Bitmap?): String {
            return bytesToHexadecimalString(
                printerSize.bitmapToBytes(
                    bitmap!!
                )
            )
        }

        /**
         * Convert byte array to a hexadecimal string of the image data.
         *
         * @param bytes Bytes contain the image in ESC/POS command.
         * @return A hexadecimal string of the image data.
         */
        fun bytesToHexadecimalString(bytes: ByteArray): String {
            val imageHexString = StringBuilder()
            for (aByte in bytes) {
                var hexString = Integer.toHexString((aByte and 0xFF.toByte()).toInt())
                if (hexString.length == 1) {
                    hexString = "0$hexString"
                }
                imageHexString.append(hexString)
            }
            return imageHexString.toString()
        }

        /**
         * Convert hexadecimal string of the image data to bytes ESC/POS command.
         *
         * @param hexString Hexadecimal string of the image data.
         * @return Bytes contain the image in ESC/POS command.
         */
        @Throws(NumberFormatException::class)
        fun hexadecimalStringToBytes(hexString: String): ByteArray {
            val bytes = ByteArray(hexString.length / 2)
            for (i in bytes.indices) {
                val pos = i * 2
                bytes[i] = hexString.substring(pos, pos + 2).toInt(16).toByte()
            }
            return bytes
        }
    }

    /**
     * Create new instance of PrinterTextParserImg.
     *
     * @param printerTextParserColumn Parent PrinterTextParserColumn instance.
     * @param textAlign               Set the image alignment. Use PrinterTextParser.TAGS_ALIGN_... constants.
     * @param image                   Bytes contain the image in ESC/POS command.
     */
    init {
        var image = image
        val printer = printerTextParserColumn.line.textParser.printer
        val byteWidth = (image[4].toInt() and 0xFF) + (image[5].toInt() and 0xFF) * 256
        val width = byteWidth * 8
        val height = (image[6].toInt() and 0xFF) + (image[7].toInt() and 0xFF) * 256
        val nbrByteDiff = Math.floor(((printer.printerWidthPx - width).toFloat() / 8f).toDouble())
            .toInt()
        var nbrWhiteByteToInsert = 0
        when (textAlign) {
            PrinterTextParser.TAGS_ALIGN_CENTER -> nbrWhiteByteToInsert = Math.round(
                nbrByteDiff.toFloat() / 2f
            )
            PrinterTextParser.TAGS_ALIGN_RIGHT -> nbrWhiteByteToInsert = nbrByteDiff
        }
        if (nbrWhiteByteToInsert > 0) {
            val newByteWidth = byteWidth + nbrWhiteByteToInsert
            val newImage = EscPosPrinterCommands.initImageCommand(newByteWidth, height)
            for (i in 0 until height) {
                System.arraycopy(
                    image,
                    byteWidth * i + 8,
                    newImage,
                    newByteWidth * i + nbrWhiteByteToInsert + 8,
                    byteWidth
                )
            }
            image = newImage
        }
        length =
            Math.ceil((byteWidth.toFloat() * 8 / printer.printerCharSizeWidthPx.toFloat()).toDouble())
                .toInt()
        this.image = image
    }
}