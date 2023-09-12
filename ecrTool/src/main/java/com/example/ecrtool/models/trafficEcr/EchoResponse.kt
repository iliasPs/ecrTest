package com.example.ecrtool.models.trafficEcr

data class EchoResponse (
    val text: String? = null,
    val terminalId: String? = null,
    val appVersion: String? = null,
    val isInit: Boolean = false
): BaseModel()
