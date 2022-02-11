package com.crazyrockgames.galleryinst.adapter

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crazyrockgames.galleryinst.R
import com.crazyrockgames.galleryinst.interfaces.OnItemLongClickListener
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnItemClickListener
import com.luck.picture.lib.tools.DateUtils
import java.io.File

/**
 * @author：Sosa_Omar
 * @date：04-02-2002
 */
class GridImageAdapter(context: Context?, mOnAddPicClickListener: OnAddPicClickListener) :
    RecyclerView.Adapter<GridImageAdapter.ViewHolder>() {
    private lateinit var mInflater: LayoutInflater
    private var list: ArrayList<LocalMedia>? = ArrayList()
    private var selectMax = 9

    /**
     * Click to add a picture to jump
     */
    lateinit var mOnAddPicClickListener: OnAddPicClickListener

    interface OnAddPicClickListener {
        fun onAddPicClick()
    }

    /**
     * delete
     */
    fun delete(position: Int) {
        try {
            if (position != RecyclerView.NO_POSITION && list!!.size > position) {
                list!!.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, list!!.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setSelectMax(selectMax: Int) {
        this.selectMax = selectMax
    }

    fun setList(list: List<LocalMedia>?) {
        this.list = list as ArrayList<LocalMedia>?
    }

    val data: List<LocalMedia>
        get() = if (list == null) ArrayList() else list!!

    fun remove(position: Int) {
        if (list != null && position < list!!.size) {
            list!!.removeAt(position)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mImg: ImageView
        var mIvDel: ImageView
        var tvDuration: TextView

        init {
            mImg = view.findViewById(R.id.fiv)
            mIvDel = view.findViewById(R.id.iv_del)
            tvDuration = view.findViewById(R.id.tv_duration)
        }
    }

    override fun getItemCount(): Int {
        return if (list!!.size < selectMax) {
            list!!.size + 1
        } else {
            list!!.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isShowAddItem(position)) {
            TYPE_CAMERA
        } else {
            TYPE_PICTURE
        }
    }

    /**
     * Create ViewHolder
     */
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ViewHolder {
        val view = mInflater.inflate(
            R.layout.gv_filter_image,
            viewGroup, false
        )
        return ViewHolder(view)
    }

    private fun isShowAddItem(position: Int): Boolean {
        val size = if (list!!.size == 0) 0 else list!!.size
        return position == size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_CAMERA) {
            viewHolder.mImg.setImageResource(R.drawable.ic_add_image)
            viewHolder.mImg.setOnClickListener { v: View? -> mOnAddPicClickListener.onAddPicClick() }
            viewHolder.mIvDel.visibility = View.INVISIBLE
        } else {
            viewHolder.mIvDel.visibility = View.VISIBLE
            viewHolder.mIvDel.setOnClickListener { view: View? ->
                val index = viewHolder.adapterPosition
                if (index != RecyclerView.NO_POSITION && list!!.size > index) {
                    list!!.removeAt(index)
                    notifyItemRemoved(index)
                    notifyItemRangeChanged(index, list!!.size)
                }
            }
            val media = list!![position]
            if (media == null
                || TextUtils.isEmpty(media.path)
            ) {
                return
            }
            val chooseModel = media.chooseModel
            val path: String
            path = if (media.isCut && !media.isCompressed) {
                media.cutPath
            } else if (media.isCompressed || media.isCut && media.isCompressed) {
                media.compressPath
            } else if (PictureMimeType.isHasVideo(media.mimeType) && !TextUtils.isEmpty(media.coverPath)) {
                media.coverPath
            } else {
                media.path
            }
            Log.i(TAG, "Original image address::" + media.path)
            if (media.isCut) {
                Log.i(TAG, "Cut Address::" + media.cutPath)
            }
            if (media.isCompressed) {
                Log.i(TAG, "Compression address::" + media.compressPath)
                Log.i(
                    TAG,
                    "compressed file size::" + File(media.compressPath).length() / 1024 + "k"
                )
            }
            if (!TextUtils.isEmpty(media.androidQToPath)) {
                Log.i(TAG, "Android Q-specific address::" + media.androidQToPath)
            }
            if (media.isOriginal) {
                Log.i(TAG, "Whether to enable the original image function::" + true)
                Log.i(
                    TAG,
                    "Address after enabling the original image function::" + media.originalPath
                )
            }
            val duration = media.duration
            viewHolder.tvDuration.visibility =
                if (PictureMimeType.isHasVideo(media.mimeType)) View.VISIBLE else View.GONE
            if (chooseModel == PictureMimeType.ofAudio()) {
                viewHolder.tvDuration.visibility = View.VISIBLE
                viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    com.luck.picture.lib.R.drawable.picture_icon_audio,
                    0,
                    0,
                    0
                )
            } else {
                viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    com.luck.picture.lib.R.drawable.picture_icon_video,
                    0,
                    0,
                    0
                )
            }
            viewHolder.tvDuration.text = DateUtils.formatDurationTime(duration)
            if (chooseModel == PictureMimeType.ofAudio()) {
                viewHolder.mImg.setImageResource(com.luck.picture.lib.R.drawable.picture_audio_placeholder)
            } else {
                Glide.with(viewHolder.itemView.context)
                    .load(
                        if (PictureMimeType.isContent(path) && !media.isCut && !media.isCompressed) Uri.parse(
                            path
                        ) else path
                    )
                    .centerCrop()
                    .placeholder(R.color.app_color_f6)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.mImg)
            }
            if (mItemClickListener != null) {
                viewHolder.itemView.setOnClickListener { v: View? ->
                    val adapterPosition = viewHolder.adapterPosition
                    mItemClickListener!!.onItemClick(v, adapterPosition)
                }
            }
            if (mItemLongClickListener != null) {
                viewHolder.itemView.setOnLongClickListener { v: View? ->
                    val adapterPosition = viewHolder.adapterPosition
                    mItemLongClickListener!!.onItemLongClick(viewHolder, adapterPosition, v)
                    true
                }
            }
        }
    }

    private var mItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(l: OnItemClickListener?) {
        mItemClickListener = l
    }

    private var mItemLongClickListener: OnItemLongClickListener? = null
    fun setItemLongClickListener(l: OnItemLongClickListener?) {
        mItemLongClickListener = l
    }

    companion object {
        const val TAG = "PictureSelector"
        const val TYPE_CAMERA = 1
        const val TYPE_PICTURE = 2
    }

    fun GridImageAdapter(context: Context?, mOnAddPicClickListener: OnAddPicClickListener?) {
        mInflater = LayoutInflater.from(context)
        this.mOnAddPicClickListener = mOnAddPicClickListener!!
    }
}