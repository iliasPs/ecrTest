package com.example.ecrtool.models.trafficToPos

import com.example.ecrtool.listeners.AppMessenger
import com.example.ecrtool.models.mk.MkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

data class MyEcrEftposInit(

    val port: Int = 8080,
    val coroutineScope: CoroutineScope? = CoroutineScope(Dispatchers.IO),
    val filePath: String = "",
    val appListener: AppMessenger?, // appListener to receive requests that the module doesn't handle
    val isCoreVersion: Boolean,
    val TID: String,
    val vatNumber: String,
    val apiKey: String,
    val MAN: String,
    val appVersion: String,
    val validateMk: Boolean
)
