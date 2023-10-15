package fi.notesnap.notesnap.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build

object CameraUtilities {

    //used to ask user for permission to use camera
    @SuppressLint("ObsoleteSdkInt")
    val REQUIRED_PERMISSIONS =
        mutableListOf(
            Manifest.permission.CAMERA,
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
}