package com.example.ecrtool.db

import androidx.room.Room
import com.example.ecrtool.models.trafficEcr.AmountRequest
import com.example.ecrtool.models.trafficEcr.ConfirmationResponse
import com.example.ecrtool.models.trafficEcr.RegReceiptRequest
import com.example.ecrtool.models.trafficEcr.ResultResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class DbHandler : DbHandlerRepo, KoinComponent {

    private val appDatabase: AppDatabase = Room.databaseBuilder(
        get(),
        AppDatabase::class.java, "my-database"
    ).build()

    companion object {

        @Volatile
        private var INSTANCE: DbHandler? = null

        fun getInstance(): DbHandler {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = DbHandler()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    override suspend fun insertAmountRequest(amountRequest: AmountRequest) {
        withContext(Dispatchers.IO) {
            appDatabase.amountRequestDao().addNewEntry(amountRequest)
        }
    }

    override suspend fun getAllAmountRequests(): List<AmountRequest> {
        return withContext(Dispatchers.IO) {
            appDatabase.amountRequestDao().getAll()
        }
    }

    override suspend fun getAmountRequestBySessionNumber(sessionNumber: String): AmountRequest? {
        return withContext(Dispatchers.IO) {
            appDatabase.amountRequestDao().getBySessionNumber(sessionNumber)
        }
    }

    override suspend fun deleteAmountRequest(amountRequest: AmountRequest) {
        withContext(Dispatchers.IO) {
            appDatabase.amountRequestDao().delete(amountRequest)
        }
    }

    override suspend fun deleteAllAmountRequests() {
        withContext(Dispatchers.IO) {
            appDatabase.amountRequestDao().deleteAll()
        }
    }

    override suspend fun insertConfirmationResponse(confirmationResponse: ConfirmationResponse) {
        withContext(Dispatchers.IO) {
            appDatabase.confirmationResponseDao().insert(confirmationResponse)
        }
    }

    override suspend fun getAllConfirmationResponses(): List<ConfirmationResponse> {
        return withContext(Dispatchers.IO) {
            appDatabase.confirmationResponseDao().getAll()
        }
    }

    override suspend fun getConfirmationResponseByReceiptNumber(receiptNumber: String): ConfirmationResponse? {
        return withContext(Dispatchers.IO) {
            appDatabase.confirmationResponseDao().getByReceiptNumber(receiptNumber)
        }
    }

    override suspend fun deleteConfirmationResponse(confirmationResponse: ConfirmationResponse) {
        withContext(Dispatchers.IO) {
            appDatabase.confirmationResponseDao().delete(confirmationResponse)
        }
    }

    override suspend fun deleteAllConfirmationResponses() {
        withContext(Dispatchers.IO) {
            appDatabase.confirmationResponseDao().deleteAll()
        }
    }

    override suspend fun insertRegReceipt(receiptRequest: RegReceiptRequest) {
        withContext(Dispatchers.IO) {
            appDatabase.regReceiptRequestDao().insertRegReceipt(receiptRequest)
        }
    }

    override suspend fun getRegReceiptRequestByReceiptNumber(receiptNumber: String): RegReceiptRequest? {
        return withContext(Dispatchers.IO) {
            appDatabase.regReceiptRequestDao().getRegReceiptRequestByReceiptNumber(receiptNumber)
        }
    }

    override suspend fun deleteRegReceiptRequest(receiptRequest: RegReceiptRequest) {
        withContext(Dispatchers.IO) {
            appDatabase.regReceiptRequestDao().deleteRegReceiptRequest(receiptRequest)
        }
    }

    override suspend fun deleteResult(resultResponse: ResultResponse) {
        withContext(Dispatchers.IO) {
            appDatabase.resultDao().delete(resultResponse)
        }
    }

    override suspend fun insertResult(resultResponse: ResultResponse) {
        withContext(Dispatchers.IO) {
            appDatabase.resultDao().insert(resultResponse)
        }
    }

    override suspend fun getAllResultResponses(): List<ResultResponse> {
        return withContext(Dispatchers.IO) {
            appDatabase.resultDao().getAll()
        }
    }

    override suspend fun deleteAllResults() {
        withContext(Dispatchers.IO) {
            appDatabase.resultDao().deleteAll()
        }
    }

    override suspend fun getResultResponseBySessionNumber(receiptNumber: String): ResultResponse? {
        return withContext(Dispatchers.IO) {
            appDatabase.resultDao().getResultResponseBySessionNumber(receiptNumber)
        }
    }

    override suspend fun updateAmountRequest(amountRequest: AmountRequest) {
        withContext(Dispatchers.IO) {
            appDatabase.amountRequestDao().update(amountRequest)
        }
    }
}