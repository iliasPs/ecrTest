package com.example.ecrtool.models.dbRelated

import androidx.room.Entity

@Entity(tableName = "transaction_data")
data class TransactionData (
    val receiptNumber: String = "",
    val amount: String = ""
)