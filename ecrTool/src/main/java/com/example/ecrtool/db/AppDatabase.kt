package com.example.ecrtool.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ecrtool.models.trafficEcr.AmountRequest
import com.example.ecrtool.models.trafficEcr.ConfirmationResponse
import com.example.ecrtool.models.trafficEcr.RegReceiptRequest
import com.example.ecrtool.models.trafficEcr.ResultResponse

@Database(
    entities = [ConfirmationResponse::class, AmountRequest::class, RegReceiptRequest::class, ResultResponse::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateTimeConverter::class, DoubleTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun confirmationResponseDao(): ConfirmationResponseDao
    abstract fun amountRequestDao(): AmountRequestDao
    abstract fun regReceiptRequestDao(): RegReceiptRequestDao
    abstract fun resultDao(): ResultDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}