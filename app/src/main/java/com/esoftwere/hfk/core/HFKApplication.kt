package com.esoftwere.hfk.core

import android.app.Application
import androidx.multidex.MultiDex
import com.esoftwere.hfk.utils.TinyDB

class HFKApplication : Application() {
    /*Create TinyDb Singleton Instance*/
    val tinyDB: TinyDB by lazy {
        TinyDB(applicationInstance)
    }

    override fun onCreate() {
        super.onCreate()
        applicationInstance = this

        MultiDex.install(applicationInstance)
    }

    companion object {
        lateinit var applicationInstance: HFKApplication
    }
}