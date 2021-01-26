package com.khairo.escposprinter.connection.usb

import android.hardware.usb.*
import java.io.IOException
import java.io.OutputStream
import java.nio.ByteBuffer

class UsbOutputStream(usbManager: UsbManager, usbDevice: UsbDevice?) : OutputStream() {
    private var usbConnection: UsbDeviceConnection?
    private var usbInterface: UsbInterface?
    private var usbEndpoint: UsbEndpoint?

    @Throws(IOException::class)
    override fun write(i: Int) {
        this.write(byteArrayOf(i.toByte()))
    }

    @Throws(IOException::class)
    override fun write(bytes: ByteArray) {
        this.write(bytes, 0, bytes.size)
    }

    @Throws(IOException::class)
    override fun write(bytes: ByteArray, offset: Int, length: Int) {
        if (usbInterface == null || usbEndpoint == null || usbConnection == null) {
            throw IOException("Unable to connect to USB device.")
        }
        if (!usbConnection!!.claimInterface(usbInterface, true)) {
            throw IOException("Error during claim USB interface.")
        }
        val buffer = ByteBuffer.wrap(bytes)
        val usbRequest = UsbRequest()
        try {
            usbRequest.initialize(usbConnection, usbEndpoint)
            if (!usbRequest.queue(buffer, bytes.size)) {
                throw IOException("Error queueing USB request.")
            }
            usbConnection!!.requestWait()
        } finally {
            usbRequest.close()
        }
    }

    @Throws(IOException::class)
    override fun flush() {
    }

    @Throws(IOException::class)
    override fun close() {
        if (usbConnection != null) {
            usbConnection!!.close()
            usbInterface = null
            usbEndpoint = null
            usbConnection = null
        }
    }

    init {
        usbInterface = UsbDeviceHelper.findPrinterInterface(usbDevice)
        if (usbInterface == null) {
            throw IOException("Unable to find USB interface.")
        }
        usbEndpoint = UsbDeviceHelper.findEndpointIn(usbInterface)
        if (usbEndpoint == null) {
            throw IOException("Unable to find USB endpoint.")
        }
        usbConnection = usbManager.openDevice(usbDevice)
        if (usbConnection == null) {
            throw IOException("Unable to open USB connection.")
        }
    }
}