package com.example.fiarebaseapp.models.local

import androidx.paging.DataSource
import androidx.room.*
import com.example.fiarebaseapp.MainApplication
import com.example.fiarebaseapp.models.ProductModel

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(products: List<ProductModel?>?)

    @Query("SELECT * FROM ProductTable")
    fun getAllProducts(): DataSource.Factory<Int, ProductModel>

    @Query("DELETE FROM ProductTable")
    fun deleteAllProducts()

    @Query("select * from ProductTable limit 1")
    fun first(): ProductModel
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