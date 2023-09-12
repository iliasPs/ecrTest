package com.example.ecrtool

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ecrtool.db.AmountRequestDao
import com.example.ecrtool.db.AppDatabase
import com.example.ecrtool.models.trafficEcr.AmountRequest
import junit.framework.TestCase.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.Month

class DbTestAmount {

    private lateinit var myDatabase: AppDatabase
    private lateinit var amountRequestDao: AmountRequestDao


    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        myDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        amountRequestDao = myDatabase.amountRequestDao()
    }

    @After
    fun cleanup() {
        myDatabase.close()
    }

    @Test
    fun testInsertAndGetAll() = runBlocking {
        val amountRequest1 = createAmountRequest(
            "receipt-1",
            "session-1",
            10.0,
            1,
            "$",
            2,
            LocalDateTime.of(2023, Month.JANUARY, 1, 12, 0, 0),
            "ECR-1",
            "123",
            "data",
            "mac",
            false
        )
        val amountRequest2 = createAmountRequest(
            "receipt-2",
            "session-2",
            20.0,
            2,
            "$",
            2,
            LocalDateTime.of(2023, Month.JANUARY, 1, 12, 0, 0),
            "ECR-2",
            "456",
            "data",
            "mac",
            false
        )

        amountRequestDao.insert(amountRequest1)
        amountRequestDao.insert(amountRequest2)

        val allAmountRequests = amountRequestDao.getAll()

        assertEquals(2, allAmountRequests.size)
        assertTrue(allAmountRequests.contains(amountRequest1))
        assertTrue(allAmountRequests.contains(amountRequest2))
    }

    @Test
    fun testGetBySessionNumber() = runBlocking {
        val localDate = LocalDateTime.of(2023, Month.JANUARY, 1, 12, 0, 0)
        val localDate2 = LocalDateTime.of(2023, Month.JANUARY, 1, 12, 0, 1)

        val amountRequest1 = createAmountRequest("receipt-1", "session-1", 10.0, 1, "$", 2, localDate, "ECR-1", "123", "data", "mac", false)
        val amountRequest2 = createAmountRequest("receipt-2", "session-2", 20.0, 2, "$", 2, localDate2, "ECR-2", "456", "data", "mac", false)

        amountRequestDao.insert(amountRequest1)
        amountRequestDao.insert(amountRequest2)

        val retrievedAmountRequest = amountRequestDao.getBySessionNumber("session-1")

        assertNotNull(retrievedAmountRequest)
        assertEquals("receipt-1", retrievedAmountRequest?.receiptNumber)
        assertEquals("session-1", retrievedAmountRequest?.sessionNumber)
        assertEquals(10.0, retrievedAmountRequest?.amount)
        assertEquals(1, retrievedAmountRequest?.currencyCode)
        assertEquals("$", retrievedAmountRequest?.currencySymbol)
        assertEquals(2, retrievedAmountRequest?.decimals)
        assertEquals("ECR-1", retrievedAmountRequest?.ecrId)
        assertEquals("123", retrievedAmountRequest?.operatorNumber)
        assertEquals("data", retrievedAmountRequest?.customData)
        assertEquals("mac", retrievedAmountRequest?.mac)
        assertFalse(retrievedAmountRequest?.completed ?: true)
    }

    @Test
    fun testDelete() = runBlocking {
        val amountRequest = createAmountRequest(
            "receipt-1",
            "session-1",
            10.0,
            1,
            "$",
            2,
            LocalDateTime.now(),
            "ECR-1",
            "123",
            "data",
            "mac",
            false
        )
        amountRequestDao.insert(amountRequest)

        amountRequestDao.delete(amountRequest)

        val retrievedAmountRequest = amountRequestDao.getBySessionNumber("session-1")

        assertNull(retrievedAmountRequest)
    }

    // Add more test methods for other DAO operations

    // ...

    private fun createAmountRequest(
        receiptNumber: String,
        sessionNumber: String,
        amount: Double,
        currencyCode: Int,
        currencySymbol: String,
        decimals: Int,
        dateTime: LocalDateTime,
        ecrId: String,
        operatorNumber: String,
        customData: String,
        mac: String,
        completed: Boolean
    ): AmountRequest {
        return AmountRequest(
            receiptNumber = receiptNumber,
            sessionNumber = sessionNumber,
            amount = amount,
            currencyCode = currencyCode,
            currencySymbol = currencySymbol,
            decimals = decimals,
            dateTime = dateTime,
            ecrId = ecrId,
            operatorNumber = operatorNumber,
            customData = customData,
            mac = mac,
            completed = completed
        )
    }
}