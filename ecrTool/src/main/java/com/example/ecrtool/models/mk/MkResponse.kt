package com.example.ecrtool.models.mk

import com.example.ecrtool.utils.Utils.nonNull
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MkResponse(
    @SerialName("Status")
    val status: String = "",
    @SerialName("Description")
    val description: String = "",
    @SerialName("TID")
    val tId: String = "",
    @SerialName("MACKEY")
    val mKey: String = ""
)

fun MkResponse.toModel(): MkResponseModel {
    return MkResponseModel(
        status = this.status.nonNull(),
        description = this.description.nonNull(),
        TID = this.tId.nonNull(),
        MACKEY = this.mKey.nonNull()
    )
}


