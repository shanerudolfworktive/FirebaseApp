package com.example.fiarebaseapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.example.fiarebaseapp.mocks.MockProductListAPI
import com.example.fiarebaseapp.models.local.ProductDao
import com.example.fiarebaseapp.models.local.ProductsDatabase
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class ProductsDatabaseTest {
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
    @Throws(InterruptedException::class)
    fun initialNullTest() {
        Assert.assertNull(dao.first())
        val product = dao.productById("test-id")
        Assert.assertNull(product.value?.productId, null)

    }

    @Test
    @Throws(InterruptedException::class)
    fun insertAndQueryTest() {
        val api = MockProductListAPI()
        dao.insert(api.response.products)
        val firstModel = dao.first()
        Assert.assertEquals(firstModel.productId, "test-id")

        val latch = CountDownLatch(1)
        val product = dao.productById("test-id")
        product.observeForever(Observer {
            Assert.assertEquals(it.productId, "test-id")
            latch.countDown()
        })

        //sync wait, so that db maintains open
        latch.await()
    }

    @Test
    @Throws(InterruptedException::class)
    fun deleteTest() {
        val api = MockProductListAPI()
        dao.insert(api.response.products)
        dao.deleteAllProducts()
        Assert.assertNull(dao.first())
    }
}