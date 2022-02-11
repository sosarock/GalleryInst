package com.crazyrockgames.galleryinst.utils

import android.content.Context
import com.crazyrockgames.galleryinst.interfaces.ImageCacheUtils
import com.luck.picture.lib.engine.CacheResourcesEngine
import java.io.File

/**
 * @author：Sosa_Omar
 * @date：04-02-2002
 */
class GlideCacheEngine private constructor() : CacheResourcesEngine {
    override fun onCachePath(context: Context, url: String): String {
        val cacheFile: File?
        cacheFile = if (GLIDE_VERSION >= 4) {
            // Glide 4.x
            ImageCacheUtils.getCacheFileTo4x(context, url)
        } else {
            // Glide 3.x
            ImageCacheUtils.getCacheFileTo3x(context, url)
        }
        return if (cacheFile != null) cacheFile.absolutePath else ""
    }

    companion object {
        /**
         * glide Version number, please refer to user integration. This is just a simulation
         */
        private const val GLIDE_VERSION = 4
        private var instance: GlideCacheEngine? = null
        fun createCacheEngine(): GlideCacheEngine? {
            if (null == instance) {
                synchronized(GlideCacheEngine::class.java) {
                    if (null == instance) {
                        instance = GlideCacheEngine()
                    }
                }
            }
            return instance
        }
    }
}