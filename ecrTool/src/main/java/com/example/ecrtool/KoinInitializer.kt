package com.example.ecrtool

import android.content.Context
import androidx.startup.Initializer
import com.example.ecrtool.dataHandle.ProcessFlow
import com.example.ecrtool.db.AppDatabase
import com.example.ecrtool.network.api.MasterKeyApi
import com.example.ecrtool.network.repository.MkRepository
import com.example.ecrtool.network.repository.MkRepositoryImpl
import com.example.ecrtool.network.retrofit.RetrofitHelper
import com.example.ecrtool.network.useCase.MkUseCase
import com.example.ecrtool.utils.Constants
import com.example.ecrtool.utils.Utils
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit

private val databaseModule = module {
    single { AppDatabase.getDatabase(get()) }
    single { get<AppDatabase>().regReceiptRequestDao() }
    single { get<AppDatabase>().resultDao() }
    single { get<AppDatabase>().amountRequestDao() }
    single { get<AppDatabase>().confirmationResponseDao() }
}

private val processModule = module {
    single { ProcessFlow.getInstance() }
}

private val apiModule = module {
    single {
        val contentType = "application/json".toMediaType()
        val json = Json {
            explicitNulls = false
            coerceInputValues = true
            ignoreUnknownKeys = true
        }
        Retrofit.Builder()
            .baseUrl(Constants.MK_URL)
            .client(RetrofitHelper.getOkHttpClient())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(MasterKeyApi::class.java)
    }
}

private val repositoryModule = module {
    single<MkRepository> { MkRepositoryImpl(get()) }
    single { MkUseCase(get()) }
}

@Suppress("UNUSED")
class KoinInitializer : Initializer<Koin> {

    override fun create(context: Context): Koin {
        return startKoin {
            androidContext(context)
            modules(databaseModule, apiModule, repositoryModule, processModule)
        }.koin
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}