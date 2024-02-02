package com.example.inventorycountingapp.common.dialog

import android.app.ProgressDialog
import android.content.Context

class Loader(private val context: Context) {
    private var progressDialog: ProgressDialog? = null

    fun showLoader() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(context)
            progressDialog?.setMessage("Please wait...")
            progressDialog?.setCancelable(false)
        }

        progressDialog?.show()
    }

    fun hideLoader() {
        progressDialog?.dismiss()
    }
}
