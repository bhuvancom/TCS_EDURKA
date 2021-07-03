package com.tcs.edureka.utility

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import com.github.mjdev.libaums.fs.FileSystem
import com.github.mjdev.libaums.fs.UsbFileInputStream
import com.github.mjdev.libaums.fs.UsbFileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

/**
 * @author Bhuvaneshvar
 */
class UsbUtil(usbManager: UsbManager, usbDevice: UsbDevice) {

    companion object {

        private const val TAG = "UsbUtil"

        suspend fun readData(currentFs: FileSystem, onComplete: (String) -> Unit, onFailure: (String) -> Unit) {
            withContext(Dispatchers.IO) {
                try {
                    val root = currentFs.rootDirectory
                    val search = root.search("TCS_MAP")
                    if (search != null) {
                        if (search.isDirectory) {
                            val search1 = search.search("mapData.json")
                            if (search1 != null && !search1.isDirectory) {
                                val ins = UsbFileInputStream(search1)
                                val buffer = ByteArray(currentFs.chunkSize)
                                ins.read(buffer)
                                val str = String(buffer, StandardCharsets.UTF_8)
                                onComplete(str)
                                Log.d(TAG, "readData: found")
                            } else {
                                onFailure("Folder not found")
                            }
                        } else {
                            onFailure("Folder not found")
                        }
                    } else {
                        onFailure("Folder not found")
                    }
                } catch (error: Exception) {
                    onFailure("Error ${error.message}")
                }

            }
        }

        suspend fun writeDate(currentFs: FileSystem,
                              data: String,
                              onComplete: (Boolean) -> Unit,
                              onFailure: (String) -> Unit) {
            withContext(Dispatchers.IO) {
                try {
                    val root = currentFs.rootDirectory
                    val files = root.listFiles()
                    for (file in files) {
                        Log.d(TAG, file.name)
                        if (file.isDirectory) {
                            Log.d(TAG, "$file")
                        }
                    }
                    val search = root.search("TCS_MAP")
                    search?.delete()

                    val newDir = root.createDirectory("TCS_MAP")
                    val file = newDir.createFile("mapData.json")
                    // write to a file
                    val os = UsbFileOutputStream(file)
                    os.write(data.encodeToByteArray())
                    Log.d(TAG, "writeDate: writing ${data.encodeToByteArray()}  it " +
                            "should be ${String(data.encodeToByteArray())} ")
                    os.close()

                    onComplete(true)
                } catch (error: Exception) {
                    Log.d(TAG, "writeDate: error ${error.message}")
                    onFailure("Error ${error.message}")
                }
            }
        }

        private val ACTION_USB_PERMISSION: String = "com.android.example.USB_PERMISSION"

        fun getPermission(context: Context,
                          usbManager: UsbManager,
                          usbReceiver: BroadcastReceiver,
                          usbDevice: UsbDevice) {
            if (usbManager.hasPermission(usbDevice)) return

            val permissionIntent = PendingIntent.getBroadcast(context,
                    0, Intent(ACTION_USB_PERMISSION), 0)
            val filter = IntentFilter(ACTION_USB_PERMISSION)
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            context.registerReceiver(usbReceiver, filter)
            usbManager.requestPermission(usbDevice, permissionIntent)
        }
    }
}