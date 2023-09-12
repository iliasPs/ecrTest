package com.example.ecrtool.network.result

import com.example.ecrtool.network.exception.DomainException


/**
 * Executes the given [block] of code and wraps the result in a [DataResult] object.
 *
 * @param block A lambda function that produces a value of type [T].
 * @return A [DataResult] object representing the success or failure of the block's execution.
 */
inline fun <T : Any> asDataResult(block: () -> T) = try {
    DataResult.Success(block())
} catch (t: DomainException) {
    DataResult.Error(t)
}