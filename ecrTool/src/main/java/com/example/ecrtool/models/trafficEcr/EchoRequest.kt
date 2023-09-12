package com.example.ecrtool.models.trafficEcr

data class EchoRequest(
    val text: String? = null,
    val ecrNumber: String? = null,
    val isInit: Boolean = false
): BaseModel()
