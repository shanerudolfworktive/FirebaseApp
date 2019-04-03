package com.example.fiarebaseapp.repositories

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.fiarebaseapp.MainApplication
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
        pageSize = 1,
        prefetchDistance = 10
//        enablePlaceholders = false
    )
    val productModels: LiveData<PagedList<ProductModel>> = productDao.getAllProducts().toLiveData(
        config = pagingConfig,
        boundaryCallback = this
    )

    fun deleteAllProducts() {
        appExecutors.diskIO.execute {
            productDao.deleteAllProducts()
        }
    }

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        Log.d("RepoBoundaryCallback", "onZeroItemsLoaded")
        lastRequestedPage=1
        requestAndSaveData()
    }

    override fun onItemAtEndLoaded(itemAtEnd: ProductModel) {
        Log.d("RepoBoundaryCallback", "onItemAtEndLoaded")
        requestAndSaveData()
    }

    private fun requestAndSaveData() {
        if (loading) return

        appExecutors.diskIO.execute{
            if (productDao.getFirst(lastRequestedPage).isEmpty()) {
                appExecutors.mainThread.execute{
                    loading = true
                    ProductListAPI.getInstance().fetchProducts(lastRequestedPage,30).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            loading = false
                            lastRequestedPage++
                            productDao.insert(it)
                            Toast.makeText(MainApplication.appContext, "success fetch page: " + lastRequestedPage, Toast.LENGTH_SHORT).show()
                        },{
                            loading = false
                            it.printStackTrace()
                        })
                }
            }else {
                appExecutors.mainThread.execute {
                    lastRequestedPage++
                }
            }
        }
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
