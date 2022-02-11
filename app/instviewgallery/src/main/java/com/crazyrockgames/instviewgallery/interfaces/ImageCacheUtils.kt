package com.crazyrockgames.instviewgallery.interfaces

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import java.io.File
import java.lang.Exception

/**
 * @author：Sosa_Omar
 * @date：04-02-2002
 */
object ImageCacheUtils {
    /**
     * Get image cache based on url
     * Glide 4.x please call this method
     * Note: This method must be performed in a child thread
     *
     * @param context
     * @param url
     * @return
     */
    fun getCacheFileTo4x(context: Context?, url: String?): File? {
        return try {
            Glide.with(context!!).downloadOnly().load(url).submit().get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Get image cache based on url
     * Glide 3.x please call this method
     * Note: This method must be performed in a child thread
     *
     * @param context
     * @param url
     * @return
     */
    fun getCacheFileTo3x(context: Context?, url: String?): File? {
        return try {
            Glide.with(context!!).load(url).downloadOnly(SIZE_ORIGINAL, SIZE_ORIGINAL)
                .get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}