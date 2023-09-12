package com.example.ecrtool.models.trafficEcr

import androidx.room.Ignore

open class BaseModel(
    @Ignore
    var messageSize: String = "",
    @Ignore
    var direction: String = "",
    @Ignore
    var protocolVariant: String = "",
    @Ignore
    var protocolVersion: String = ""
)