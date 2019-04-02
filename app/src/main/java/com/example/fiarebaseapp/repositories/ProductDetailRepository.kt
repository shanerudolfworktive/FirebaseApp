package com.example.fiarebaseapp.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.fiarebaseapp.models.ProductModel
import com.example.fiarebaseapp.models.local.ProductDao
import com.example.fiarebaseapp.models.local.ProductsDatabase
import com.example.fiarebaseapp.utils.AppExecutors

class ProductDetailRepository(
    private val appExecutors: AppExecutors = AppExecutors.getInstance(),
    private val dao: ProductDao = ProductsDatabase.getInstance().productDao()
) {
    fun productById(productId: String):LiveData<ProductModel> {
        return dao.productById(productId)
    }

    companion object {
        @Volatile
        private var INSTANCE: ProductDetailRepository? = null
        fun getInstance(): ProductDetailRepository{
            synchronized(this){
                if(INSTANCE == null) INSTANCE = ProductDetailRepository()
            }
            return INSTANCE!!
        }
    }
}