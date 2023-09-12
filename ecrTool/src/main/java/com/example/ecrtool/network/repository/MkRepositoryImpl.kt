package com.example.ecrtool.network.repository

import android.util.Log
import com.example.ecrtool.models.mk.MkRequest
import com.example.ecrtool.models.mk.MkResponseModel
import com.example.ecrtool.models.mk.toModel
import com.example.ecrtool.network.api.MasterKeyApi

class MkRepositoryImpl(private val api: MasterKeyApi) : MkRepository {

    override suspend fun getMasterKey(mkRequest: MkRequest): MkResponseModel {
        return api.getMasterKey(mkRequest).let {
            Log.d("MkRepositoryImpl", it.toString())
            it.toModel()
        }
    }

}