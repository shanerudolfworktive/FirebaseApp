package com.example.fiarebaseapp.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors constructor(
    val diskIO: Executor = DiskIOThreadExecutor(),
    val networkIO: Executor = Executors.newFixedThreadPool(3),
    val mainThread: Executor = MainThreadExecutor()
){
    companion object {
        @Volatile
        private var INSTANCE: AppExecutors? = null
        fun getInstance(): AppExecutors{
            synchronized(this) {
                if(INSTANCE == null) INSTANCE = AppExecutors()
            }
            return INSTANCE!!
        }
    }
}

private class DiskIOThreadExecutor : Executor {
    private val diskIO = Executors.newSingleThreadExecutor()
    override fun execute(p0: Runnable?) {
        diskIO.execute(p0)
    }
}

private class MainThreadExecutor : Executor{
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    override fun execute(p0: Runnable?) {
        mainThreadHandler.post(p0)
    }
}