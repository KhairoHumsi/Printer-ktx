package com.khairo.escposprinter

import android.content.Context
import android.graphics.Bitmap
import com.khairo.escposprinter.barcode.Barcode
import com.khairo.escposprinter.connection.tcp.TcpDeviceConnection
import com.khairo.escposprinter.exceptions.EscPosBarcodeException
import com.khairo.escposprinter.exceptions.EscPosEncodingException
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.encoder.ByteMatrix
import com.google.zxing.qrcode.encoder.Encoder
import java.io.UnsupportedEncodingException
import java.util.*

class CoroutinesEscPosPrinterCommands @JvmOverloads constructor(
    private val printerConnection: TcpDeviceConnection,
    charsetEncoding: EscPosCharsetEncoding? = null
) {
    /**
     * @return Charset encoding
     */
    private val charsetEncoding: EscPosCharsetEncoding =
        charsetEncoding ?: EscPosCharsetEncoding("windows-1252", 6)

    /**
     * Start socket connection and open stream with the device.
     */
    suspend fun connect(context: Context): CoroutinesEscPosPrinterCommands {
        printerConnection.connect(context)
        return this
    }

    /**
     * Close the socket connection and stream with the device.
     */
    suspend fun disconnect() {
        printerConnection.disconnect()
    }

    suspend fun isConnected() =
        printerConnection.isConnected()

    /**
     * Reset printers parameters.
     */
    suspend fun reset() {
        printerConnection.write(RESET_PRINTER)
    }

    /**
     * Set the alignment of text and barcodes.
     * Don't works with image.
     *
     * @param align Set the alignment of text and barcodes. Use EscPosPrinterCommands.TEXT_ALIGN_... constants
     * @return Fluent interface
     */
    suspend fun setAlign(align: ByteArray?): CoroutinesEscPosPrinterCommands {
        if (!printerConnection.isConnected()) {
            return this
        }
        printerConnection.write(align!!)
        return this
    }
    /**
     * Print text with the connected printer.
     *
     * @param text             Text to be printed
     * @param textSize         Set the text size. Use EscPosPrinterCommands.TEXT_SIZE_... constants
     * @param textColor        Set the text color. Use EscPosPrinterCommands.TEXT_COLOR_... constants
     * @param textReverseColor Set the background and text color. Use EscPosPrinterCommands.TEXT_COLOR_REVERSE_... constants
     * @param textBold         Set the text weight. Use EscPosPrinterCommands.TEXT_WEIGHT_... constants
     * @param textUnderline    Set the underlining of the text. Use EscPosPrinterCommands.TEXT_UNDERLINE_... constants
     * @param textDoubleStrike Set the double strike of the text. Use EscPosPrinterCommands.TEXT_DOUBLE_STRIKE_... constants
     * @return Fluent interface
     */
    /**
     * Print text with the connected printer.
     *
     * @param text          Text to be printed
     * @param textSize      Set the text size. Use EscPosPrinterCommands.TEXT_SIZE_... constants
     * @param textColor        Set the text color. Use EscPosPrinterCommands.TEXT_COLOR_... constants
     * @param textReverseColor Set the background and text color. Use EscPosPrinterCommands.TEXT_COLOR_REVERSE_... constants
     * @param textBold      Set the text weight. Use EscPosPrinterCommands.TEXT_WEIGHT_... constants
     * @param textUnderline Set the underlining of the text. Use EscPosPrinterCommands.TEXT_UNDERLINE_... constants
     * @return Fluent interface
     */
    /**
     * Print text with the connected printer.
     *
     * @param text     Text to be printed
     * @param textSize Set the text size. Use EscPosPrinterCommands.TEXT_SIZE_... constants
     * @param textColor        Set the text color. Use EscPosPrinterCommands.TEXT_COLOR_... constants
     * @param textReverseColor Set the background and text color. Use EscPosPrinterCommands.TEXT_COLOR_REVERSE_... constants
     * @param textBold Set the text weight. Use EscPosPrinterCommands.TEXT_WEIGHT_... constants
     * @return Fluent interface
     */
    /**
     * Print text with the connected printer.
     *
     * @param text     Text to be printed
     * @param textSize Set the text size. Use EscPosPrinterCommands.TEXT_SIZE_... constants
     * @param textColor        Set the text color. Use EscPosPrinterCommands.TEXT_COLOR_... constants
     * @param textReverseColor Set the background and text color. Use EscPosPrinterCommands.TEXT_COLOR_REVERSE_... constants
     * @return Fluent interface
     */
    /**
     * Print text with the connected printer.
     *
     * @param text     Text to be printed
     * @param textSize Set the text size. Use EscPosPrinterCommands.TEXT_SIZE_... constants
     * @param textColor        Set the text color. Use EscPosPrinterCommands.TEXT_COLOR_... constants
     * @return Fluent interface
     */
    /**
     * Print text with the connected printer.
     *
     * @param text     Text to be printed
     * @param textSize Set the text size. Use EscPosPrinterCommands.TEXT_SIZE_... constants
     * @return Fluent interface
     */
    /**
     * Print text with the connected printer.
     *
     * @param text Text to be printed
     * @return Fluent interface
     */
    @JvmOverloads
    @Throws(EscPosEncodingException::class)
    suspend fun printText(
        text: String,
        textSize: ByteArray? = null,
        textColor: ByteArray? = null,
        textReverseColor: ByteArray? = null,
        textBold: ByteArray? = null,
        textUnderline: ByteArray? = null,
        textDoubleStrike: ByteArray? = null
    ): CoroutinesEscPosPrinterCommands {
        if (!printerConnection.isConnected()) {
            return this
        }
        try {
            val textBytes = text.toByteArray(charset(charsetEncoding.name))
            printerConnection.write(charsetEncoding.command)
            //this.printerConnection.write(EscPosPrinterCommands.TEXT_FONT_A);
            if (textSize != null) {
                printerConnection.write(textSize)
            } else {
                printerConnection.write(TEXT_SIZE_NORMAL)
            }
            if (textDoubleStrike != null) {
                printerConnection.write(textDoubleStrike)
            } else {
                printerConnection.write(TEXT_DOUBLE_STRIKE_OFF)
            }
            if (textUnderline != null) {
                printerConnection.write(textUnderline)
            } else {
                printerConnection.write(TEXT_UNDERLINE_OFF)
            }
            if (textBold != null) {
                printerConnection.write(textBold)
            } else {
                printerConnection.write(TEXT_WEIGHT_NORMAL)
            }
            if (textColor != null) {
                printerConnection.write(textColor)
            } else {
                printerConnection.write(TEXT_COLOR_BLACK)
            }
            if (textReverseColor != null) {
                printerConnection.write(textReverseColor)
            } else {
                printerConnection.write(TEXT_COLOR_REVERSE_OFF)
            }
            printerConnection.write(textBytes)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            throw EscPosEncodingException(e.message)
        }
        return this
    }

    /**
     * Print image with the connected printer.
     *
     * @param image Bytes contain the image in ESC/POS command
     * @return Fluent interface
     */
    suspend fun printImage(image: ByteArray?): CoroutinesEscPosPrinterCommands {
        if (!printerConnection.isConnected()) {
            return this
        }
        printerConnection.write(image!!)
        return this
    }

    /**
     * Print a barcode with the connected printer.
     *
     * @param barcode Instance of Class that implement Barcode
     * @return Fluent interface
     */
    suspend fun printBarcode(barcode: Barcode): CoroutinesEscPosPrinterCommands {
        if (!printerConnection.isConnected()) {
            return this
        }
        val code = barcode.code
        val barcodeLength = barcode.getCodeLength()
        val barcodeCommand = ByteArray(barcodeLength + 4)
        System.arraycopy(
            byteArrayOf(
                0x1D,
                0x6B,
                barcode.barcodeType.toByte(),
                barcodeLength.toByte()
            ), 0, barcodeCommand, 0, 4
        )
        for (i in 0 until barcodeLength) {
            barcodeCommand[i + 4] = code[i].toByte()
        }
        printerConnection.write(byteArrayOf(0x1D, 0x48, barcode.textPosition.toByte()))
        printerConnection.write(byteArrayOf(0x1D, 0x77, barcode.colWidth.toByte()))
        printerConnection.write(byteArrayOf(0x1D, 0x68, barcode.height.toByte()))
        printerConnection.write(barcodeCommand)
        return this
    }

    /**
     * Print a QR code with the connected printer.
     *
     * @param qrCodeType Set the barcode type. Use EscPosPrinterCommands.QRCODE_... constants
     * @param text       String that contains QR code data
     * @param size       dot size of QR code pixel
     * @return Fluent interface
     */
    @Throws(EscPosEncodingException::class)
    suspend fun printQRCode(
        qrCodeType: Int,
        text: String,
        size: Int
    ): CoroutinesEscPosPrinterCommands {
        var size = size
        if (!printerConnection.isConnected()) {
            return this
        }
        if (size < 1) {
            size = 1
        } else if (size > 16) {
            size = 16
        }
        try {
            val textBytes = text.toByteArray(charset("UTF-8"))
            val commandLength = textBytes.size + 3
            val pL = commandLength % 256
            val pH = commandLength / 256

            /*byte[] qrCodeCommand = new byte[textBytes.length + 7];
            System.arraycopy(new byte[]{0x1B, 0x5A, 0x00, 0x00, (byte)size, (byte)pL, (byte)pH}, 0, qrCodeCommand, 0, 7);
            System.arraycopy(textBytes, 0, qrCodeCommand, 7, textBytes.length);
            this.printerConnection.write(qrCodeCommand);*/printerConnection.write(
                byteArrayOf(
                    0x1D,
                    0x28,
                    0x6B,
                    0x04,
                    0x00,
                    0x31,
                    0x41,
                    qrCodeType.toByte(),
                    0x00
                )
            )
            printerConnection.write(
                byteArrayOf(
                    0x1D,
                    0x28,
                    0x6B,
                    0x03,
                    0x00,
                    0x31,
                    0x43,
                    size.toByte()
                )
            )
            printerConnection.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, 0x30))
            val qrCodeCommand = ByteArray(textBytes.size + 8)
            System.arraycopy(
                byteArrayOf(
                    0x1D,
                    0x28,
                    0x6B,
                    pL.toByte(),
                    pH.toByte(),
                    0x31,
                    0x50,
                    0x30
                ), 0, qrCodeCommand, 0, 8
            )
            System.arraycopy(textBytes, 0, qrCodeCommand, 8, textBytes.size)
            printerConnection.write(qrCodeCommand)
            printerConnection.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            throw EscPosEncodingException(e.message)
        }
        return this
    }
    /**
     * Forces the transition to a new line and set the alignment of text and barcodes with the connected printer.
     *
     * @param align Set the alignment of text and barcodes. Use EscPosPrinterCommands.TEXT_ALIGN_... constants
     * @return Fluent interface
     */
    /**
     * Forces the transition to a new line with the connected printer.
     *
     * @return Fluent interface
     */
    @JvmOverloads
    suspend fun newLine(
        context: Context,
        align: ByteArray? = null
    ): CoroutinesEscPosPrinterCommands {
        if (!printerConnection.isConnected()) {
            return this
        }
        printerConnection.write(byteArrayOf(LF))
        printerConnection.send(context)
        if (align != null) {
            printerConnection.write(align)
        }
        return this
    }

    /**
     * Feed the paper
     *
     * @param dots Number of dots to feed (0 <= dots <= 255)
     * @return Fluent interface
     */
    suspend fun feedPaper(context: Context, dots: Int): CoroutinesEscPosPrinterCommands {
        if (!printerConnection.isConnected()) {
            return this
        }
        if (dots > 0) {
            printerConnection.write(byteArrayOf(0x1B, 0x4A, dots.toByte()))
            printerConnection.send(context, dots)
        }
        return this
    }

    /**
     * Cut the paper
     *
     * @return Fluent interface
     */
    suspend fun cutPaper(context: Context): CoroutinesEscPosPrinterCommands {
        if (!printerConnection.isConnected()) {
            return this
        }
        printerConnection.write(byteArrayOf(0x1D, 0x56, 0x01))
        printerConnection.send(context, 100)
        return this
    }

    companion object {
        const val LF: Byte = 0x0A
        val RESET_PRINTER = byteArrayOf(0x1B, 0x40)
        val TEXT_ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)
        val TEXT_ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)
        val TEXT_ALIGN_RIGHT = byteArrayOf(0x1B, 0x61, 0x02)
        val TEXT_WEIGHT_NORMAL = byteArrayOf(0x1B, 0x45, 0x00)
        val TEXT_WEIGHT_BOLD = byteArrayOf(0x1B, 0x45, 0x01)
        val TEXT_FONT_A = byteArrayOf(0x1B, 0x4D, 0x00)
        val TEXT_FONT_B = byteArrayOf(0x1B, 0x4D, 0x01)
        val TEXT_FONT_C = byteArrayOf(0x1B, 0x4D, 0x02)
        val TEXT_FONT_D = byteArrayOf(0x1B, 0x4D, 0x03)
        val TEXT_FONT_E = byteArrayOf(0x1B, 0x4D, 0x04)
        val TEXT_SIZE_NORMAL = byteArrayOf(0x1D, 0x21, 0x00)
        val TEXT_SIZE_DOUBLE_HEIGHT = byteArrayOf(0x1D, 0x21, 0x01)
        val TEXT_SIZE_DOUBLE_WIDTH = byteArrayOf(0x1D, 0x21, 0x10)
        val TEXT_SIZE_BIG = byteArrayOf(0x1D, 0x21, 0x11)
        val TEXT_UNDERLINE_OFF = byteArrayOf(0x1B, 0x2D, 0x00)
        val TEXT_UNDERLINE_ON = byteArrayOf(0x1B, 0x2D, 0x01)
        val TEXT_UNDERLINE_LARGE = byteArrayOf(0x1B, 0x2D, 0x02)
        val TEXT_DOUBLE_STRIKE_OFF = byteArrayOf(0x1B, 0x47, 0x00)
        val TEXT_DOUBLE_STRIKE_ON = byteArrayOf(0x1B, 0x47, 0x01)
        val TEXT_COLOR_BLACK = byteArrayOf(0x1B, 0x72, 0x00)
        val TEXT_COLOR_RED = byteArrayOf(0x1B, 0x72, 0x01)
        val TEXT_COLOR_REVERSE_OFF = byteArrayOf(0x1D, 0x42, 0x00)
        val TEXT_COLOR_REVERSE_ON = byteArrayOf(0x1D, 0x42, 0x01)
        const val BARCODE_TYPE_UPCA = 65
        const val BARCODE_TYPE_UPCE = 66
        const val BARCODE_TYPE_EAN13 = 67
        const val BARCODE_TYPE_EAN8 = 68
        const val BARCODE_TYPE_ITF = 70
        const val BARCODE_TYPE_128 = 73
        const val BARCODE_TEXT_POSITION_NONE = 0
        const val BARCODE_TEXT_POSITION_ABOVE = 1
        const val BARCODE_TEXT_POSITION_BELOW = 2
        const val QRCODE_1 = 49
        const val QRCODE_2 = 50
        fun initImageCommand(bytesByLine: Int, bitmapHeight: Int): ByteArray {
            val xH = bytesByLine / 256
            val xL = bytesByLine - xH * 256
            val yH = bitmapHeight / 256
            val yL = bitmapHeight - yH * 256
            val imageBytes = ByteArray(8 + bytesByLine * bitmapHeight)
            System.arraycopy(
                byteArrayOf(
                    0x1D,
                    0x76,
                    0x30,
                    0x00,
                    xL.toByte(),
                    xH.toByte(),
                    yL.toByte(),
                    yH.toByte()
                ), 0, imageBytes, 0, 8
            )
            return imageBytes
        }

        /**
         * Convert Bitmap instance to a byte array compatible with ESC/POS printer.
         *
         * @param bitmap Bitmap to be convert
         * @return Bytes contain the image in ESC/POS command
         */
        fun bitmapToBytes(bitmap: Bitmap): ByteArray {
            val bitmapWidth = bitmap.width
            val bitmapHeight = bitmap.height
            val bytesByLine = Math.ceil((bitmapWidth.toFloat() / 8f).toDouble())
                .toInt()
            val imageBytes = initImageCommand(bytesByLine, bitmapHeight)
            var i = 8
            for (posY in 0 until bitmapHeight) {
                var j = 0
                while (j < bitmapWidth) {
                    val stringBinary = StringBuilder()
                    for (k in 0..7) {
                        val posX = j + k
                        if (posX < bitmapWidth) {
                            val color = bitmap.getPixel(posX, posY)
                            val r = color shr 16 and 0xff
                            val g = color shr 8 and 0xff
                            val b = color and 0xff
                            if (r > 160 && g > 160 && b > 160) {
                                stringBinary.append("0")
                            } else {
                                stringBinary.append("1")
                            }
                        } else {
                            stringBinary.append("0")
                        }
                    }
                    imageBytes[i++] = stringBinary.toString().toInt(2).toByte()
                    j += 8
                }
            }
            return imageBytes
        }

        /**
         * Convert a string to QR Code byte array compatible with ESC/POS printer.
         *
         * @param data String data to convert in QR Code
         * @param size QR code dots size
         * @return Bytes contain the image in ESC/POS command
         */
        @Throws(EscPosBarcodeException::class)
        fun QRCodeDataToBytes(data: String?, size: Int): ByteArray {
            var byteMatrix: ByteMatrix? = null
            try {
                val hints = EnumMap<EncodeHintType, Any?>(
                    EncodeHintType::class.java
                )
                hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
                val code = Encoder.encode(data, ErrorCorrectionLevel.L, hints)
                byteMatrix = code.matrix
            } catch (e: WriterException) {
                e.printStackTrace()
                throw EscPosBarcodeException("Unable to encode QR code")
            }
            if (byteMatrix == null) {
                return initImageCommand(0, 0)
            }
            val width = byteMatrix.width
            val height = byteMatrix.height
            val coefficient = Math.round(size.toFloat() / width.toFloat())
            val imageWidth = width * coefficient
            val imageHeight = height * coefficient
            val bytesByLine = Math.ceil((imageWidth.toFloat() / 8f).toDouble()).toInt()
            var i = 8
            if (coefficient < 1) {
                return initImageCommand(0, 0)
            }
            val imageBytes = initImageCommand(bytesByLine, imageHeight)
            for (y in 0 until height) {
                val lineBytes = ByteArray(bytesByLine)
                var x = -1
                var multipleX = coefficient
                var isBlack = false
                for (j in 0 until bytesByLine) {
                    val stringBinary = StringBuilder()
                    for (k in 0..7) {
                        if (multipleX == coefficient) {
                            isBlack = ++x < width && byteMatrix[x, y].toInt() == 1
                            multipleX = 0
                        }
                        stringBinary.append(if (isBlack) "1" else "0")
                        ++multipleX
                    }
                    lineBytes[j] = stringBinary.toString().toInt(2).toByte()
                }
                for (multipleY in 0 until coefficient) {
                    System.arraycopy(lineBytes, 0, imageBytes, i, lineBytes.size)
                    i += lineBytes.size
                }
            }
            return imageBytes
        }
    }
    /**
     * Create new instance of EscPosPrinterCommands.
     *
     * @param printerConnection an instance of a class which implement DeviceConnection
     * @param charsetEncoding   Set the charset encoding.
     */

    /**
     * @return Charset encoding
     */
    fun getCharsetEncoding(): EscPosCharsetEncoding? {
        return charsetEncoding
    }
}