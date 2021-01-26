package com.khairo.escposprinter.connection.usb

import android.content.Context
import android.hardware.usb.UsbConstants

class UsbPrintersConnections
/**
 * Create a new instance of UsbPrintersConnections
 *
 * @param context Application context
 */
    (context: Context) : UsbConnections(context) {

    override fun getList(): Array<UsbConnection?> {
        val usbConnections = super.getList()
        var i = 0
        val printersTmp = arrayOfNulls<UsbConnection>(usbConnections!!.size)
        for (usbConnection in usbConnections) {
            val device = usbConnection!!.device
            var usbClass = device.deviceClass
            if (usbClass == UsbConstants.USB_CLASS_PER_INTERFACE && UsbDeviceHelper.findPrinterInterface(
                    device
                ) != null
            ) {
                usbClass = UsbConstants.USB_CLASS_PRINTER
            }
            if (usbClass == UsbConstants.USB_CLASS_PRINTER) {
                printersTmp[i++] = UsbConnection(usbManager, device)
            }
        }
        val usbPrinters = arrayOfNulls<UsbConnection>(i)
        System.arraycopy(printersTmp, 0, usbPrinters, 0, i)
        return usbPrinters
    }

    companion object {
        /**
         * Easy way to get the first USB printer paired / connected.
         *
         * @return a UsbConnection instance
         */
        fun selectFirstConnected(context: Context): UsbConnection? {
            val printers = UsbPrintersConnections(context)
            val bluetoothPrinters = printers.getList()
            return if (bluetoothPrinters.isEmpty()) {
                null
            } else bluetoothPrinters[0]
        }
    }
}