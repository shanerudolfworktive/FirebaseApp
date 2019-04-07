package com.example.fiarebaseapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.fiarebaseapp.mocks.MockProductDao
import com.example.fiarebaseapp.repositories.ProductDetailRepository
import com.example.fiarebaseapp.viewmodels.ProductDetailViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import java.util.concurrent.CountDownLatch

class ProductDetailViewModelTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun fetchByIdTest() {
        val dao = MockProductDao()
        val repo = ProductDetailRepository(dao=dao)

        val viewModel = ProductDetailViewModel(repo)

        val latch = CountDownLatch(1)

        viewModel.productModel.observeForever(Observer {
            Assert.assertEquals(it.productId, "test-id")
            latch.countDown()
        })
        viewModel.productById("test-id")
        latch.await()
    }
}