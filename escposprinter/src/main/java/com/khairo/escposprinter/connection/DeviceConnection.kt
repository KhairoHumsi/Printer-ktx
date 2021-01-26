package com.khairo.escposprinter.connection

import com.khairo.escposprinter.exceptions.EscPosConnectionException
import java.io.IOException
import java.io.OutputStream
import kotlin.math.floor

abstract class DeviceConnection {
    @JvmField
    protected var stream: OutputStream? = null

    @JvmField
    protected var data: ByteArray

    @Throws(EscPosConnectionException::class)
    abstract fun connect(): DeviceConnection?
    abstract fun disconnect(): DeviceConnection?

    /**
     * Check if OutputStream is open.
     *
     * @return true if is connected
     */
//    open val isConnected: Boolean
//        get() = stream != null
    open fun isConnected(): Boolean = this.stream != null

    /**
     * Add data to send.
     */
    fun write(bytes: ByteArray) {
        val data = ByteArray(bytes.size + data.size)
        System.arraycopy(this.data, 0, data, 0, this.data.size)
        System.arraycopy(bytes, 0, data, this.data.size, bytes.size)
        this.data = data
    }

    /**
     * Send data to the device.
     */
    @Throws(EscPosConnectionException::class)
    open fun send() {
        this.send(0)
    }

    /**
     * Send data to the device.
     */
    @Throws(EscPosConnectionException::class)
    open fun send(addWaitingTime: Int) {
        if (!isConnected()) {
            throw EscPosConnectionException("Unable to send data to device.")
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
            throw EscPosConnectionException(e.message)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            throw EscPosConnectionException(e.message)
        }
    }

    init {
        data = ByteArray(0)
    }
}