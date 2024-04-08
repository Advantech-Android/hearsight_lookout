package com.adv.ilook.model.util.permissions

import android.Manifest.permission.*

sealed class Permission(vararg val permissions: String) {
    // Individual permissions
    object Camera : Permission(CAMERA)

    // Bundled permissions
    object MandatoryForFeatureOne : Permission(WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION)

    // Grouped permissions
    object Location : Permission(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    object Storage : Permission(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
    object Bluetooth : Permission( BLUETOOTH_ADMIN,BLUETOOTH)
    object HS_Permissions: Permission(BLUETOOTH_ADMIN,BLUETOOTH,ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION,
       CAMERA,WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE)


    companion object {
        fun from(permission: String) = when (permission) {
            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION -> Location
            WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE -> Storage
            CAMERA -> Camera
            BLUETOOTH_ADMIN,BLUETOOTH -> Bluetooth
            else -> throw IllegalArgumentException("Unknown permission: $permission")
        }
    }
}