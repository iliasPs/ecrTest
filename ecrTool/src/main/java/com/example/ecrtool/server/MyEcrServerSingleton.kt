package com.example.ecrtool.server

import com.example.ecrtool.models.trafficToPos.MyEcrEftposInit

object MyEcrServerSingleton {

    private lateinit var server: MyEcrServer

    fun getInstance(): MyEcrServer {
        if (!MyEcrServerSingleton::server.isInitialized) {
            throw IllegalStateException("MyEcrServer must be initialized first")
        }
        return server
    }

    fun initialize(myEcrEftposInit: MyEcrEftposInit) {
        server = MyEcrServer(myEcrEftposInit)
    }
}