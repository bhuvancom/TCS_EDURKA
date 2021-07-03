package com.tcs.edureka.receivers;

interface USBInterface {

    fun onUsbAttached()
    fun onUsbDetached()
    fun onUsbPermissionGranted()
    fun onUsbPermissionDenied()
}