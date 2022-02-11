package com.crazyrockgames.galleryinst.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.ImageViewTarget
import com.luck.picture.lib.R
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.listener.OnImageCompleteCallback
import com.luck.picture.lib.tools.MediaUtils
import com.luck.picture.lib.widget.longimage.ImageSource
import com.luck.picture.lib.widget.longimage.ImageViewState
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView

/**
 * @author：Sosa_Omar
 * @date：04-02-2002
 */
class GlideEngine private constructor() : ImageEngine {
    /**
     * load image
     *
     * @param context
     * @param url
     * @param imageView
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .into(imageView)
    }

    /**
     * Loading network pictures to adapt to long picture scheme
     * # Note: This method will only be called when the network image is loaded
     *
     * @param context
     * @param url
     * @param imageView
     * @param longImageView
     * @param callback     Network image loading callback listener {link after version 2.5.1 Please use the #OnImageCompleteCallback#}
     */
    override fun loadImage(
        context: Context, url: String,
        imageView: ImageView,
        longImageView: SubsamplingScaleImageView, callback: OnImageCompleteCallback
    ) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : ImageViewTarget<Bitmap?>(imageView) {
                override fun onLoadStarted(placeholder: Drawable?) {
                    super.onLoadStarted(placeholder)
                    if (callback != null) {
                        callback.onShowLoading()
                    }
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    if (callback != null) {
                        callback.onHideLoading()
                    }
                }

                override fun setResource(resource: Bitmap?) {
                    if (callback != null) {
                        callback.onHideLoading()
                    }
                    if (resource != null) {
                        val eqLongImage = MediaUtils.isLongImg(
                            resource.width,
                            resource.height
                        )
                        longImageView.visibility = if (eqLongImage) View.VISIBLE else View.GONE
                        imageView.visibility = if (eqLongImage) View.GONE else View.VISIBLE
                        if (eqLongImage) {
                            longImageView.isQuickScaleEnabled = true
                            longImageView.isZoomEnabled = true
                            longImageView.isPanEnabled = true
                            longImageView.setDoubleTapZoomDuration(100)
                            longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                            longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                            longImageView.setImage(
                                ImageSource.bitmap(resource),
                                ImageViewState(0f, PointF(0f, 0f), 0)
                            )
                        } else {
                            imageView.setImageBitmap(resource)
                        }
                    }
                }
            })
    }

    /**
     * Loading network pictures to adapt to long picture scheme
     * # Note: This method will only be called when the network image is loaded
     *
     * @param context
     * @param url
     * @param imageView
     * @param longImageView
     * @ deprecated
     */
    override fun loadImage(
        context: Context, url: String,
        imageView: ImageView,
        longImageView: SubsamplingScaleImageView
    ) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : ImageViewTarget<Bitmap?>(imageView) {
                override fun setResource(resource: Bitmap?) {
                    if (resource != null) {
                        val eqLongImage = MediaUtils.isLongImg(
                            resource.width,
                            resource.height
                        )
                        longImageView.visibility = if (eqLongImage) View.VISIBLE else View.GONE
                        imageView.visibility = if (eqLongImage) View.GONE else View.VISIBLE
                        if (eqLongImage) {
                            longImageView.isQuickScaleEnabled = true
                            longImageView.isZoomEnabled = true
                            longImageView.isPanEnabled = true
                            longImageView.setDoubleTapZoomDuration(100)
                            longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                            longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                            longImageView.setImage(
                                ImageSource.bitmap(resource),
                                ImageViewState(0f, PointF(0f, 0f), 0)
                            )
                        } else {
                            imageView.setImageBitmap(resource)
                        }
                    }
                }
            })
    }

    /**
     * Load album directory
     *
     * @param context context
     * @param url image path
     * @param imageView holds the image ImageView
     */
    override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .override(180, 180)
            .centerCrop()
            .sizeMultiplier(0.5f)
            .apply(RequestOptions().placeholder(R.drawable.picture_image_placeholder))
            .into(object : BitmapImageViewTarget(imageView) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.resources, resource)
                    circularBitmapDrawable.cornerRadius = 8f
                    imageView.setImageDrawable(circularBitmapDrawable)
                }
            })
    }

    /**
     * load gif
     *
     * @param context context
     * @param url image path
     * @param imageView holds the image ImageView
     */
    override fun loadAsGifImage(
        context: Context, url: String,
        imageView: ImageView
    ) {
        Glide.with(context)
            .asGif()
            .load(url)
            .into(imageView)
    }

    /**
     * Load picture list picture
     *
     * @param context context
     * @param url image path
     * @param imageView holds the image ImageView
     */
    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .override(200, 200)
            .centerCrop()
            .apply(RequestOptions().placeholder(R.drawable.picture_image_placeholder))
            .into(imageView)
    }

    companion object {
        private var instance: GlideEngine? = null

        @JvmStatic
        fun createGlideEngine(): GlideEngine? {
            if (null == instance) {
                synchronized(GlideEngine::class.java) {
                    if (null == instance) {
                        instance = GlideEngine()
                    }
                }
            }
            return instance
        }
    }
}