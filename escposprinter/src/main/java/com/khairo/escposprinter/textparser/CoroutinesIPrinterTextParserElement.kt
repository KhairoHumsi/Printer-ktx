package com.khairo.escposprinter.textparser

import com.khairo.escposprinter.CoroutinesEscPosPrinterCommands
import com.khairo.escposprinter.exceptions.EscPosEncodingException

interface CoroutinesIPrinterTextParserElement {
    @Throws(EscPosEncodingException::class)
    fun length(): Int

    @Throws(EscPosEncodingException::class)
    suspend fun print(printerSocket: CoroutinesEscPosPrinterCommands?): CoroutinesIPrinterTextParserElement?
}
