package com.example.ecrtool.network.repository

import com.example.ecrtool.models.mk.MkRequest
import com.example.ecrtool.models.mk.MkResponse
import com.example.ecrtool.models.mk.MkResponseModel
import com.example.ecrtool.network.result.DataResult

interface MkRepository {

    suspend fun getMasterKey(mkRequest: MkRequest): MkResponseModel
}