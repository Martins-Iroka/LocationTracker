package com.martdev.android.androidtracker

import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors private constructor(
    val backgroundThread: Executor,
    val mainThread: Executor
) {

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(@NonNull command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    companion object {

        // For Singleton instantiation
        private val LOCK = Any()
        private var sInstance: AppExecutors? = null

        val instance: AppExecutors
            get() {
                if (sInstance == null) {
                    synchronized(LOCK) {
                        sInstance = AppExecutors(
                            Executors.newFixedThreadPool(3),
                            MainThreadExecutor()
                        )
                    }
                }
                return sInstance!!
            }
    }
}