package com.example.ecrtool.models.trafficEcr

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "confirmation_response")
data class ConfirmationResponse(
    val receiptNumber: String,
    @NonNull
    @PrimaryKey
    val sessionNumber: String,
    @ColumnInfo("confirmation_response_amount")
    val amount: Double = 0.0,
    val ecrId: String = "",
    val decimals: Int
): BaseModel()