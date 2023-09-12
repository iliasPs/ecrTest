package com.example.ecrtool

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ecrtool.db.AppDatabase
import com.example.ecrtool.db.ConfirmationResponseDao
import com.example.ecrtool.models.trafficEcr.ConfirmationResponse
import junit.framework.TestCase.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConfirmationResponseDaoTest {



    private lateinit var confirmationResponseDao: ConfirmationResponseDao
    private lateinit var myDatabase: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        myDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        confirmationResponseDao = myDatabase.confirmationResponseDao()
    }

    @After
    fun cleanup() {
        myDatabase.close()
    }

    @Test
    fun testInsert() = runBlocking {
        val confirmationResponse = createConfirmationResponse("receipt-1", "session-1", 10.0, "ECR-1")

        confirmationResponseDao.insert(confirmationResponse)

        val retrievedResponse = confirmationResponseDao.getByReceiptNumber("receipt-1")

        assertEquals(confirmationResponse, retrievedResponse)
    }

    @Test
    fun testGetByReceiptNumber() = runBlocking {
        val confirmationResponse1 = createConfirmationResponse("receipt-1", "session-1", 10.0, "ECR-1")
        val confirmationResponse2 = createConfirmationResponse("receipt-2", "session-2", 20.0, "ECR-2")

        confirmationResponseDao.insert(confirmationResponse1)
        confirmationResponseDao.insert(confirmationResponse2)

        val retrievedResponse = confirmationResponseDao.getByReceiptNumber("receipt-2")

        assertEquals(confirmationResponse2, retrievedResponse)
    }

    @Test
    fun testDelete() = runBlocking {
        val confirmationResponse = createConfirmationResponse("receipt-1", "session-1", 10.0, "ECR-1")

        confirmationResponseDao.insert(confirmationResponse)
        confirmationResponseDao.delete(confirmationResponse)

        val retrievedResponse = confirmationResponseDao.getByReceiptNumber("receipt-1")

        assertNull(retrievedResponse)
    }

    @Test
    fun testGetAll() = runBlocking {
        val confirmationResponse1 = createConfirmationResponse("receipt-1", "session-1", 10.0, "ECR-1")
        val confirmationResponse2 = createConfirmationResponse("receipt-2", "session-2", 20.0, "ECR-2")

        confirmationResponseDao.insert(confirmationResponse1)
        confirmationResponseDao.insert(confirmationResponse2)

        val allResponses = confirmationResponseDao.getAll()

        assertEquals(2, allResponses.size)
        assertTrue(allResponses.contains(confirmationResponse1))
        assertTrue(allResponses.contains(confirmationResponse2))
    }

    @Test
    fun testDeleteAll() = runBlocking {
        val confirmationResponse1 = createConfirmationResponse("receipt-1", "session-1", 10.0, "ECR-1")
        val confirmationResponse2 = createConfirmationResponse("receipt-2", "session-2", 20.0, "ECR-2")

        confirmationResponseDao.insert(confirmationResponse1)
        confirmationResponseDao.insert(confirmationResponse2)

        confirmationResponseDao.deleteAll()

        val allResponses = confirmationResponseDao.getAll()

        assertEquals(0, allResponses.size)
    }

    private fun createConfirmationResponse(
        receiptNumber: String,
        sessionNumber: String,
        amount: Double,
        ecrId: String
    ): ConfirmationResponse {
        return ConfirmationResponse(
            receiptNumber = receiptNumber,
            sessionNumber = sessionNumber,
            amount = amount,
            ecrId = ecrId
        )
    }
}

