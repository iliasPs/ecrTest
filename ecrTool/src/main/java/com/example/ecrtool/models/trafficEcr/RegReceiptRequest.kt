package com.example.ecrtool.models.trafficEcr

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.ecrtool.db.LocalDateTimeConverter
import java.time.LocalDateTime

@Entity(tableName = "reg_receipt_requests", primaryKeys = ["sessionNumber"])
data class RegReceiptRequest(
    val sessionNumber: String,
    val amount: Double? = null,
    val curCode: String? = null,
    val curExp: String? = null,
    val ecrId: String? = null,
    @TypeConverters(LocalDateTimeConverter::class)
    val dateTime: LocalDateTime? = null,
    val operatorNumber: String? = null,
    val receiptNumber: String = "",
    val customData: String? = null,
    val mac: String? = null
) : BaseModel()