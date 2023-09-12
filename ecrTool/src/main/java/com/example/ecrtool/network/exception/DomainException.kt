package com.example.ecrtool.network.exception

data class DomainException(
    val errorKey: String = "",
    val throwable: Throwable? = null,
    val isNetworkError: Boolean = false
): Throwable()
