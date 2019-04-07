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

    private var lastRequestedPage = 1
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
        lastRequestedPage = 1
        requestAndSaveData()
    }

    override fun onItemAtEndLoaded(itemAtEnd: ProductModel) {
        requestAndSaveData()
    }

    private fun requestAndSaveData() {
        if (loading) return

        appExecutors.diskIO.execute {
            if (productDao.getFirst(lastRequestedPage).isEmpty()) {
                appExecutors.mainThread.execute {
                    loading = true
                    productAPI.fetchProducts(lastRequestedPage, 30).subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.single())
                        .flatMap {
                            if(it.statusCode != 200) {
                                throw Throwable("" + it.statusCode)
                            }
                            productDao.insert(it)
                            Observable.just(it)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            loading = false
                            lastRequestedPage++
                            networkState.postValue(NetworkState(State.SUCCESS, successMessage = "" + (lastRequestedPage-1)))
                        }, {
                            networkState.postValue(NetworkState(State.ERROR, it.message))
                            loading = false
                            it.printStackTrace()
                        })
                }
            } else {
                appExecutors.mainThread.execute {
                    lastRequestedPage++
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ProductRepository? = null

        fun getInstance(): ProductRepository {
            synchronized(this) {
                if (INSTANCE == null) INSTANCE = ProductRepository()
            }
            return INSTANCE!!
        }
    }
}
