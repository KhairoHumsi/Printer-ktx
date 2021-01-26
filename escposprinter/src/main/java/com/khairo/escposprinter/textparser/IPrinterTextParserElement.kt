package com.khairo.escposprinter.textparser

import com.khairo.escposprinter.EscPosPrinterCommands
import com.khairo.escposprinter.exceptions.EscPosEncodingException

interface IPrinterTextParserElement {
    @Throws(EscPosEncodingException::class)
    fun length(): Int

    @Throws(EscPosEncodingException::class)
    fun print(printerSocket: EscPosPrinterCommands?): IPrinterTextParserElement?
}
