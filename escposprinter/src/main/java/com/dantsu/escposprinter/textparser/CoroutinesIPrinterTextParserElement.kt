package com.dantsu.escposprinter.textparser

import com.dantsu.escposprinter.CoroutinesEscPosPrinterCommands
import com.dantsu.escposprinter.exceptions.EscPosEncodingException

interface CoroutinesIPrinterTextParserElement {
    @Throws(EscPosEncodingException::class)
    fun length(): Int

    @Throws(EscPosEncodingException::class)
    suspend fun print(printerSocket: CoroutinesEscPosPrinterCommands?): CoroutinesIPrinterTextParserElement?
}
