package com.khairo.escposprinter.connection.usb

import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface

object UsbDeviceHelper {
    /**
     * Find the correct USB interface for printing
     *
     * @param usbDevice USB device
     * @return correct USB interface for printing, null if not found
     */
    fun findPrinterInterface(usbDevice: UsbDevice?): UsbInterface? {
        if (usbDevice == null) {
            return null
        }
        val interfacesCount = usbDevice.interfaceCount
        for (i in 0 until interfacesCount) {
            val usbInterface = usbDevice.getInterface(i)
            if (usbInterface.interfaceClass == UsbConstants.USB_CLASS_PRINTER) {
                return usbInterface
            }
        }
        return usbDevice.getInterface(0)
    }

    /**
     * Find the USB endpoint for device input
     *
     * @param usbInterface USB interface
     * @return Input endpoint or null if not found
     */
    fun findEndpointIn(usbInterface: UsbInterface?): UsbEndpoint? {
        if (usbInterface != null) {
            val endpointsCount = usbInterface.endpointCount
            for (i in 0 until endpointsCount) {
                val endpoint = usbInterface.getEndpoint(i)
                if (endpoint.type == UsbConstants.USB_ENDPOINT_XFER_BULK && endpoint.direction == UsbConstants.USB_DIR_OUT) {
                    return endpoint
                }
            }
        }
        return null
    }
}