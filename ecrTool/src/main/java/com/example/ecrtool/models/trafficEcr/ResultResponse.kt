package com.example.ecrtool.models.trafficEcr

import androidx.room.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@Entity(tableName = "result_response")
data class ResultResponse(
    val receiptNumber: String,
    @PrimaryKey
    val sessionNumber: String,
    @ColumnInfo("confirmation_response_amount")
    val amount: Double,
    val ecrId: String = "",
    private val _rspCode: String = "",
    @Embedded
    val prnData: PrnData? = null,
    @Embedded
    val transData: TransData? = null,
    val decimals: Int = 2,
    val customData: String = ""
) : BaseModel() {
    val rspCode: String
        get() = _rspCode ?: ""

    init {
        if (!RESPONSE_CODES.containsKey(_rspCode)) {
            throw IllegalArgumentException("Invalid response code: $_rspCode")
        }
    }

    companion object {
        private val RESPONSE_CODES = mapOf(
            "00" to "success",
            "33" to "rejected",
            "03" to "user canceled or timeout",
            "04" to "declined by terminal",
            "05" to "declined by the host",
            "06" to "communication problem",
            "09" to "bank host unreachable",
            "66" to "system error in EFTPOS"
        )
    }
}