package com.example.fiarebaseapp.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.fiarebaseapp.models.NetworkState
import com.example.fiarebaseapp.models.ProductModel
import com.example.fiarebaseapp.models.State
import com.example.fiarebaseapp.models.local.ProductDao
import com.example.fiarebaseapp.models.local.ProductsDatabase
import com.example.fiarebaseapp.models.remote.ProductListAPI
import com.example.fiarebaseapp.utils.AppExecutors
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ProductRepository constructor(
    private val appExecutors: AppExecutors = AppExecutors.getInstance(),
    private val productAPI: ProductListAPI = ProductListAPI.getInstance(),
    private val productDao: ProductDao = ProductsDatabase.getInstance().productDao()
) : PagedList.BoundaryCallback<ProductModel>() {

    private var loading = false

    private val pagingConfig = Config(
        pageSize = 30,
        prefetchDistance = 10
    )
    val productModels: LiveData<PagedList<ProductModel>> = productDao.getAllProducts().toLiveData(
        config = pagingConfig,
        boundaryCallback = this
    )

    val networkState = MutableLiveData<NetworkState>()

    fun deleteAllProducts() {
        appExecutors.diskIO.execute {
            productDao.deleteAllProducts()
        }
    }

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        requestAndSaveData()
    }

    override fun onItemAtEndLoaded(itemAtEnd: ProductModel) {
        requestAndSaveData()
    }

    private fun requestAndSaveData() {
        if (loading) return

        loading = true
        Observable.fromCallable {
            val lastProduct = productDao.last()
            lastProduct?.pageNumber?: 0
        }
            .subscribeOn(Schedulers.io())
            .flatMap {
                productAPI.fetchProducts(it+1, 30)
            }
            .flatMap {
                if (it.statusCode != 200) throw Throwable("" + it.statusCode)
                productDao.insert(it)
                Observable.just(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                loading = false
                networkState.postValue(
                    NetworkState(
                        State.SUCCESS,
                        successMessage = "" + it.pageNumber
                    )
                )
            }, {
                networkState.postValue(NetworkState(State.ERROR, it.message))
                loading = false
                it.printStackTrace()
            })
    }

    companion object {
        private var INSTANCE: ProductRepository? = null
        fun getInstance(): ProductRepository {
            if (INSTANCE == null) INSTANCE = ProductRepository()
            return INSTANCE!!
        }
    }
}
