package com.example.fiarebaseapp.models.remote

import com.example.fiarebaseapp.consts.Constants
import com.example.fiarebaseapp.models.ProductResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductListAPI {
    @GET("/walmartproducts/{pageNumber}/{pageSize}")
    fun fetchProducts(
        @Path(value = "pageNumber", encoded = true) pageNumber: Int,
        @Path(value = "pageSize", encoded = true) pageSize: Int
    ): Observable<ProductResponse>

    companion object {
        @Volatile
        private var INSTANCE: ProductListAPI? = null

        fun getInstance(): ProductListAPI {
            synchronized(this) {
                val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(Constants.BASE_URL)
                    .build()
                INSTANCE = retrofit.create(ProductListAPI::class.java)
            }
            return INSTANCE!!
        }
    }
}