package com.example.ecrtool

import android.content.Context
import com.example.ecrtool.appData.AppData
import com.example.ecrtool.models.trafficToPos.MyEcrEftposInit
import com.example.ecrtool.models.trafficToPos.PaymentToPosResult
import com.example.ecrtool.server.MyEcrServer
import com.example.ecrtool.server.MyEcrServerSingleton
import com.example.ecrtool.utils.Logger

class EcrToPosMain(
    initObject: MyEcrEftposInit,
    context: Context
) {
    private var server: MyEcrServer
    private val applicationContext: Context = context.applicationContext

    init {
        MyEcrServerSingleton.initialize(initObject)
        server = MyEcrServerSingleton.getInstance()
        AppData.setAppData(initObject)
    }

    fun stopServer() {
        server.stop()
    }

    fun startServer() {
        server.start()
    }

    fun disconnect() {
        server.disconnect()
    }

    //get log file
    fun getLogFile() {
        Logger.shareLogFile(applicationContext)
    }

    fun sendMessageToEcr(message: Any) {
        if (message is PaymentToPosResult) {
            server.sendMessageToEcr(message)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: EcrToPosMain? = null

        fun getInstance(): EcrToPosMain {
            return INSTANCE ?: synchronized(this) {
                throw IllegalStateException("EcrToPosMain must be initialized first")
            }
        }

        fun initialize(myEcrEftposInit: MyEcrEftposInit, context: Context) {
            INSTANCE = EcrToPosMain(myEcrEftposInit, context)
        }
    }
}
