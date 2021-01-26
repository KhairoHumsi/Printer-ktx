package com.khairo.escposprinter.connection.tcp

import android.content.Context
import com.khairo.exeption.PrintingException.FINISH_PRINTER_DISCONNECTED
import com.khairo.exeption.onException
import java.io.IOException
import java.io.OutputStream
import kotlin.math.floor

abstract class TcpDeviceConnection {
    @JvmField
    protected var stream: OutputStream? = null

    @JvmField
    protected var data: ByteArray

    abstract suspend fun connect(context: Context): TcpDeviceConnection?
    abstract suspend fun disconnect(): TcpDeviceConnection?

    /**
     * Check if OutputStream is open.
     *
     * @return true if is connected
     */
//    open val isConnected: Boolean
//        get() = stream != null
    open  fun isConnected(): Boolean = this.stream != null

    /**
     * Add data to send.
     */
    suspend fun write(bytes: ByteArray) {
        val data = ByteArray(bytes.size + data.size)
        System.arraycopy(this.data, 0, data, 0, this.data.size)
        System.arraycopy(bytes, 0, data, this.data.size, bytes.size)
        this.data = data
    }

    /**
     * Send data to the device.
     */
    open suspend fun send(context: Context) {
        this.send(context, 0)
    }

    /**
     * Send data to the device.
     */
    open fun send(context: Context, addWaitingTime: Int) {
        if (!isConnected()) {
            onException(context, FINISH_PRINTER_DISCONNECTED)
            return
        }
        try {
            stream!!.write(data)
            stream!!.flush()
            data = ByteArray(0)
            val waitingTime = addWaitingTime + floor((data.size / 16f).toDouble())
                .toInt()
            if (waitingTime > 0) {
                Thread.sleep(waitingTime.toLong())
            }
        } catch (e: IOException) {
            e.printStackTrace()
            onException(context, FINISH_PRINTER_DISCONNECTED)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            onException(context, FINISH_PRINTER_DISCONNECTED)
        }
    }

    init {
        data = ByteArray(0)
    }
}