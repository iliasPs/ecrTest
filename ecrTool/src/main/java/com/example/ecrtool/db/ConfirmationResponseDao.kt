package com.example.ecrtool.db

import androidx.room.*
import com.example.ecrtool.models.trafficEcr.ConfirmationResponse

@Dao
interface ConfirmationResponseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(response: ConfirmationResponse)

    @Query("SELECT * FROM confirmation_response WHERE receiptNumber = :receiptNumber")
    suspend fun getByReceiptNumber(receiptNumber: String): ConfirmationResponse?

    @Delete
    suspend fun delete(response: ConfirmationResponse)

    @Query("SELECT * FROM confirmation_response")
    suspend fun getAll(): List<ConfirmationResponse>

    @Query("DELETE FROM confirmation_response")
    suspend fun deleteAll()
}