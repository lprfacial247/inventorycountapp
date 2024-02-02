package com.example.inventorycountingapp.common.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.inventorycountingapp.R

class NoProductDialog(context: Context, val title: String, val barCode: String, val description: String) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(R.layout.dialog_no_product)

        val titleTextView: TextView = findViewById(R.id.dialogTitle)
        val dialogBarCode: TextView = findViewById(R.id.dialogBarCode)
        val dialogDescription: TextView = findViewById(R.id.dialogDescription)
        val closeButton: Button = findViewById(R.id.dialogButton)

        // Customize dialog content
        titleTextView.text = title
        dialogBarCode.text = barCode
        dialogDescription.text = description

        closeButton.setOnClickListener {
            dismiss()
        }
    }
}
