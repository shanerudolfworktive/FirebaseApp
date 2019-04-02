package com.example.fiarebaseapp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.fiarebaseapp.models.ProductModel
import com.example.fiarebaseapp.models.local.ProductsDatabase
import com.example.fiarebaseapp.models.remote.ProductListAPI
import com.example.fiarebaseapp.utils.AppExecutors
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ProductRepository private constructor(
    private val appExecutors: AppExecutors = AppExecutors.getInstance(),
    private val productAPI: ProductListAPI = ProductListAPI.getInstance(),
    private val productsDatabase: ProductsDatabase = ProductsDatabase.getInstance()
): PagedList.BoundaryCallback<ProductModel>(){

    val productDao = productsDatabase.productDao()
    private var lastRequestedPage = 1
    private var loading = false

    private val pagingConfig = Config(
        pageSize = 2,
        prefetchDistance = 30
//        enablePlaceholders = false
    )
    val productModels: LiveData<PagedList<ProductModel>> = productDao.getAllProducts().toLiveData(
        config = pagingConfig,
        boundaryCallback = this
    )

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        Log.d("RepoBoundaryCallback", "onZeroItemsLoaded")
        requestAndSaveData()
    }

    override fun onItemAtEndLoaded(itemAtEnd: ProductModel) {
        Log.d("RepoBoundaryCallback", "onItemAtEndLoaded")
        requestAndSaveData()
    }

    private fun requestAndSaveData() {
        if (loading) return

        loading = true
        ProductListAPI.getInstance().fetchProducts(lastRequestedPage,30).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                loading = false
                lastRequestedPage++
                appExecutors.diskIO.execute{
                    productDao.insert(it.products)
                    Log.e("testing2", Gson().toJson(productDao.first()))
                }
                Log.e("testing", Gson().toJson(it))
            },{
                loading = false
                it.printStackTrace()
            })
    }

    companion object {
        @Volatile
        private var INSTANCE: ProductRepository? = null
        fun getInstance(): ProductRepository{
            synchronized(this){
                if(INSTANCE == null) INSTANCE = ProductRepository()
            }
            return INSTANCE!!
        }
    }
}
