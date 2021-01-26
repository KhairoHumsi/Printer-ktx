package com.khairo.escposprinter.connection.usb

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.khairo.escposprinter.connection.DeviceConnection
import com.khairo.escposprinter.exceptions.EscPosConnectionException
import java.io.IOException

class UsbConnection
/**
 * Create un instance of UsbConnection.
 *
 * @param usbManager an instance of UsbManager
 * @param device  an instance of UsbDevice
 */(
    private val usbManager: UsbManager,
    /**
     * Get the instance UsbDevice connected.
     *
     * @return an instance of UsbDevice
     */
    val device: UsbDevice
) : DeviceConnection() {

    /**
     * Start socket connection with the usbDevice.
     */
    @Throws(EscPosConnectionException::class)
    override fun connect(): UsbConnection {
        if (isConnected()) {
            return this
        }
        try {
            stream = UsbOutputStream(usbManager, device)
            data = ByteArray(0)
        } catch (e: IOException) {
            e.printStackTrace()
            stream = null
            throw EscPosConnectionException("Unable to connect to USB device.")
        }
        return this
    }

    /**
     * Close the socket connection with the usbDevice.
     */
    override fun disconnect(): UsbConnection {
        data = ByteArray(0)
        if (isConnected()) {
            try {
                stream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            stream = null
        }
        return this
    }

    /**
     * Send data to the device.
     */
    @Throws(EscPosConnectionException::class)
    override fun send() {
        this.send(0)
    }

    /**
     * Send data to the device.
     */
    @Throws(EscPosConnectionException::class)
    override fun send(addWaitingTime: Int) {
        try {
            stream!!.write(data)
            data = ByteArray(0)
        } catch (e: IOException) {
            e.printStackTrace()
            throw EscPosConnectionException(e.message)
        }
    }
}