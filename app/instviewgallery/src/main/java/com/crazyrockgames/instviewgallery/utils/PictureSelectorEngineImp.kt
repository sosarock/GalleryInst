package com.crazyrockgames.instviewgallery.utils

import android.util.Log
import com.crazyrockgames.instviewgallery.utils.GlideEngine.Companion.createGlideEngine
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.engine.PictureSelectorEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener

/**
 * @author：Sosa_Omar
 * @date：04-02-2002
 */
class PictureSelectorEngineImp : PictureSelectorEngine {
    override fun createEngine(): ImageEngine {
        // TODO This is the case when the memory is extremely insufficient, such as turning on the unreserved activity or background process limit in the developer options, causing the ImageEngine to be recycled
        // Recreate the image loading engine
        return createGlideEngine()!!
    }

    override fun getResultCallbackListener(): OnResultCallbackListener<LocalMedia?> {
        return object : OnResultCallbackListener<LocalMedia?> {
            override fun onResult(result: List<LocalMedia?>) {
                // TODO This situation is when the memory is extremely low, such as turning on the unreserved activity or background process limit in the developer options, causing the OnResultCallbackListener to be recycled
                // Some remedial measures can be taken here to push the results to the corresponding page by broadcasting or other methods to prevent the loss of results
                Log.i(TAG, "onResult:" + result.size)
            }

            override fun onCancel() {
                Log.i(TAG, "PictureSelector onCancel")
            }
        }
    }

    companion object {
        private val TAG = PictureSelectorEngineImp::class.java.simpleName
    }
}