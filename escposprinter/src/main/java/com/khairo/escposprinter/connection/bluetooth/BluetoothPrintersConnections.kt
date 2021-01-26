package com.khairo.escposprinter.connection.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import com.khairo.escposprinter.exceptions.EscPosConnectionException

class BluetoothPrintersConnections : BluetoothConnections() {
    /**
     * Get a list of bluetooth printers.
     *
     * @return an array of EscPosPrinterCommands
     */
    @SuppressLint("MissingPermission")
    override fun getList(): Array<BluetoothConnection?> {
        val bluetoothDevicesList = super.getList()
        var i = 0
        val printersTmp = arrayOfNulls<BluetoothConnection>(bluetoothDevicesList!!.size)
        for (bluetoothConnection in bluetoothDevicesList) {
            val device = bluetoothConnection!!.device
            val majDeviceCl = device!!.bluetoothClass.majorDeviceClass
            val deviceCl = device.bluetoothClass.deviceClass
            if (majDeviceCl == BluetoothClass.Device.Major.IMAGING && (deviceCl == 1664 || deviceCl == BluetoothClass.Device.Major.IMAGING)) {
                printersTmp[i++] = BluetoothConnection(device)
            }
        }
        val bluetoothPrinters = arrayOfNulls<BluetoothConnection>(i)
        System.arraycopy(printersTmp, 0, bluetoothPrinters, 0, i)
        return bluetoothPrinters
    }

    companion object {
        /**
         * Easy way to get the first bluetooth printer paired / connected.
         *
         * @return a EscPosPrinterCommands instance
         */
        fun selectFirstPaired(): BluetoothConnection? {
            val printers = BluetoothPrintersConnections()
            val bluetoothPrinters = printers.getList()
            if (bluetoothPrinters.isNotEmpty()) {
                for (printer in bluetoothPrinters) {
                    try {
                        return printer!!.connect()
                    } catch (e: EscPosConnectionException) {
                        e.printStackTrace()
                    }
                }
            }
            return null
        }
    }
}