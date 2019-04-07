package com.example.fiarebaseapp.repositories

import androidx.lifecycle.LiveData
import com.example.fiarebaseapp.models.ProductModel
import com.example.fiarebaseapp.models.local.ProductDao
import com.example.fiarebaseapp.models.local.ProductsDatabase

class ProductDetailRepository(
    private val dao: ProductDao = ProductsDatabase.getInstance().productDao()
) {
    fun productById(productId: String):LiveData<ProductModel> {
        return dao.productById(productId)
    }

    companion object {
        private var INSTANCE: ProductDetailRepository? = null
        fun getInstance(): ProductDetailRepository{
            if(INSTANCE == null) INSTANCE = ProductDetailRepository()
            return INSTANCE!!
        }
    }
}