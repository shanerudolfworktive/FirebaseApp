package com.example.fiarebaseapp.models.remote

import com.example.fiarebaseapp.models.ProductResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductListAPI {
    @GET("walmartproducts/{pageNumber}/{pageSize}")
    fun fetchProducts(
        @Path(value = "pageNumber", encoded = true) pageNumber: Int,
        @Path(value = "pageSize", encoded = true) pageSize: Int
    ): Observable<ProductResponse>

    companion object {
        private var INSTANCE: ProductListAPI? = null
        private val lock = Any()

        fun getInstance(): ProductListAPI {
            synchronized(lock) {
                val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://mobile-tha-server.firebaseapp.com/")
                    .build()
                INSTANCE = retrofit.create(ProductListAPI::class.java)
            }
            return INSTANCE!!
        }
    }
}