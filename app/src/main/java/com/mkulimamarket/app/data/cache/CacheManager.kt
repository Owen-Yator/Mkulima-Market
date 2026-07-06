package com.mkulimamarket.app.data.cache

import android.content.Context
import java.io.File

class CacheManager(
    private val context: Context
) {

    private val cacheFileName = "prices_cache.csv"

    /**
     * Returns cached CSV file if it exists
     */
    fun getCacheFile(): File {
        return File(context.filesDir, cacheFileName)
    }

    /**
     * Save CSV string into internal storage
     */
    fun saveCache(csv: String) {
        val file = getCacheFile()
        file.writeText(csv)
    }

    /**
     * Read cached CSV if available
     */
    fun readCache(): String? {
        val file = getCacheFile()

        return if (file.exists()) {
            file.readText()
        } else {
            null
        }
    }

    /**
     * Check if cache exists
     */
    fun hasCache(): Boolean {
        return getCacheFile().exists()
    }

    /**
     * Clear cache manually (for refresh button)
     */
    fun clearCache() {
        val file = getCacheFile()
        if (file.exists()) file.delete()
    }
}