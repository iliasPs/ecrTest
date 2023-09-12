package com.example.ecrtool.models.trafficEcr

//this can be in the request and in the response, the difference is its value
data class Error(
    val errorCode: String? = null
): BaseModel()