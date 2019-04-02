package com.example.fiarebaseapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.fiarebaseapp.models.ProductModel
import com.example.fiarebaseapp.repositories.ProductDetailRepository

class ProductDetailViewModel(
    private val repository: ProductDetailRepository = ProductDetailRepository.getInstance()
) : BaseViewModel(){
    val productId = MutableLiveData<String>()
    val productModel: LiveData<ProductModel> = Transformations.switchMap(productId){
        repository.productById(it)
    }

    fun productById(productId: String) {
        this.productId.postValue(productId)
    }
}