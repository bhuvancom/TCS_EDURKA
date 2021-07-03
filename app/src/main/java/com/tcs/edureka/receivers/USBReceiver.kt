package com.tcs.edureka.receivers;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager

class USBReceiver : BroadcastReceiver() {
    companion object {
        val ACTION_USB_PERMISSION: String = "com.android.example.USB_PERMISSION"
    }

    private var usbInterface: USBInterface? = null
    public fun setUsbInterface(usbInterface: USBInterface) {
        this.usbInterface = usbInterface
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_USB_PERMISSION == intent.action) {
            synchronized(this) {
                val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)

                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    device?.apply {
                        usbInterface?.onUsbPermissionGranted()
                    }
                } else {
                    usbInterface?.onUsbPermissionDenied()
                }
            }
        }
        if (UsbManager.ACTION_USB_DEVICE_DETACHED == intent.action) {
            val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
            device?.apply {
                usbInterface?.onUsbDetached()
            }
        }
        if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.contentEquals(intent.action.toString())) {
            usbInterface?.onUsbAttached()
        }
    }
}