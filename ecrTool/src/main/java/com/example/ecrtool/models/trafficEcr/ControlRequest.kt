package com.example.ecrtool.models.trafficEcr

data class ControlRequest (
    val ecrId: String? = null,
    val commandName: String? = null,
    val parameterValue: String? = null
): BaseModel()