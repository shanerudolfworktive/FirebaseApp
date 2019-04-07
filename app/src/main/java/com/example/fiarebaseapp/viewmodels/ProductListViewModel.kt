package com.example.fiarebaseapp.viewmodels

import com.example.fiarebaseapp.repositories.ProductRepository

class ProductListViewModel(
    private val repository: ProductRepository = ProductRepository.getInstance()
) : BaseViewModel(){

    val productModels = repository.productModels
    val networkState = repository.networkState

    fun deleteAllProducts() {
        repository.deleteAllProducts()
    }

}