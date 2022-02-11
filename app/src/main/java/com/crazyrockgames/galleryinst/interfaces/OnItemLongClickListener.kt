package com.crazyrockgames.galleryinst.interfaces

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author：Sosa_Omar
 * @date：04-02-2002
 */
interface OnItemLongClickListener {
    fun onItemLongClick(holder: RecyclerView.ViewHolder?, position: Int, v: View?)
}