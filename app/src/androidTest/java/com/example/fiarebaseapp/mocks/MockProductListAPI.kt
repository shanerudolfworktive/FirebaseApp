package com.example.fiarebaseapp.mocks

import com.example.fiarebaseapp.models.ProductModel
import com.example.fiarebaseapp.models.ProductResponse
import com.example.fiarebaseapp.models.remote.ProductListAPI
import io.reactivex.Observable

class MockProductListAPI(
    var response: ProductResponse = ProductResponse(
        totalProducts = 0,
        pageNumber = 0,
        pageSize = 0,
        statusCode = 200,
        products = arrayListOf(
            ProductModel(
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
        )
    )
): ProductListAPI {

    override fun fetchProducts(pageNumber: Int, pageSize: Int): Observable<ProductResponse> {
        return Observable.just(response)
    }
}