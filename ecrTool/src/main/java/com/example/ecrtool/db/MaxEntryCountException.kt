package com.example.ecrtool.db

/**
 * Exception thrown when the maximum entry count is reached.
 * The maximum entry count specifies the limit of entries allowed in a certain context.
 */
class MaxEntryCountException(maxEntries: Int) : Exception("Maximum entry count reached: $maxEntries") {
    override fun toString(): String {
        return "MaxEntryCountException: ${super.message}"
    }
}