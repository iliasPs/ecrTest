package com.example.ecrtool.db

import androidx.room.*
import com.example.ecrtool.models.trafficEcr.RegReceiptRequest

@Dao
interface RegReceiptRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegReceipt(receiptRequest: RegReceiptRequest)

    @Query("SELECT * FROM reg_receipt_requests WHERE receiptNumber = :receiptNumber")
    suspend fun getRegReceiptRequestByReceiptNumber(receiptNumber: String): RegReceiptRequest?

    @Delete
    suspend fun deleteRegReceiptRequest(receiptRequest: RegReceiptRequest)
}