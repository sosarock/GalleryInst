package com.crazyrockgames.galleryinst


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.crazyrockgames.galleryinst.adapter.GridImageAdapter
import com.crazyrockgames.galleryinst.interfaces.DragListener
import com.crazyrockgames.galleryinst.utils.GlideCacheEngine
import com.crazyrockgames.galleryinst.utils.GlideEngine
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.broadcast.BroadcastAction
import com.luck.picture.lib.broadcast.BroadcastManager
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.instagram.InsGallery
import com.luck.picture.lib.permissions.PermissionChecker
import com.luck.picture.lib.tools.PictureFileUtils
import com.luck.picture.lib.tools.ToastUtils
import java.util.*

class GalleryInstActivity : AppCompatActivity() {

    private var mAdapter: GridImageAdapter? = null
    private var needScaleBig = true
    private var needScaleSmall = true
    private var isUpward = false
    private var mItemTouchHelper: ItemTouchHelper? = null
    private var mDragListener: DragListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {

        } else {
            clearCache()
        }

        mAdapter = GridImageAdapter(this@GalleryInstActivity, onAddPicClickListener)
        if (savedInstanceState?.getParcelableArrayList<Parcelable>(
                "selectorList"
            ) != null
        ) {
            mAdapter!!.setList(savedInstanceState.getParcelableArrayList("selectorList"))
        }
        InsGallery.setCurrentTheme(InsGallery.THEME_STYLE_DEFAULT)
        InsGallery.openGallery(
            this@GalleryInstActivity,
            GlideEngine.createGlideEngine(),
            GlideCacheEngine.createCacheEngine(),
            mAdapter!!.data
        )

        mAdapter!!.setOnItemClickListener { v: View?, position: Int ->
            val selectList = mAdapter!!.data
            if (selectList.size > 0) {
                val media = selectList[position]
                val mimeType = media.mimeType
                val mediaType = PictureMimeType.getMimeType(mimeType)
                when (mediaType) {
                    PictureConfig.TYPE_VIDEO ->
                        PictureSelector.create(this@GalleryInstActivity)
                            .themeStyle(com.luck.picture.lib.R.style.picture_default_style)
                            .externalPictureVideo(media.path)
                    PictureConfig.TYPE_AUDIO ->
                        PictureSelector.create(this@GalleryInstActivity)
                            .externalPictureAudio(if (PictureMimeType.isContent(media.path)) media.androidQToPath else media.path)
                    else ->
                        PictureSelector.create(this@GalleryInstActivity)
                            .themeStyle(com.luck.picture.lib.R.style.picture_default_style)
                            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                            .isNotPreviewDownload(true)
                            .imageEngine(GlideEngine.createGlideEngine())
                            .openExternalPreview(position, selectList)
                }
            }
        }

        mItemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val itemViewType = viewHolder.itemViewType
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    viewHolder.itemView.alpha = 0.7f
                }
                return makeMovementFlags(
                    ItemTouchHelper.DOWN or ItemTouchHelper.UP
                            or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0
                )
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                try {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition
                    val itemViewType = target.itemViewType
                    if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                        if (fromPosition < toPosition) {
                            for (i in fromPosition until toPosition) {
                                Collections.swap(mAdapter!!.data, i, i + 1)
                            }
                        } else {
                            for (i in fromPosition downTo toPosition + 1) {
                                Collections.swap(mAdapter!!.data, i, i - 1)
                            }
                        }
                        mAdapter!!.notifyItemMoved(fromPosition, toPosition)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return true
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemViewType = viewHolder.itemViewType
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    if (null == mDragListener) {
                        return
                    }
                    if (needScaleBig) {
                        viewHolder.itemView.animate().scaleXBy(0.1f).scaleYBy(0.1f).duration =
                            100
                        needScaleBig = false
                        needScaleSmall = false
                    }

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

            override fun onSelectedChanged(
                viewHolder: RecyclerView.ViewHolder?,
                actionState: Int
            ) {
                val itemViewType = viewHolder?.itemViewType ?: GridImageAdapter.TYPE_CAMERA
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    if (ItemTouchHelper.ACTION_STATE_DRAG == actionState && mDragListener != null) {
                        (mDragListener as DragListener).dragState(true)
                    }
                    super.onSelectedChanged(viewHolder, actionState)
                }
            }

            override fun getAnimationDuration(
                recyclerView: RecyclerView,
                animationType: Int,
                animateDx: Float,
                animateDy: Float
            ): Long {
                needScaleSmall = true
                isUpward = true
                return super.getAnimationDuration(
                    recyclerView,
                    animationType,
                    animateDx,
                    animateDy
                )
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                val itemViewType = viewHolder.itemViewType
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    viewHolder.itemView.alpha = 1.0f
                    super.clearView(recyclerView, viewHolder)
                    mAdapter!!.notifyDataSetChanged()
                    resetState()
                }
            }
        })

        BroadcastManager.getInstance(this@GalleryInstActivity).registerReceiver(
            broadcastReceiver,
            BroadcastAction.ACTION_DELETE_PREVIEW_POSITION
        )
    }


    private fun resetState() {
        if (mDragListener != null) {
            mDragListener!!.deleteState(false)
            mDragListener!!.dragState(false)
        }
        isUpward = false
    }

    private fun clearCache() {
        if (PermissionChecker.checkSelfPermission(
                getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            PictureFileUtils.deleteAllCacheDirFile(getContext())
        } else {
            PermissionChecker.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    for (media in selectList) {
                        //  file.add(media)
                    }
                    //entitylocalMedia = selectList
                    //mAdapter!!.setList(selectList)
                    //mAdapter!!.notifyDataSetChanged()
                }
            }
        } else {
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE -> {
                var i = 0
                while (i < grantResults.size) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        PictureFileUtils.deleteCacheDirFile(getContext(), PictureMimeType.ofImage())
                    } else {
                        Toast.makeText(
                            this@GalleryInstActivity,
                            "Read memory card access denied", Toast.LENGTH_SHORT
                        ).show()
                    }
                    i++
                }
            }
        }
    }

    private val onAddPicClickListener = object : GridImageAdapter.OnAddPicClickListener {
        override fun onAddPicClick() {
            InsGallery.setCurrentTheme(InsGallery.THEME_STYLE_DEFAULT)
            InsGallery.openGallery(
                this@GalleryInstActivity,
                GlideEngine.createGlideEngine(),
                GlideCacheEngine.createCacheEngine(),
                mAdapter!!.data
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mAdapter != null && mAdapter!!.data != null && mAdapter!!.data.size > 0) {
            outState.putParcelableArrayList(
                "selectorList",
                mAdapter!!.data as ArrayList<out Parcelable?>
            )
        }
    }

    private val broadcastReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val extras: Bundle?
            when (action) {
                BroadcastAction.ACTION_DELETE_PREVIEW_POSITION -> {
                    extras = intent.extras
                    val position = extras!!.getInt(PictureConfig.EXTRA_PREVIEW_DELETE_POSITION)
                    ToastUtils.s(getContext(), "delete image index:$position")
                    if (position < mAdapter!!.data.size) {
                        mAdapter!!.remove(position)
                        mAdapter!!.notifyItemRemoved(position)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (broadcastReceiver != null) {
            BroadcastManager.getInstance(getContext()).unregisterReceiver(
                broadcastReceiver,
                BroadcastAction.ACTION_DELETE_PREVIEW_POSITION
            )
        }
    }

    fun getContext(): Context? {
        return this
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}