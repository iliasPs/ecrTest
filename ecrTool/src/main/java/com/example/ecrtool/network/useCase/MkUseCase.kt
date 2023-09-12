package com.example.ecrtool.network.useCase

import android.util.Log
import com.example.ecrtool.models.mk.MkRequest
import com.example.ecrtool.models.mk.MkResponseModel
import com.example.ecrtool.network.exception.DomainException
import com.example.ecrtool.network.repository.MkRepository
import com.example.ecrtool.network.result.DataResult
import com.example.ecrtool.network.result.asDataResult

open class MkUseCase(private val mkRepository: MkRepository) {

    open suspend fun invoke(mkRequest: MkRequest): DataResult<MkResponseModel> {
        return asDataResult {
            val rsp = mkRepository.getMasterKey(mkRequest)
            Log.d("MkUseCase", rsp.toString())
            if (rsp.status == "000") {
                rsp
            } else {
                throw DomainException(rsp.status)
            }
        }
    }
}