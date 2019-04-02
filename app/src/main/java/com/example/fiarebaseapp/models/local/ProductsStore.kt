package com.example.fiarebaseapp.models.local

import androidx.paging.DataSource
import androidx.room.*
import com.example.fiarebaseapp.MainApplication
import com.example.fiarebaseapp.models.ProductModel
import com.example.fiarebaseapp.models.ProductResponse
import com.example.fiarebaseapp.utils.AppExecutors

@Dao
abstract class ProductDao {

    fun insert(productResponse: ProductResponse) {
        for(product: ProductModel in productResponse.products.orEmpty()) {
            product.pageNumber = productResponse.pageNumber
        }
        AppExecutors.getInstance().diskIO.execute {
            insert(productResponse.products)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(products: List<ProductModel?>?)

    @Query("SELECT * FROM ProductTable")
    abstract fun getAllProducts(): DataSource.Factory<Int, ProductModel>

    @Query("select * from ProductTable where pageNumber = :pageNumber limit 1")
    abstract fun getFirst(pageNumber: Int): List<ProductModel>

    @Query("DELETE FROM ProductTable")
    abstract fun deleteAllProducts()

    @Query("select * from ProductTable limit 1")
    abstract fun first(): ProductModel

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