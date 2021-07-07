package com.tcs.edureka.receivers;

/**
 * @author Bhuvaneshvar
 */
interface USBInterface {

    fun onUsbAttached()
    fun onUsbDetached()
    fun onUsbPermissionGranted()
    fun onUsbPermissionDenied()
}