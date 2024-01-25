package com.example.inventorycountingapp.common

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

object DeviceUtils {

    fun getDeviceIMEI(activity: Activity): String {
        return Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
    }
}
