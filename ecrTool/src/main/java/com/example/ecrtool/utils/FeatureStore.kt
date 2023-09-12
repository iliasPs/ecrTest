package com.example.ecrtool.utils

object FeatureStore {
    private var coreVersion: Boolean = false

    fun setVersion(isCore: Boolean) {
        coreVersion = isCore
    }

    fun isCoreVersion(): Boolean {
        return coreVersion
    }
}

