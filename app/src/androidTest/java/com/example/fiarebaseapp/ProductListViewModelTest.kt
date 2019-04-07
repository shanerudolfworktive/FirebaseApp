package com.example.fiarebaseapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.example.fiarebaseapp.mocks.MockProductListAPI
import com.example.fiarebaseapp.models.State
import com.example.fiarebaseapp.models.local.ProductDao
import com.example.fiarebaseapp.models.local.ProductsDatabase
import com.example.fiarebaseapp.repositories.ProductRepository
import com.example.fiarebaseapp.viewmodels.ProductListViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch


@RunWith(AndroidJUnit4::class)
class ProductListViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    lateinit var db: ProductsDatabase
    lateinit var dao: ProductDao

    @Before
    @Throws(Exception::class)
    fun initDb() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(), ProductsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.productDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun fetchRepoNetworkError() {
        val api = MockProductListAPI()
        api.response.statusCode = 404
        val repo = ProductRepository(productAPI = api, productDao = dao)

        val latch = CountDownLatch(1)
        val viewModel = ProductListViewModel(repo)
        viewModel.networkState.observeForever(Observer {
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
        val viewModel = ProductListViewModel(repo)
        val latch = CountDownLatch(1)
        var isInit = true
        viewModel.productModels.observeForever(Observer {
            if(isInit) assertEquals(it.size, 0)
            else assertEquals(it.size, 1)
            isInit = false
        })

        viewModel.networkState.observeForever(Observer {
            latch.countDown()
            assertEquals(it.state, State.SUCCESS)
        })

        //sync wait, so that db maintains open
        latch.await()
    }

}