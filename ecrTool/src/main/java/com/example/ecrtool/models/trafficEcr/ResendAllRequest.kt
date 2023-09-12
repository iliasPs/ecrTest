package com.example.ecrtool.models.trafficEcr

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

//when i get this i need to return all results

data class ResendAllRequest(
    val ecrId: String? = null,
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    val dateTime: LocalDateTime? = null, // YYYYMMDDhhmmss
    val mac: String? = null
): BaseModel()