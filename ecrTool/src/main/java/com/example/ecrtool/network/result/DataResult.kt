package com.example.ecrtool.network.result

import com.example.ecrtool.network.exception.DomainException

/**
 * Represents the result of a data operation that can either succeed or fail.
 * This is a sealed class with two subclasses: Success and Error.
 * The Success subclass is used when the data operation succeeds and carries the result data.
 * The Error subclass is used when the data operation fails and carries the cause of the failure.
 *
 * @param <ResultType> The type of the result data.
 */
sealed class DataResult<out ResultType> {

    val isSuccess get() = this is Success
    val isError get() = this is Error

    data class Success<ResultType : Any>(
        val data: ResultType
    ) : DataResult<ResultType>()

    data class Error(
        val cause: DomainException,
        val errorKey: String = cause.errorKey,

        ) : DataResult<Nothing>()
}