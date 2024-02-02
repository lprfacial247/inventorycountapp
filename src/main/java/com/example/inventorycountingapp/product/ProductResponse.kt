package com.example.inventorycountingapp.product


import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("age_restriction")
        val ageRestriction: Int,
        @SerializedName("b_active")
        val bActive: Int,
        @SerializedName("b_age_restriction")
        val bAgeRestriction: Int,
        @SerializedName("b_changed_price")
        val bChangedPrice: Int,
        @SerializedName("b_include_tax")
        val bIncludeTax: Int,
        @SerializedName("b_low_stock")
        val bLowStock: Int,
        @SerializedName("b_markup_fix")
        val bMarkupFix: Int,
        @SerializedName("barcode")
        val barcode: String,
        @SerializedName("category_idx")
        val categoryIdx: Int,
        @SerializedName("color_id")
        val colorId: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("default_qty")
        var defaultQty: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("image_name")
        val imageName: String,
        @SerializedName("image_path")
        val imagePath: String,
        @SerializedName("index")
        val index: Int,
        @SerializedName("low_stock_qty")
        val lowStockQty: String,
        @SerializedName("main_unit_idx")
        val mainUnitIdx: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("preferred_qty")
        val preferredQty: String,
        @SerializedName("prod_markup")
        val prodMarkup: String,
        @SerializedName("purchase_price")
        val purchasePrice: String,
        @SerializedName("reorder_qty")
        val reorderQty: String,
        @SerializedName("sale_price")
        val salePrice: String,
        @SerializedName("sale_price_tax")
        val salePriceTax: String,
        @SerializedName("sale_unit_idx")
        val saleUnitIdx: Int,
        @SerializedName("supplier_idx")
        val supplierIdx: Int,
        @SerializedName("tax_idx")
        val taxIdx: Int,
        @SerializedName("tax_price")
        val taxPrice: String,
        @SerializedName("unpack_size")
        val unpackSize: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}