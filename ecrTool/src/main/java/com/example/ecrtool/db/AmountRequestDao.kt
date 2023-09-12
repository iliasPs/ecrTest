package com.example.ecrtool.db

import androidx.room.*
import com.example.ecrtool.models.trafficEcr.AmountRequest

@Dao
interface AmountRequestDao {

    companion object{
        const val MAX_ENTRIES = 1000
    }
    @Query("SELECT COUNT(*) FROM amount_request")
    suspend fun getEntryCount(): Int

    @Query("SELECT * FROM amount_request")
    suspend fun getAll(): List<AmountRequest>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(amountRequest: AmountRequest)

    @Delete
    suspend fun delete(amountRequest: AmountRequest)

    @Query("SELECT * FROM amount_request WHERE sessionNumber = :sessionNumber")
    suspend fun getBySessionNumber(sessionNumber: String): AmountRequest?

    @Query("DELETE FROM amount_request")
    suspend fun deleteAll()

    suspend fun addNewEntry(amountRequest: AmountRequest) {
        val currentCount = getEntryCount()

        if (currentCount < MAX_ENTRIES) {
            insert(amountRequest)
        } else {
            throw MaxEntryCountException(MAX_ENTRIES)
        }
    }

    @Update
    suspend fun update(amountRequest: AmountRequest)
}