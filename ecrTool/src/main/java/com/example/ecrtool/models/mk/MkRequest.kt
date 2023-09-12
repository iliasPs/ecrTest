package com.example.ecrtool.models.mk

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MkRequest(

    @SerialName("TID") // Terminal Identification Number (EFTPOS)
    val tId: String = "",
    @SerialName("ECRID") // Fiscal device number from ECR ECHO call
    val EcrId: String = "",
    @SerialName("TAXID") // ΑΦΜ επιχείρησης
    val afm: String = "",
    @SerialName("MAN")
    val man: String = "",
    @SerialName("APIKEY")
    val apiKey: String = ""
)


