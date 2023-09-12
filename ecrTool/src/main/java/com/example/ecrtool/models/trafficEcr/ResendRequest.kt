package com.example.ecrtool.models.trafficEcr

data class ResendRequest(

    val sessionNumber: String = "",
    val amount: Double = 0.0,
    val currencyCode: String = "",
    val currencyExponent: String = "",
    val ecrId: String = "",
    val receiptNumber: String = "",
    val mac: String = ""

): BaseModel()