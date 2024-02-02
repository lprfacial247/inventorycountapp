package com.example.inventorycountingapp.common

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.inventorycountingapp.app.MyApplication
import com.example.inventorycountingapp.product.ProductResponse
import com.example.inventorycountingapp.product.TransformedData

fun String.toast() {
    Toast.makeText(MyApplication.appContext, this, Toast.LENGTH_SHORT).show()
}

fun ImageView.load(imageUrl: String) {
    Glide.with(this.context).load(imageUrl).into(this)
}

fun ImageView.load(imageUri: Uri) {
    Glide.with(this.context).load(imageUri).into(this)
}

fun ImageView.load(imageUrl: String, errorDrawable: Int) {
    Glide.with(this.context)
        .load(imageUrl)
        .error(errorDrawable)
        .placeholder(errorDrawable)
        .into(this)
}

fun convertToTransformedList(dataList: List<ProductResponse.Data>): List<TransformedData> {
    return dataList.map { data ->
        TransformedData(
            product_idx = data.index,
            quantity = data.defaultQty.toFloatOrNull() ?: 0.0f
        )
    }
}