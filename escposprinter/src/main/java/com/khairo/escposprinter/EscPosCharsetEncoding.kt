package com.khairo.escposprinter

class EscPosCharsetEncoding(val name: String, escPosCharsetId: Int) {
    val command: ByteArray = byteArrayOf(0x1B, 0x74, escPosCharsetId.toByte())

}
