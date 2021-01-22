package com.khairo.printer.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.dantsu.async.*
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.tcp.TcpConnection
import com.dantsu.escposprinter.connection.usb.UsbConnection
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.dantsu.escposprinter.exceptions.EscPosEncodingException
import com.dantsu.escposprinter.exceptions.EscPosParserException
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.khairo.printer.R
import com.khairo.printer.databinding.ActivityMainBinding
import com.khairo.printer.utils.barcodeGenerator
import com.khairo.printer.utils.printViaWifi
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply {
            buttonTcp.setOnClickListener {
                lifecycleScope.launch () {
                    printTcp()
                }
            }

            buttonBluetooth.setOnClickListener {
                printBluetooth()
            }

            buttonUsb.setOnClickListener {
                printUsb()
            }
        }
    }

    /*==============================================================================================
    ======================================BLUETOOTH PART============================================
    ==============================================================================================*/
    private val PERMISSION_BLUETOOTH = 1

    private fun printBluetooth() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH),
                PERMISSION_BLUETOOTH
            )
        } else {
            // this.printIt(BluetoothPrintersConnections.selectFirstPaired());
            AsyncBluetoothEscPosPrint(this).execute(this.getAsyncEscPosPrinter(null))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PERMISSION_BLUETOOTH -> printBluetooth()
            }
        }
    }

    /*==============================================================================================
    ===========================================USB PART=============================================
    ==============================================================================================*/
    private val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    private val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val usbManager = getSystemService(USB_SERVICE) as UsbManager
                    val usbDevice =
                        intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbDevice != null) {
                            // printIt(new UsbConnection(usbManager, usbDevice));
                            AsyncUsbEscPosPrint(context)
                                .execute(
                                    getAsyncEscPosPrinter(
                                        UsbConnection(
                                            usbManager,
                                            usbDevice
                                        )
                                    )
                                )
                        }
                    }
                }
            }
        }
    }

    fun printUsb() {
        val usbConnection = UsbPrintersConnections.selectFirstConnected(this)
        val usbManager = this.getSystemService(USB_SERVICE) as UsbManager
        if (usbConnection == null) {
            AlertDialog.Builder(this)
                .setTitle("USB Connection")
                .setMessage("No USB printer found.")
                .show()
            return
        }
        val permissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        registerReceiver(usbReceiver, filter)
        usbManager.requestPermission(usbConnection.device, permissionIntent)
    }

    /*==============================================================================================
    ===================================ESC/POS PRINTER PART=========================================
    ==============================================================================================*/
    /**
     * Synchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    fun printIt(printerConnection: DeviceConnection?) {
        AsyncTask.execute {
            try {
                val format = SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss")
                val printer = EscPosPrinter(printerConnection, 203, 48f, 32)
                printer
                    .printFormattedText(
                        "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                            printer,
                            applicationContext.resources.getDrawableForDensity(
                                R.drawable.logo,
                                DisplayMetrics.DENSITY_MEDIUM
                            )
                        ) + "</img>\n" +
                                "[L]\n" +
                                "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
                                "[C]<font size='small'>" + format.format(Date()) + "</font>\n" +
                                "[L]\n" +
                                "[C]==================عربي تيست هههههههه==============\n" +
                                "[L]\n" +
                                "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
                                "[L]  + Size : S\n" +
                                "[L]\n" +
                                "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
                                "[L]  + Size : 57/58\n" +
                                "[L]\n" +
                                "[C]--------------------------------\n" +
                                "[R]TOTAL PRICE :[R]34.98e\n" +
                                "[R]TAX :[R]4.23e\n" +
                                "[L]\n" +
                                "[C]================================\n" +
                                "[L]\n" +
                                "[L]<font size='tall'>Customer :</font>\n" +
                                "[L]Raymond DUPONT\n" +
                                "[L]5 rue des girafes\n" +
                                "[L]31547 PERPETES\n" +
                                "[L]Tel : +33801201456\n" +
                                "[L]\n" +
                                "[C]<barcode type='128' height='10'>83125478455134567890</barcode>\n" +
                                "[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>" +
                                "[L]\n" +
                                "[L]\n" +
                                "[L]\n" +
                                "[L]\n" +
                                "[L]\n" +
                                "[L]\n"
                    )
            } catch (e: EscPosConnectionException) {
                e.printStackTrace()
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Broken connection")
                    .setMessage(e.message)
                    .show()
            } catch (e: EscPosParserException) {
                e.printStackTrace()
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Invalid formatted text")
                    .setMessage(e.message)
                    .show()
            } catch (e: EscPosEncodingException) {
                e.printStackTrace()
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Bad selected encoding")
                    .setMessage(e.message)
                    .show()
            } catch (e: EscPosBarcodeException) {
                e.printStackTrace()
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Invalid barcode")
                    .setMessage(e.message)
                    .show()
            }
        }
    }

    /**
     * Asynchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    fun getAsyncEscPosPrinter(printerConnection: DeviceConnection?): AsyncEscPosPrinter {
        val format = SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss")
        val printer = AsyncEscPosPrinter(printerConnection!!, 203, 48f, 32)
        return printer.setTextToPrint(
            "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                printer,
                this.applicationContext.resources.getDrawableForDensity(
                    R.drawable.logo,
                    DisplayMetrics.DENSITY_MEDIUM
                )
            ) + "</img>\n" +
                    "[L]\n" +
                    "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
                    "[L]\n" +
                    "[C]<u type='double'>" + format.format(Date()) + "</u>\n" +
                    "[C]\n" +
                    "[C]================================\n" +
                    "[L]\n" +
                    "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
                    "[L]  + Size : S\n" +
                    "[L]\n" +
                    "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
                    "[L]  + Size : 57/58\n" +
                    "[L]\n" +
                    "[C]--------------------------------\n" +
                    "[R]TOTAL PRICE :[R]34.98e\n" +
                    "[R]TAX :[R]4.23e\n" +
                    "[L]\n" +
                    "[C]================================\n" +
                    "[L]\n" +
                    "[L]<u><font color='bg-black' size='tall'>Customer :</font></u>\n" +
                    "[L]Raymond DUPONT\n" +
                    "[L]5 rue des girafes\n" +
                    "[L]31547 PERPETES\n" +
                    "[L]Tel : +33801201456\n" +
                    "\n" +
                    "[C]<barcode type='128' height='10'>83125478455134567890</barcode>\n" +
                    "[L]\n" +
                    "[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>\n" +
                    "[L]\n" +
                    "[L]\n" +
                    "[L]\n" +
                    "[L]\n" +
                    "[L]\n" +
                    "[L]\n"
        )
    }

    /*==============================================================================================
    =========================================TCP PART===============================================
    ==============================================================================================*/
    private suspend fun printTcp() {
        try {
            val printer = CoroutinesEscPosPrinter(TcpConnection("192.168.1.151", 9100), 203, 48f, 32)

            val test = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.applicationContext.resources.getDrawableForDensity(R.drawable.logo, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                    "[L]\n" +
                    "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.applicationContext.resources.getDrawableForDensity(R.drawable.logo2, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                    "[L]\n" +
                    "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
                    "[L]\n" +
                    "[C]================================\n" +
                    "[L]\n" +
                    "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
                    "[L]  + Size : S\n" +
                    "[L]\n" +
                    "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
                    "[L]  + Size : 57/58\n" +
                    "[L]\n" +
                    "[C]--------------------------------\n" +
                    "[R]TOTAL PRICE :[R]34.98e\n" +
                    "[R]TAX :[R]4.23e\n" +
                    "[L]\n" +
                    "[C]================================\n" +
                    "[L]\n" +
                    "[L]<u><font color='bg-black' size='tall'>Customer :</font></u>\n" +
                    "[L]Raymond DUPONT\n" +
                    "[L]5 rue des girafes\n" +
                    "[L]31547 PERPETES\n" +
                    "[L]Tel : +33801201456\n" +
                    "\n" +
                    "[C]<barcode type='128' height='10'>83125478455134567890</barcode>\n" +
                    "[L]\n" +
                    "[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>\n" +
                    "[L]\n" +
                    "[L]\n" +
                    "[L]\n" +
                    "[L]\n" +
                    "[L]\n" +
                    "[L]\n"
//             this.printIt(new TcpConnection(ipAddress.getText().toString(), Integer.parseInt(portAddress.getText().toString())));
//            AsyncTcpEscPosPrint(this).execute(printer.setTextToPrint(test))

            CoroutinesEscPosPrint(this).execute(printer.setTextToPrint(test))

        } catch (e: NumberFormatException) {
            AlertDialog.Builder(this)
                .setTitle("Invalid TCP port address")
                .setMessage("Port field must be a number.")
                .show()
            e.printStackTrace()
        }
    }

    private fun body(): String = "[L]\n" +
            "[L]    <b>Pizza</b>[R][R]3[R][R]55 $\n" +
            "[L]      + Olive[R][R]1 $\n" +
            "[L]      + Cheese[R][R]5 $\n" +
            "[L]      + Mushroom[R][R]7 $\n" +
            "[L]\n" +
            "[L]    <b>Burger</b>[R][R]7[R][R]43.54 $\n" +
            "[L]      + Cheese[R][R]3 $\n" +
            "[L]\n" +
            "[L]    <b>Shawarma</b>[R][R]2[R][R]4 $\n" +
            "[L]      + Garlic[R][R]0.5 $\n" +
            "[L]\n" +
            "[L]    <b>Steak</b>[R][R]3[R][R]75 $\n" +
            "[L]\n" +
            "[R] PAYMENT METHOD :[R]Visa\n"

    private fun customer(): String =
        "[C]================================\n" +
                "[L]\n" +
                "[L]<b>Delivery</b>[R]5 $\n" +
                "[L]\n" +
                "[L]<u><font color='bg-black' size='tall'>Customer :</font></u>\n" +
                "[L]Name : Mohammad khair\n" +
                "[L]Phone : 00962787144627\n" +
                "[L]Area : Khalda\n" +
                "[L]street : testing street\n" +
                "[L]building : 9\n" +
                "[L]Floor : 2\n" +
                "[L]Apartment : 1\n" +
                "[L]Note : This order is just for testing\n"
}
