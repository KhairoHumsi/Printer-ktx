package com.dantsu.escposprinter.textparser

import com.dantsu.escposprinter.EscPosPrinterCommands
import com.dantsu.escposprinter.exceptions.EscPosEncodingException

interface IPrinterTextParserElement {
    @Throws(EscPosEncodingException::class)
    fun length(): Int

    @Throws(EscPosEncodingException::class)
    fun print(printerSocket: EscPosPrinterCommands?): IPrinterTextParserElement?
}
