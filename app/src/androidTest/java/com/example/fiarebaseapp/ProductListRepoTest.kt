package com.example.fiarebaseapp

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.example.fiarebaseapp.models.ProductModel
import com.example.fiarebaseapp.models.ProductResponse
import com.example.fiarebaseapp.models.State
import com.example.fiarebaseapp.models.local.ProductDao
import com.example.fiarebaseapp.models.local.ProductsDatabase
import com.example.fiarebaseapp.models.remote.ProductListAPI
import com.example.fiarebaseapp.repositories.ProductRepository
import com.google.gson.Gson
import io.reactivex.Observable
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.lang.Error
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class ProductListRepoTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    lateinit var db: ProductsDatabase
    lateinit var dao: ProductDao

    @Before
    @Throws(Exception::class)
    fun initDb() {
        Log.e("database open", "database open")
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(), ProductsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.productDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        Log.e("database closed", "database closed")
        db.close()
    }

    @Test
    @Throws(InterruptedException::class)
    fun initialNullCheck() {
        assertNull(dao.first())
    }

    @Test
    @Throws(InterruptedException::class)
    fun insert() {
        dao.insert(arrayListOf(
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
        ))
        Log.e("testing 4", "dao.getAllProducts()=" + Gson().toJson(dao.getAllProducts()))
    }

    class MockProductListAPI : ProductListAPI {
        var response = ProductResponse(
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
        override fun fetchProducts(pageNumber: Int, pageSize: Int): Observable<ProductResponse> {
            return Observable.just(response)
        }
    }

    @Test
    fun fetchRepoNetworkError() {
        val api = MockProductListAPI()
        api.response.statusCode = 404
        val repo = ProductRepository(productAPI = api, productDao = dao)

        val latch = CountDownLatch(1)

        repo.networkState.observeForever(Observer {
            assertEquals(it.state, State.ERROR)
            latch.countDown()
        })

        repo.onZeroItemsLoaded()

        //sync wait, so that db maintains open
        latch.await()

    }

    @Test
    fun fetchRepoSuccess() {
        val repo = ProductRepository(productAPI = MockProductListAPI(), productDao = dao)

        val latch = CountDownLatch(1)
        var isInit = true
        repo.productModels.observeForever(Observer {
            if(isInit) assertEquals(it.size, 0)
            else assertEquals(it.size, 1)
            isInit = false
        })

        repo.networkState.observeForever(Observer {
            latch.countDown()
            assertEquals(it.state, State.SUCCESS)
        })

        //sync wait, so that db maintains open
        latch.await()
    }

}