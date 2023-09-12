package com.example.ecrtool.models.trafficEcr

data class AckResultRequest(

    val sessionNumber: String? = null,
    val ecrId: String? = null,
    val amount: Double? = null,
    val receiptNumber: String? = null,
    val secondReceiptNumber: String? = null
): BaseModel()