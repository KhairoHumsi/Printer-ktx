package com.khairo.escposprinter.connection.usb

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager

open class UsbConnections(context: Context) {
    protected var usbManager: UsbManager =
        context.getSystemService(Context.USB_SERVICE) as UsbManager

    /**
     * Get a list of USB devices available.
     *
     * @return Return an array of UsbConnection instance
     */
    open fun getList(): Array<UsbConnection?>? {
        val devicesList: Collection<UsbDevice> = usbManager.deviceList.values
        val usbDevices = arrayOfNulls<UsbConnection>(devicesList.size)
        if (devicesList.isNotEmpty()) {
            var i = 0
            for (device in devicesList) {
                usbDevices[i++] = UsbConnection(usbManager, device)
            }
        }
        return usbDevices
    }
}