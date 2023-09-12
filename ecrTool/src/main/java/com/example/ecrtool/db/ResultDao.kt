package com.example.ecrtool.db

import androidx.room.*
import com.example.ecrtool.models.trafficEcr.ResultResponse

@Dao
interface ResultDao {

    @Query("SELECT * FROM result_response")
    suspend fun getAll(): List<ResultResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(resultResponse: ResultResponse)

    @Delete
    suspend fun delete(resultResponse: ResultResponse)

    @Query("DELETE FROM result_response")
    suspend fun deleteAll()

    @Query("SELECT * FROM result_response WHERE sessionNumber = :sessionNumber")
    suspend fun getResultResponseBySessionNumber(sessionNumber: String): ResultResponse?
}