package com.example.inventorycountingapp.common

import android.widget.Toast
import com.example.inventorycountingapp.app.MyApplication

fun String.toast() {
    Toast.makeText(MyApplication.appContext, this, Toast.LENGTH_SHORT).show()
}