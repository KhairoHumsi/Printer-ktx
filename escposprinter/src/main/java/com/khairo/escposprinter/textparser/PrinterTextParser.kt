package com.khairo.escposprinter.textparser

import com.khairo.escposprinter.EscPosPrinter
import com.khairo.escposprinter.EscPosPrinterCommands
import com.khairo.escposprinter.exceptions.EscPosBarcodeException
import com.khairo.escposprinter.exceptions.EscPosEncodingException
import com.khairo.escposprinter.exceptions.EscPosParserException

class PrinterTextParser(val printer: EscPosPrinter) {
    private var textSize = arrayOf(EscPosPrinterCommands.TEXT_SIZE_NORMAL)
    private var textColor = arrayOf(EscPosPrinterCommands.TEXT_COLOR_BLACK)
    private var textReverseColor = arrayOf(EscPosPrinterCommands.TEXT_COLOR_REVERSE_OFF)
    private var textBold = arrayOf(EscPosPrinterCommands.TEXT_WEIGHT_NORMAL)
    private var textUnderline = arrayOf(EscPosPrinterCommands.TEXT_UNDERLINE_OFF)
    private var textDoubleStrike = arrayOf(EscPosPrinterCommands.TEXT_DOUBLE_STRIKE_OFF)
    private var text = ""
    fun setFormattedText(text: String): PrinterTextParser {
        this.text = text
        return this
    }

    val lastTextSize: ByteArray
        get() = textSize[textSize.size - 1]

    fun addTextSize(newTextSize: ByteArray): PrinterTextParser {
        textSize = arrayBytePush(textSize, newTextSize).toTypedArray()
        return this
    }

    fun dropLastTextSize(): PrinterTextParser {
        if (textSize.size > 1) {
            textSize = arrayByteDropLast(textSize).toTypedArray()
        }
        return this
    }

    val lastTextColor: ByteArray
        get() = textColor[textColor.size - 1]

    fun addTextColor(newTextColor: ByteArray): PrinterTextParser {
        textColor = arrayBytePush(textColor, newTextColor).toTypedArray()
        return this
    }

    fun dropLastTextColor(): PrinterTextParser {
        if (textColor.size > 1) {
            textColor = arrayByteDropLast(textColor).toTypedArray()
        }
        return this
    }

    val lastTextReverseColor: ByteArray
        get() = textReverseColor[textReverseColor.size - 1]

    fun addTextReverseColor(newTextReverseColor: ByteArray): PrinterTextParser {
        textReverseColor = arrayBytePush(textReverseColor, newTextReverseColor).toTypedArray()
        return this
    }

    fun dropLastTextReverseColor(): PrinterTextParser {
        if (textReverseColor.size > 1) {
            textReverseColor = arrayByteDropLast(textReverseColor).toTypedArray()
        }
        return this
    }

    val lastTextBold: ByteArray
        get() = textBold[textBold.size - 1]

    fun addTextBold(newTextBold: ByteArray): PrinterTextParser {
        textBold = arrayBytePush(textBold, newTextBold).toTypedArray()
        return this
    }

    fun dropTextBold(): PrinterTextParser {
        if (textBold.size > 1) {
            textBold = arrayByteDropLast(textBold).toTypedArray()
        }
        return this
    }

    val lastTextUnderline: ByteArray
        get() = textUnderline[textUnderline.size - 1]

    fun addTextUnderline(newTextUnderline: ByteArray): PrinterTextParser {
        textUnderline = arrayBytePush(textUnderline, newTextUnderline).toTypedArray()
        return this
    }

    fun dropLastTextUnderline(): PrinterTextParser {
        if (textUnderline.size > 1) {
            textUnderline = arrayByteDropLast(textUnderline).toTypedArray()
        }
        return this
    }

    val lastTextDoubleStrike: ByteArray
        get() = textDoubleStrike[textDoubleStrike.size - 1]

    fun addTextDoubleStrike(newTextDoubleStrike: ByteArray): PrinterTextParser {
        textDoubleStrike = arrayBytePush(textDoubleStrike, newTextDoubleStrike).toTypedArray()
        return this
    }

    fun dropLastTextDoubleStrike(): PrinterTextParser {
        if (textDoubleStrike.size > 1) {
            textDoubleStrike = arrayByteDropLast(textDoubleStrike).toTypedArray()
        }
        return this
    }

    @Throws(
        EscPosParserException::class,
        EscPosBarcodeException::class,
        EscPosEncodingException::class
    )
    fun parse(): Array<PrinterTextParserLine?> {
        val stringLines = text.split("\n|\r\n".toRegex()).toTypedArray()
        val lines = arrayOfNulls<PrinterTextParserLine>(stringLines.size)
        var i = 0
        for (line in stringLines) {
            lines[i++] = PrinterTextParserLine(this, line)
        }
        return lines
    }

    companion object {
        const val TAGS_ALIGN_LEFT = "L"
        const val TAGS_ALIGN_CENTER = "C"
        const val TAGS_ALIGN_RIGHT = "R"
        val TAGS_ALIGN = arrayOf(TAGS_ALIGN_LEFT, TAGS_ALIGN_CENTER, TAGS_ALIGN_RIGHT)
        const val TAGS_IMAGE = "img"
        const val TAGS_BARCODE = "barcode"
        const val TAGS_QRCODE = "qrcode"
        const val ATTR_BARCODE_WIDTH = "width"
        const val ATTR_BARCODE_HEIGHT = "height"
        const val ATTR_BARCODE_TYPE = "type"
        const val ATTR_BARCODE_TYPE_EAN8 = "ean8"
        const val ATTR_BARCODE_TYPE_EAN13 = "ean13"
        const val ATTR_BARCODE_TYPE_UPCA = "upca"
        const val ATTR_BARCODE_TYPE_UPCE = "upce"
        const val ATTR_BARCODE_TYPE_128 = "128"
        const val ATTR_BARCODE_TEXT_POSITION = "text"
        const val ATTR_BARCODE_TEXT_POSITION_NONE = "none"
        const val ATTR_BARCODE_TEXT_POSITION_ABOVE = "above"
        const val ATTR_BARCODE_TEXT_POSITION_BELOW = "below"
        const val TAGS_FORMAT_TEXT_FONT = "font"
        const val TAGS_FORMAT_TEXT_BOLD = "b"
        const val TAGS_FORMAT_TEXT_UNDERLINE = "u"
        val TAGS_FORMAT_TEXT =
            arrayOf(TAGS_FORMAT_TEXT_FONT, TAGS_FORMAT_TEXT_BOLD, TAGS_FORMAT_TEXT_UNDERLINE)
        const val ATTR_FORMAT_TEXT_UNDERLINE_TYPE = "type"
        const val ATTR_FORMAT_TEXT_UNDERLINE_TYPE_NORMAL = "normal"
        const val ATTR_FORMAT_TEXT_UNDERLINE_TYPE_DOUBLE = "double"
        const val ATTR_FORMAT_TEXT_FONT_SIZE = "size"
        const val ATTR_FORMAT_TEXT_FONT_SIZE_BIG = "big"
        const val ATTR_FORMAT_TEXT_FONT_SIZE_TALL = "tall"
        const val ATTR_FORMAT_TEXT_FONT_SIZE_WIDE = "wide"
        const val ATTR_FORMAT_TEXT_FONT_SIZE_NORMAL = "normal"
        const val ATTR_FORMAT_TEXT_FONT_COLOR = "color"
        const val ATTR_FORMAT_TEXT_FONT_COLOR_BLACK = "black"
        const val ATTR_FORMAT_TEXT_FONT_COLOR_BG_BLACK = "bg-black"
        const val ATTR_FORMAT_TEXT_FONT_COLOR_RED = "red"
        const val ATTR_FORMAT_TEXT_FONT_COLOR_BG_RED = "bg-red"
        const val ATTR_QRCODE_SIZE = "size"
        var regexAlignTags: String? = null
            get() {
                if (field == null) {
                    val regexAlignTags = StringBuilder()
                    for (i in TAGS_ALIGN.indices) {
                        regexAlignTags.append("|\\[").append(TAGS_ALIGN[i]).append("\\]")
                    }
                    field = regexAlignTags.toString().substring(1)
                }
                return field
            }
            private set

        @JvmStatic
        fun isTagTextFormat(oldTagName: String): Boolean {
            var tagName = oldTagName
            if (tagName.startsWith("/")) tagName = tagName.substring(1)

            for (tag in TAGS_FORMAT_TEXT) {
                if (tag == tagName) return true
            }
            return false
        }

//        fun arrayByteDropLast(arr: Array<ByteArray>): Array<ByteArray> {
//            if (arr.isEmpty()) return arr
//            System.arraycopy(arr, 0, arr, 0, arr.size)
//            return arr
//        }

//        fun arrayBytePush(arr: Array<ByteArray>, add: ByteArray): Array<ByteArray> {
//            var newArr = ArrayList<ByteArray>()
//
//            for (i in arr.indices) {
//                newArr[i] = arr[i]
//            }
//            System.arraycopy(arr, 0, arr, 0, arr.size)
//            Log.d("asdadada", "size: ${arr.size}")
//            arr[arr.size] = add
//            return newArr
//        }

        fun arrayByteDropLast(arr: Array<ByteArray>): List<ByteArray> {
            if (arr.isEmpty()) {
                return arr.toList()
            }
            val newArr = arrayOfNulls<ByteArray>(arr.size - 1)
            System.arraycopy(arr, 0, newArr, 0, newArr.size)
            return newArr.filterNotNull()
        }

        //
        fun arrayBytePush(arr: Array<ByteArray>, add: ByteArray): List<ByteArray> {
            val newArr = arrayOfNulls<ByteArray>(arr.size + 1)
            System.arraycopy(arr, 0, newArr, 0, arr.size)
            newArr[arr.size] = add
            return newArr.filterNotNull()
        }
    }
}