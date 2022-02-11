package com.crazyrockgames.instviewgallery.interfaces

/**
 * @author：Sosa_Omar
 * @date：04-02-2002
 * @describe: drag and drop listener events
 */
interface DragListener {
    /**
     * Whether to drag the item to the delete place and change the color according to the state
     *
     * @param isDelete
     */
    fun deleteState(isDelete: Boolean)

    /**
     * Is it in the dragging state
     *
     * @param start
     */
    fun dragState(isStart: Boolean)
}