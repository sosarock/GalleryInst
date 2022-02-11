package com.crazyrockgames.galleryinst

import android.app.Application
import android.content.Context
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.crazyrockgames.galleryinst.utils.PictureSelectorEngineImp
import com.luck.picture.lib.app.IApp
import com.luck.picture.lib.app.PictureAppMaster
import com.luck.picture.lib.crash.PictureSelectorCrashUtils
import com.luck.picture.lib.engine.PictureSelectorEngine

/**
 * Created by Sosa Omar E. on 08/02/2022.
 */
class GalleryInst : Application(), IApp, CameraXConfig.Provider {

    override fun onCreate() {
        super.onCreate()

        PictureAppMaster.getInstance().setApp(this)
        PictureSelectorCrashUtils.init { t: Thread?, e: Throwable? -> }

    }

    interface OnTimerRunning {
        fun setTimeRemaining(time: Long)
    }


    override fun getAppContext(): Context? {
        return this
    }

    override fun getPictureSelectorEngine(): PictureSelectorEngine? {
        return PictureSelectorEngineImp()
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }

}