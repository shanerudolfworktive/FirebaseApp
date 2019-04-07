package com.example.fiarebaseapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class ProductResponse constructor(
    val totalProducts: Int?,
    val pageNumber: Int?,
    val pageSize: Int?,
    var statusCode: Int?,
    val products: List<ProductModel>?
)

@Entity(tableName = "ProductTable")
data class ProductModel constructor(
    @PrimaryKey val productId: String,
    val productName: String? = "",
    val shortDescription: String? = "",
    val longDescription: String? = "",
    val price: String? = "",
    val productImage: String? = "",
    val reviewRating: Float?,
    val reviewCount: Int?,
    val inStock: Boolean?,
    var pageNumber: Int?,
    var insertTime: Long
)