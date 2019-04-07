package com.example.fiarebaseapp.mocks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.fiarebaseapp.models.ProductModel
import com.example.fiarebaseapp.models.local.ProductDao

class MockProductDao : ProductDao(){
    val product = ProductModel(
        "test-id",
        "test-name",
        "test-sortDesc",
        "test-longDesc",
        "test-price",
        "test-image",
        10f,
        15,
        true,
        20,
        50
    )

    override fun insert(products: List<ProductModel?>?) {

    }

    override fun getFirst(pageNumber: Int): List<ProductModel> {
        return arrayListOf()
    }

    override fun getAllProducts(): DataSource.Factory<Int, ProductModel> {
        return null!!
    }

    override fun deleteAllProducts() {
    }

    override fun first(): ProductModel {
        return product
    }

    override fun productById(productId: String): LiveData<ProductModel> {
        val liveData = MutableLiveData<ProductModel>()
        liveData.postValue(product)
        return liveData
    }
}