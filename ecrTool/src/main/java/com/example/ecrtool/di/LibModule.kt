//package com.example.ecrtool.di
//
//import android.content.Context
//import com.example.ecrtool.db.*
//import com.example.ecrtool.network.api.MasterKeyApi
//import com.example.ecrtool.network.repository.MkRepository
//import com.example.ecrtool.network.repository.MkRepositoryImpl
//import com.example.ecrtool.network.retrofit.RetrofitHelper
//import com.example.ecrtool.network.useCase.MkUseCase
//import com.example.ecrtool.utils.Constants
//import com.example.ecrtool.utils.Utils
//import com.google.gson.Gson
//import com.google.gson.GsonBuilder
//import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import kotlinx.serialization.ExperimentalSerializationApi
//import kotlinx.serialization.json.Json
//import okhttp3.MediaType.Companion.toMediaType
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object LibModule {
//
//    @Provides
//    fun provideApplicationContext(context: Context): Context {
//        return context.applicationContext
//    }
//
//    @Provides
//    @Singleton
//    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
//        return AppDatabase.getDatabase(context)
//    }
//
//    @Provides
//    @Singleton
//    fun provideRegReceiptDao(database: AppDatabase): RegReceiptRequestDao {
//        return database.regReceiptRequestDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideResultDao(database: AppDatabase): ResultDao {
//        return database.resultDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideAmountRequestDao(database: AppDatabase): AmountRequestDao {
//        return database.amountRequestDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideConfirmationResponseDao(database: AppDatabase): ConfirmationResponseDao {
//        return database.confirmationResponseDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideMKRepository(api: MasterKeyApi): MkRepository {
//        return MkRepositoryImpl(api)
//    }
//
//    @Provides
//    @Singleton
//    fun provideAADEApi(): MasterKeyApi {
//        val contentType = "application/json".toMediaType()
//        val gson: Gson = GsonBuilder()
//            .setLenient()
//            .create()
//
//        return Retrofit.Builder()
//            .baseUrl(Constants.MK_URL)
//            .client(RetrofitHelper.getOkHttpClient())
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()
//            .create(MasterKeyApi::class.java)
//    }
//
//
//    @Provides
//    @Singleton
//    fun provideMkUseCase(mkRepository: MkRepository): MkUseCase {
//        return MkUseCase(mkRepository)
//    }
//
//}