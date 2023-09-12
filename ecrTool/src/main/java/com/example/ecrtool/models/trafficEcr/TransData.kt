package com.example.ecrtool.models.trafficEcr

import androidx.room.ColumnInfo

data class TransData(
    val cardType: String? = null,
    val txnType: String? = null,
    val cardPanMasked: String? = null,
    val amountFinal: Double? = null,
    @ColumnInfo("transData_amount")
    val amount: Double? = null,
    val amountTip: Double? = null,
    val amountLoyalty: Double? = null,
    val amountCashBack: Double? = null,
    val bankId: String? = null,
    val terminalId: String? = null,
    val batchNumber: String? = null,
    val rrn: String? = null,
    val stan: String? = null,
    val authCode: String? = null,
    val transactionDateTime: String? = null,
    val transactionEcrStatus: String = "",
) {

    companion object {
        private val validTxnTypes = mapOf(
            "00" to "sale",
            "01" to "cancellation",
            "02" to "refund",
            "03" to "preapproval",
            "04" to "mail order",
            "05" to "sale with installments"
        )
    }

    init {
        if (!validTxnTypes.containsKey(txnType)) {
            throw IllegalArgumentException("Invalid txnType: $txnType. Allowed values are ${validTxnTypes.keys}.")
        }
    }
}

