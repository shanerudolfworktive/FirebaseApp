package com.example.fiarebaseapp.viewmodels

import com.example.fiarebaseapp.repositories.ProductRepository

class ProductListViewModel : BaseViewModel(){
    private val repository: ProductRepository = ProductRepository.getInstance()
    val productModels = repository.productModels
}