package com.adv.ilook.model.util.extension

import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import okhttp3.internal.format

// Permission request codes
const val REQUEST_CODE_CAMERA = 100
const val REQUEST_CODE_BLUETOOTH = 101
const val REQUEST_CODE_USB = 102
const val REQUEST_CODE_MICROPHONE = 103
const val REQUEST_CODE_LOCATION = 104
const val REQUEST_CODE_BACKGROUND_LOCATION = 105
const val REQUEST_CODE_ALL = 106
const val REQUEST_CODE_NOTIFICATION = 107

private const val TAG = "==>>Permission"
fun AppCompatActivity.hasPermission(
    context: Context, permission: List<String>, success: (result: Boolean) -> Unit
) {
    val getResult = permission.map {
        ActivityCompat.checkSelfPermission(
            context, it
        ) == PackageManager.PERMISSION_GRANTED
    }
    Log.d(TAG, "hasPermission: getResults : ${getResult}")
    for (per in permission) Log.d(TAG, "hasPermission: permission: $per")
    for (result in getResult) success(result)
}

// Request Bluetooth permission
fun AppCompatActivity.requestBluetoothPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    var permissionManifest: List<String> = listOf(BLUETOOTH)
    permissionManifest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            BLUETOOTH_SCAN,
            BLUETOOTH_CONNECT,
            BLUETOOTH_ADVERTISE
        )
    } else {
        listOf(BLUETOOTH, BLUETOOTH_ADMIN)
    }
    hasPermission(activity, permissionManifest) { isGranted ->
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                activity, permissionManifest.toTypedArray(), REQUEST_CODE_BLUETOOTH
            )
            success(false)
        } else {
            success(true)
        }

    }
}

// Request USB permission
fun AppCompatActivity.
        requestUsbPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    var permissionManifest: List<String> = listOf(READ_EXTERNAL_STORAGE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) permissionManifest =
        listOf(MANAGE_EXTERNAL_STORAGE)
    else listOf(
        READ_EXTERNAL_STORAGE,
        WRITE_EXTERNAL_STORAGE
    )
    hasPermission(activity, permissionManifest) {
        if (!it) {
            ActivityCompat.requestPermissions(
                activity, permissionManifest.toTypedArray(), REQUEST_CODE_USB
            )
             success(false)
        } else {
            success(true)
        }
    }
}

fun AppCompatActivity.
        requestNotificationPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    var permissionManifest: List<String> = listOf()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) permissionManifest =
        listOf(
            FOREGROUND_SERVICE_MEDIA_PROJECTION,FOREGROUND_SERVICE,
           FOREGROUND_SERVICE_MEDIA_PROJECTION,
            "android.permission.CAPTURE_VIDEO_OUTPUT",
            "android.permission.PROJECT_MEDIA")

    hasPermission(activity, permissionManifest) {
        if (!it) {
            ActivityCompat.requestPermissions(
                activity, permissionManifest.toTypedArray(), REQUEST_CODE_NOTIFICATION
            )
            success(false)
        } else {
            success(true)
        }
    }
}


// Request Microphone permission
fun AppCompatActivity.requestCameraMicrophonePermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    val permissionManifest: List<String> =
        listOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
    hasPermission(activity, permissionManifest) {
        if (!it) {
            ActivityCompat.requestPermissions(
                activity, permissionManifest.toTypedArray(), REQUEST_CODE_MICROPHONE
            )
            success(false)
        } else {
            success(true)
        }

    }
}

fun AppCompatActivity.requestCameraPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    hasPermission(activity, listOf(Manifest.permission.CAMERA)) {
        if (!it) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA
            )
             success(false)
        } else {
            success(true)
        }

    }
}

fun AppCompatActivity.requestLocationPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    hasPermission(activity, listOf(Manifest.permission.ACCESS_FINE_LOCATION)) {
        if (!it) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION
            )
             success(false)
        } else {

            success(true)
        }

    }
}

fun AppCompatActivity.requestBackgroundLocationPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {

        hasPermission(activity, listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            if (!it) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    REQUEST_CODE_BACKGROUND_LOCATION
                )
                success(false)
            } else {
                success(true)
            }

        }
    }
}

fun AppCompatActivity.requestAllPermission(
    activity: Activity, success: (result: Boolean) -> Unit
) {
    var permissionManifest: MutableList<String> = mutableListOf<String>()


    permissionManifest.add(CAMERA)
    permissionManifest.add(RECORD_AUDIO)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        permissionManifest.add(FOREGROUND_SERVICE_MICROPHONE)
    }
    permissionManifest.add(BLUETOOTH)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        permissionManifest.add(BLUETOOTH_SCAN)
        permissionManifest.add(BLUETOOTH_ADVERTISE)
    }
    permissionManifest.add(ACCESS_FINE_LOCATION)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        permissionManifest.add(ACCESS_BACKGROUND_LOCATION)
    }

    hasPermission(activity, permissionManifest) {
        if (!it) {
            ActivityCompat.requestPermissions(
                activity,
                permissionManifest.toTypedArray(),
                REQUEST_CODE_ALL
            )
             success(false)
        } else {
            success(true)
        }

    }
}