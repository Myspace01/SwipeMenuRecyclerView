package com.umeng.myrecyclerview.touch;


import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Elson on 2017/8/18.
 */

public interface OnItemMovementListener
{
    int INVALID = 0;

    int LEFT = ItemTouchHelper.LEFT;

    int UP = ItemTouchHelper.UP;

    int RIGHT = ItemTouchHelper.RIGHT;

    int DOWN = ItemTouchHelper.DOWN;

    /**
     * Can drag and drop the ViewHolder?
     *
     * @param recyclerView     {@link RecyclerView}.
     * @param targetViewHolder target ViewHolder.
     * @return use {@link #LEFT}, {@link #UP}, {@link #RIGHT}, {@link #DOWN}.
     */
    int onDragFlags(RecyclerView recyclerView, RecyclerView.ViewHolder targetViewHolder);

    /**
     * Can swipe and drop the ViewHolder?
     *
     * @param recyclerView     {@link RecyclerView}.
     * @param targetViewHolder target ViewHolder.
     * @return use {@link #LEFT}, {@link #UP}, {@link #RIGHT}, {@link #DOWN}.
     */
    int onSwipeFlags(RecyclerView recyclerView, RecyclerView.ViewHolder targetViewHolder);
}
