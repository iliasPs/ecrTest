package com.example.ecrtool.models.trafficEcr

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class RefundRequest(

    val sessionNumber: String? = null,
    val amount: Double? = null,
    val currencyCode: String? = null,
    val curExp: String? = null,
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    val dateTime: LocalDateTime? = null, // YYYYMMDDhhmmss
    val ecrId: String? = null,
    val operatorNumber: String? = null,
    val customData: String? = null,
    val receiptId: String? = null,
    val mac: String? = null
): BaseModel()




