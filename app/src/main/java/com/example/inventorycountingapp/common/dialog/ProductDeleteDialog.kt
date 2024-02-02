package com.example.inventorycountingapp.common.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.inventorycountingapp.R
import com.example.inventorycountingapp.common.load
import com.example.inventorycountingapp.product.ProductResponse

class ProductDeleteDialog(
    context: Context,
    val item: ProductResponse.Data,
    var onDelete: (ProductResponse.Data) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(R.layout.dialog_product_delete)

        val ivProduct: ImageView = findViewById(R.id.ivProduct)
        val tvName: TextView = findViewById(R.id.tvName)
        val tvCode: TextView = findViewById(R.id.tvCode)
        val closeButton: ImageView = findViewById(R.id.dialogButton)

        ivProduct.load(item.imagePath, R.drawable.ic_default_item)
        tvName.text = item.name
        tvCode.text = item.barcode

        closeButton.setOnClickListener {
            onDelete.invoke(item)
            dismiss()
        }
    }
}
