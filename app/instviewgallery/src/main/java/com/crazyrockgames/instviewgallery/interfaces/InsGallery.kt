package com.crazyrockgames.instviewgallery.interfaces

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.luck.picture.lib.PictureSelectionModel
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.engine.CacheResourcesEngine
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.instagram.InsGallery
import com.luck.picture.lib.instagram.InstagramSelectionConfig
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.style.PictureCropParameterStyle
import com.luck.picture.lib.style.PictureParameterStyle
import com.luck.picture.lib.style.PictureWindowAnimationStyle

/**
 * Created by Sosa Omar E. on 11/02/2022.
 */
object InsGallery {
    const val THEME_STYLE_DEFAULT = 0
    const val THEME_STYLE_DARK = 1
    const val THEME_STYLE_DARK_BLUE = 2
    var currentTheme = THEME_STYLE_DEFAULT

    private fun InsGallery() {
        throw IllegalStateException("you can't instantiate me!")
    }

    fun openGallery(
        activity: Activity,
        engine: ImageEngine?,
        listener: OnResultCallbackListener<*>?
    ) {
        openGallery(activity, engine, null, null, listener)
    }

    fun openGallery(
        activity: Activity,
        engine: ImageEngine?,
        cacheResourcesEngine: CacheResourcesEngine?,
        listener: OnResultCallbackListener<*>?
    ) {
        openGallery(activity, engine, cacheResourcesEngine, null, listener)
    }

    fun openGallery(
        activity: Activity,
        engine: ImageEngine?,
        cacheResourcesEngine: CacheResourcesEngine?,
        selectionMedia: List<LocalMedia?>?,
        listener: OnResultCallbackListener<*>?
    ) {
        applyInstagramOptions(
            activity.applicationContext, PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll())
        )
            .imageEngine(engine)
            .loadCacheResourcesCallback(cacheResourcesEngine)
            .selectionData(selectionMedia)
            .forResult(listener)
    }

    fun openGallery(
        activity: Activity,
        engine: ImageEngine?,
        cacheResourcesEngine: CacheResourcesEngine?,
        selectionMedia: List<LocalMedia?>?,
        instagramConfig: InstagramSelectionConfig?,
        listener: OnResultCallbackListener<*>?
    ) {
        applyInstagramOptions(
            activity.applicationContext, instagramConfig, PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll())
        )
            .imageEngine(engine)
            .loadCacheResourcesCallback(cacheResourcesEngine)
            .selectionData(selectionMedia)
            .forResult(listener)
    }

    fun openGallery(
        activity: Activity,
        engine: ImageEngine?,
        cacheResourcesEngine: CacheResourcesEngine?,
        selectionMedia: List<LocalMedia?>?
    ) {
        applyInstagramOptions(
            activity.applicationContext, PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll())
        )
            .imageEngine(engine)
            .loadCacheResourcesCallback(cacheResourcesEngine)
            .selectionData(selectionMedia)
            .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    fun openGallery(
        activity: Activity,
        engine: ImageEngine?,
        cacheResourcesEngine: CacheResourcesEngine?,
        selectionMedia: List<LocalMedia?>?,
        instagramConfig: InstagramSelectionConfig?
    ) {
        applyInstagramOptions(
            activity.applicationContext, instagramConfig, PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll())
        )
            .imageEngine(engine)
            .loadCacheResourcesCallback(cacheResourcesEngine)
            .selectionData(selectionMedia)
            .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    fun openGallery(
        activity: Activity,
        engine: ImageEngine?,
        cacheResourcesEngine: CacheResourcesEngine?,
        selectionMedia: List<LocalMedia?>?,
        requestCode: Int
    ) {
        applyInstagramOptions(
            activity.applicationContext, PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll())
        )
            .imageEngine(engine)
            .loadCacheResourcesCallback(cacheResourcesEngine)
            .selectionData(selectionMedia)
            .forResult(requestCode)
    }


    fun applyInstagramOptions(
        context: Context,
        selectionModel: PictureSelectionModel
    ): PictureSelectionModel {
        return applyInstagramOptions(
            context,
            InstagramSelectionConfig.createConfig().setCurrentTheme(currentTheme),
            selectionModel
        )
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun applyInstagramOptions(
        context: Context,
        instagramConfig: InstagramSelectionConfig?,
        selectionModel: PictureSelectionModel
    ): PictureSelectionModel {
        return selectionModel
            .setInstagramConfig(instagramConfig)
            .setPictureStyle(createInstagramStyle(context))
            .setPictureCropStyle(createInstagramCropStyle(context))
            .setPictureWindowAnimationStyle(PictureWindowAnimationStyle())
            .isWithVideoImage(false)
            .maxSelectNum(9)
            .minSelectNum(1)
            .maxVideoSelectNum(1)
            .imageSpanCount(4)
            .isReturnEmpty(false)
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            .selectionMode(PictureConfig.MULTIPLE)
            .isSingleDirectReturn(false)
            .isPreviewImage(true)
            .isPreviewVideo(true)
            .enablePreviewAudio(false)
            .isCamera(false)
            .isZoomAnim(true)
            .isEnableCrop(true)
            .isCompress(false)
            .synOrAsy(true)
            .withAspectRatio(1, 1)
            .showCropFrame(true)
            .showCropGrid(true)
            .isOpenClickSound(true)
            .videoMaxSecond(600)
            .videoMinSecond(3)
            .recordVideoSecond(60)
            .recordVideoMinSecond(3)
            .cutOutQuality(90)
            .minimumCompressSize(100)
    }

    fun createInstagramStyle(context: Context): PictureParameterStyle? {
        val mPictureParameterStyle = PictureParameterStyle()
        if (currentTheme == THEME_STYLE_DARK || currentTheme == THEME_STYLE_DARK_BLUE) {
            mPictureParameterStyle.isChangeStatusBarFontColor = false
        } else {
            mPictureParameterStyle.isChangeStatusBarFontColor = true
        }
        mPictureParameterStyle.isOpenCompletedNumStyle = false
        mPictureParameterStyle.isOpenCheckNumStyle = true
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.pictureStatusBarColor = Color.parseColor("#1C1C1E")
        } else if (currentTheme == THEME_STYLE_DARK_BLUE) {
            mPictureParameterStyle.pictureStatusBarColor = Color.parseColor("#213040")
        } else {
            mPictureParameterStyle.pictureStatusBarColor = Color.parseColor("#FFFFFF")
        }
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.pictureTitleBarBackgroundColor = Color.parseColor("#1C1C1E")
        } else if (currentTheme == THEME_STYLE_DARK_BLUE) {
            mPictureParameterStyle.pictureTitleBarBackgroundColor = Color.parseColor("#213040")
        } else {
            mPictureParameterStyle.pictureTitleBarBackgroundColor = Color.parseColor("#FFFFFF")
        }
        mPictureParameterStyle.pictureTitleUpResId =
            com.luck.picture.lib.R.drawable.picture_arrow_up
        mPictureParameterStyle.pictureTitleDownResId =
            com.luck.picture.lib.R.drawable.picture_arrow_down
        mPictureParameterStyle.pictureFolderCheckedDotStyle =
            com.luck.picture.lib.R.drawable.picture_orange_oval
        mPictureParameterStyle.pictureLeftBackIcon = com.luck.picture.lib.R.drawable.picture_close
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.pictureTitleTextColor =
                ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_white)
        } else if (currentTheme == THEME_STYLE_DARK_BLUE) {
            mPictureParameterStyle.pictureTitleTextColor =
                ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_white)
        } else {
            mPictureParameterStyle.pictureTitleTextColor =
                ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_white)
        }
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.pictureRightDefaultTextColor =
                ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_1766FF)
        } else if (currentTheme == THEME_STYLE_DARK_BLUE) {
            mPictureParameterStyle.pictureRightDefaultTextColor = Color.parseColor("#2FA6FF")
        } else {
            mPictureParameterStyle.pictureRightDefaultTextColor =
                ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_1766FF)
        }
        if (currentTheme == THEME_STYLE_DARK) {
            mPictureParameterStyle.pictureContainerBackgroundColor =
                ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_black)
        } else if (currentTheme == THEME_STYLE_DARK_BLUE) {
            mPictureParameterStyle.pictureContainerBackgroundColor = Color.parseColor("#18222D")
        } else {
            mPictureParameterStyle.pictureContainerBackgroundColor =
                ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_white)
        }
        mPictureParameterStyle.pictureCheckedStyle =
            com.luck.picture.lib.R.drawable.picture_instagram_num_selector
        mPictureParameterStyle.pictureBottomBgColor =
            ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_fa)
        mPictureParameterStyle.pictureCheckNumBgStyle =
            com.luck.picture.lib.R.drawable.picture_num_oval
        mPictureParameterStyle.picturePreviewTextColor =
            ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_fa632d)
        mPictureParameterStyle.pictureUnPreviewTextColor =
            ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_9b)
        mPictureParameterStyle.pictureCompleteTextColor =
            ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_fa632d)
        mPictureParameterStyle.pictureUnCompleteTextColor =
            ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_9b)
        mPictureParameterStyle.pictureExternalPreviewDeleteStyle =
            com.luck.picture.lib.R.drawable.picture_icon_black_delete
        mPictureParameterStyle.pictureExternalPreviewGonePreviewDelete = true
        mPictureParameterStyle.pictureRightDefaultText =
            context.getString(com.luck.picture.lib.R.string.next)
        return mPictureParameterStyle
    }

    fun createInstagramCropStyle(context: Context?): PictureCropParameterStyle? {
        if (currentTheme == THEME_STYLE_DARK) {
            return PictureCropParameterStyle(
                Color.parseColor("#1C1C1E"),
                Color.parseColor("#1C1C1E"),
                Color.parseColor("#1C1C1E"),
                ContextCompat.getColor(context!!, com.luck.picture.lib.R.color.picture_color_white),
                false
            )
        } else if (currentTheme == THEME_STYLE_DARK_BLUE) {
            return PictureCropParameterStyle(
                Color.parseColor("#213040"),
                Color.parseColor("#213040"),
                Color.parseColor("#213040"),
                ContextCompat.getColor(context!!, com.luck.picture.lib.R.color.picture_color_white),
                false
            )
        }
        return PictureCropParameterStyle(
            ContextCompat.getColor(context!!, com.luck.picture.lib.R.color.picture_color_white),
            ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_white),
            ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_white),
            ContextCompat.getColor(context, com.luck.picture.lib.R.color.picture_color_black),
            true
        )
    }

    @JvmName("setCurrentTheme1")
    fun setCurrentTheme(currentTheme: Int) {
        InsGallery.currentTheme = currentTheme
    }
}
