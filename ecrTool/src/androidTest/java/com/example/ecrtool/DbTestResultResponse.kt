package com.example.ecrtool

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ecrtool.db.AppDatabase
import com.example.ecrtool.db.ResultDao
import com.example.ecrtool.models.trafficEcr.PrnData
import com.example.ecrtool.models.trafficEcr.ResultResponse
import com.example.ecrtool.models.trafficEcr.TransData
import junit.framework.TestCase.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DbTestResultResponse {
    private lateinit var myDatabase: AppDatabase
    private lateinit var resultDao: ResultDao



    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        myDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        resultDao = myDatabase.resultDao()
    }

    @After
    fun cleanup() {
        myDatabase.close()
    }

    @Test
    fun testInsertAndGetAll() = runBlocking {
        val resultResponse1 = createResultResponse("receipt-1", "session-1", 10.0, "ECR-1")
        val resultResponse2 = createResultResponse("receipt-2", "session-2", 20.0, "ECR-2")

        resultDao.insert(resultResponse1)
        resultDao.insert(resultResponse2)

        val allResultResponses = resultDao.getAll()

        assertEquals(2, allResultResponses.size)
        assertTrue(allResultResponses.contains(resultResponse1))
        assertTrue(allResultResponses.contains(resultResponse2))
    }

    @Test
    fun testDelete() = runBlocking {
        val resultResponse = createResultResponse("receipt-1", "session-1", 10.0, "ECR-1")
        resultDao.insert(resultResponse)

        resultDao.delete(resultResponse)

        val retrievedResultResponse = resultDao.getResultResponseBySessionNumber("session-1")

        assertNull(retrievedResultResponse)
    }

    // Add more test methods for other DAO operations

    // ...

    private fun createResultResponse(
        receiptNumber: String,
        sessionNumber: String,
        amount: Double,
        ecrId: String
    ): ResultResponse {
        return ResultResponse(
            receiptNumber = receiptNumber,
            sessionNumber = sessionNumber,
            amount = amount,
            ecrId = ecrId,
            _rspCode = "00",
            prnData = PrnData(data = "test"),
            transData = TransData(txnType = "00")
        )
    }
}