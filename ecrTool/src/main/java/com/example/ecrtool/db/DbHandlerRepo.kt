package com.example.ecrtool.db

import com.example.ecrtool.models.trafficEcr.AmountRequest
import com.example.ecrtool.models.trafficEcr.ConfirmationResponse
import com.example.ecrtool.models.trafficEcr.RegReceiptRequest
import com.example.ecrtool.models.trafficEcr.ResultResponse

/**
 * Defines the contract for a database handler which provides methods for performing
 * CRUD operations on the database using coroutines.
 */
interface DbHandlerRepo {

    suspend fun insertAmountRequest(amountRequest: AmountRequest)
    suspend fun getAllAmountRequests(): List<AmountRequest>
    suspend fun getAmountRequestBySessionNumber(sessionNumber: String): AmountRequest?
    suspend fun deleteAmountRequest(amountRequest: AmountRequest)
    suspend fun deleteAllAmountRequests()
    suspend fun insertConfirmationResponse(confirmationResponse: ConfirmationResponse)
    suspend fun getAllConfirmationResponses(): List<ConfirmationResponse>
    suspend fun getConfirmationResponseByReceiptNumber(receiptNumber: String): ConfirmationResponse?
    suspend fun deleteConfirmationResponse(confirmationResponse: ConfirmationResponse)
    suspend fun deleteAllConfirmationResponses()
    suspend fun insertRegReceipt(receiptRequest: RegReceiptRequest)
    suspend fun getRegReceiptRequestByReceiptNumber(receiptNumber: String): RegReceiptRequest?
    suspend fun deleteRegReceiptRequest(receiptRequest: RegReceiptRequest)
    suspend fun deleteResult(resultResponse: ResultResponse)
    suspend fun insertResult(resultResponse: ResultResponse)
    suspend fun getAllResultResponses(): List<ResultResponse>
    suspend fun deleteAllResults()
    suspend fun getResultResponseBySessionNumber(receiptNumber: String): ResultResponse?

    suspend fun updateAmountRequest(amountRequest: AmountRequest)
}