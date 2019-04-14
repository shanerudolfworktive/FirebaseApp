package com.example.fiarebaseapp.models.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.example.fiarebaseapp.MainApplication
import com.example.fiarebaseapp.models.ProductModel
import com.example.fiarebaseapp.models.ProductResponse

@Dao
abstract class ProductDao {

    fun insert(productResponse: ProductResponse) {
        for(product: ProductModel in productResponse.products.orEmpty()) {
            product.pageNumber = productResponse.pageNumber
            product.insertTime = System.currentTimeMillis()
        }
        insert(productResponse.products)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(products: List<ProductModel?>?)

    @Query("SELECT * FROM ProductTable ORDER BY insertTime")
    abstract fun getAllProducts(): DataSource.Factory<Int, ProductModel>

    @Query("select * from ProductTable where pageNumber = :pageNumber limit 1")
    abstract fun getFirst(pageNumber: Int): List<ProductModel>

    @Query("DELETE FROM ProductTable")
    abstract fun deleteAllProducts()

    @Query("select * from ProductTable limit 1")
    abstract fun first(): ProductModel

    @Query("select * from ProductTable ORDER BY pageNumber DESC limit 1")
    abstract fun last(): ProductModel?

    @Query("select * from ProductTable where productId=:productId")
    abstract fun productById(productId: String): LiveData<ProductModel>

}

@Database(entities = [ProductModel::class], version = 1)
abstract class ProductsDatabase: RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: ProductsDatabase ? = null

        fun getInstance(): ProductsDatabase {
            synchronized(this){
                if(INSTANCE == null) INSTANCE = buildDatabase()
            }
            return INSTANCE!!
        }

        private fun buildDatabase() =
            Room.databaseBuilder(MainApplication.appContext, ProductsDatabase::class.java, "ProductsDatabase.db")
                .fallbackToDestructiveMigration()
                .build()

    }
}