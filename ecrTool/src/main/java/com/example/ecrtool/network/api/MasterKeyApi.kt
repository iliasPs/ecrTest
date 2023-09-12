package com.example.ecrtool.network.api

import com.example.ecrtool.models.mk.MkRequest
import com.example.ecrtool.models.mk.MkResponse
import com.example.ecrtool.network.result.DataResult
import retrofit2.http.Body
import retrofit2.http.POST

interface MasterKeyApi {

    @POST("https://www1.aade.gr/tameiakes/mysec/eftposmk.php")
    suspend fun getMasterKey(@Body mkRequest: MkRequest): MkResponse

}