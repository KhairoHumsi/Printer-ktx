package com.khairo.escposprinter.connection.bluetooth

import android.bluetooth.BluetoothAdapter

open class BluetoothConnections {
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    /**
     * Get a list of bluetooth devices available.
     *
     * @return Return an array of BluetoothConnection instance
     */
    open fun getList(): Array<BluetoothConnection?>? {
        if (bluetoothAdapter == null) {
            return null
        }
        if (!bluetoothAdapter!!.isEnabled) {
            return null
        }
        val bluetoothDevicesList = bluetoothAdapter!!.bondedDevices
        val bluetoothDevices = arrayOfNulls<BluetoothConnection>(bluetoothDevicesList.size)
        if (bluetoothDevicesList.size > 0) {
            var i = 0
            for (device in bluetoothDevicesList) {
                bluetoothDevices[i++] = BluetoothConnection(device)
            }
        }
        return bluetoothDevices
    }

}