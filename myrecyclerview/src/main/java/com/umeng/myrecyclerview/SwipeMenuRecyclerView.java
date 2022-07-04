package com.umeng.myrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.RecyclerView;

import com.umeng.myrecyclerview.touch.DefaultItemTouchHelper;
import com.umeng.myrecyclerview.touch.OnItemMoveListener;
import com.umeng.myrecyclerview.touch.OnItemMovementListener;
import com.umeng.myrecyclerview.touch.OnItemStateChangedListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elson on 2017/8/18.
 */

public class SwipeMenuRecyclerView extends RecyclerView
{
    /**
     * Left menu.
     */
    public static final int LEFT_DIRECTION = 1;
    /**
     * Right menu.
     */
    public static final int RIGHT_DIRECTION = -1;

    @IntDef({LEFT_DIRECTION, RIGHT_DIRECTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DirectionMode
    {
    }

    /**
     * Invalid position.
     */
    private static final int INVALID_POSITION = -1;

    protected int mScaleTouchSlop;
    protected SwipeMenuLayout mOldSwipedLayout;
    protected int mOldTouchedPosition = INVALID_POSITION;

    private int mDownX;
    private int mDownY;

    private boolean allowSwipeDelete = false;

    private DefaultItemTouchHelper mDefaultItemTouchHelper;

    private SwipeMenuCreator mSwipeMenuCreator;
    private SwipeMenuItemClickListener mSwipeMenuItemClickListener;
    private SwipeItemClickListener mSwipeItemClickListener;

    private SwipeAdapterWrapper mAdapterWrapper;

    public SwipeMenuRecyclerView(Context context)
    {
        this(context, null);
    }

    public SwipeMenuRecyclerView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public SwipeMenuRecyclerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mScaleTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    private void initializeItemTouchHelper()
    {
        if (mDefaultItemTouchHelper == null)
        {
            mDefaultItemTouchHelper = new DefaultItemTouchHelper();
            mDefaultItemTouchHelper.attachToRecyclerView(this);
        }
    }

    /**
     * Set OnItemMoveListener.
     *
     * @param onItemMoveListener {@link OnItemMoveListener}.
     */
    public void setOnItemMoveListener(OnItemMoveListener onItemMoveListener)
    {
        initializeItemTouchHelper();
        mDefaultItemTouchHelper.setOnItemMoveListener(onItemMoveListener);
    }

    /**
     * Set OnItemMovementListener.
     *
     * @param onItemMovementListener {@link OnItemMovementListener}.
     */
    public void setOnItemMovementListener(OnItemMovementListener onItemMovementListener)
    {
        initializeItemTouchHelper();
        mDefaultItemTouchHelper.setOnItemMovementListener(onItemMovementListener);
    }

    /**
     * Set OnItemStateChangedListener.
     *
     * @param onItemStateChangedListener {@link OnItemStateChangedListener}.
     */
    public void setOnItemStateChangedListener(OnItemStateChangedListener onItemStateChangedListener)
    {
        initializeItemTouchHelper();
        this.mDefaultItemTouchHelper.setOnItemStateChangedListener(onItemStateChangedListener);
    }

    /**
     * Set can long press drag.
     *
     * @param canDrag drag true, otherwise is can't.
     */
    public void setLongPressDragEnabled(boolean canDrag)
    {
        initializeItemTouchHelper();
        mDefaultItemTouchHelper.setLongPressDragEnabled(canDrag);
    }

    /**
     * Get can long press drag.
     *
     * @return drag true, otherwise is can't.
     */
    public boolean isLongPressDragEnabled()
    {
        initializeItemTouchHelper();
        return this.mDefaultItemTouchHelper.isLongPressDragEnabled();
    }


    /**
     * Set can swipe delete.
     *
     * @param canSwipe swipe true, otherwise is can't.
     */
    public void setItemViewSwipeEnabled(boolean canSwipe)
    {
        initializeItemTouchHelper();
        allowSwipeDelete = canSwipe; // swipe and menu conflict.
        mDefaultItemTouchHelper.setItemViewSwipeEnabled(canSwipe);
    }

    /**
     * Get can long press swipe.
     *
     * @return swipe true, otherwise is can't.
     */
    public boolean isItemViewSwipeEnabled()
    {
        initializeItemTouchHelper();
        return this.mDefaultItemTouchHelper.isItemViewSwipeEnabled();
    }

    /**
     * Start drag a item.
     *
     * @param viewHolder the ViewHolder to start dragging. It must be a direct child of RecyclerView.
     */
    public void startDrag(ViewHolder viewHolder)
    {
        initializeItemTouchHelper();
        mDefaultItemTouchHelper.startDrag(viewHolder);
    }

    /**
     * Star swipe a item.
     *
     * @param viewHolder the ViewHolder to start swiping. It must be a direct child of RecyclerView.
     */
    public void startSwipe(ViewHolder viewHolder)
    {
        initializeItemTouchHelper();
        mDefaultItemTouchHelper.startSwipe(viewHolder);
    }

    /**
     * Set to create menu listener.
     */
    public void setSwipeMenuCreator(SwipeMenuCreator swipeMenuCreator)
    {
        this.mSwipeMenuCreator = swipeMenuCreator;
    }

    /**
     * Set to click menu listener.
     */
    public void setSwipeMenuItemClickListener(SwipeMenuItemClickListener swipeMenuItemClickListener)
    {
        this.mSwipeMenuItemClickListener = swipeMenuItemClickListener;
    }

    /**
     * Set item click listener.
     */
    public void setSwipeItemClickListener(SwipeItemClickListener swipeItemClickListener)
    {
        this.mSwipeItemClickListener = swipeItemClickListener;
    }

    /**
     * Default swipe menu creator.
     */
    private SwipeMenuCreator mDefaultMenuCreator = new SwipeMenuCreator()
    {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType)
        {
            if (mSwipeMenuCreator != null)
            {
                mSwipeMenuCreator.onCreateMenu(swipeLeftMenu, swipeRightMenu, viewType);
            }
        }
    };

    /**
     * Default swipe menu item click listener.
     */
    private SwipeMenuItemClickListener mDefaultMenuItemClickListener = new SwipeMenuItemClickListener()
    {

        @Override
        public void onItemClick(SwipeMenuBridge menuBridge)
        {
            if (mSwipeMenuItemClickListener != null)
            {
                int position = menuBridge.getAdapterPosition();
                position = position - getHeaderItemCount();
                if (position >= 0)
                {
                    menuBridge.mAdapterPosition = position;
                }
                mSwipeMenuItemClickListener.onItemClick(menuBridge);
            }
        }
    };

    /**
     * Default item click listener.
     */
    private SwipeItemClickListener mDefaultItemClickListener = new SwipeItemClickListener()
    {
        @Override
        public void onItemClick(View itemView, int position)
        {
            if (mSwipeItemClickListener != null)
            {
                position = position - getHeaderItemCount();
                if (position >= 0)
                {
                    mSwipeItemClickListener.onItemClick(itemView, position);
                }
            }
        }
    };

    /**
     * Get the original adapter.
     */
    public Adapter getOriginAdapter()
    {
        if (mAdapterWrapper == null)
        {
            return null;
        }
        return mAdapterWrapper.getOriginAdapter();
    }

    @Override
    public void setAdapter(Adapter adapter)
    {
        if (mAdapterWrapper != null)
        {
            if (mAdapterWrapper.getOriginAdapter() == adapter)
            {
                adapter.notifyDataSetChanged();
                return;
            }

            mAdapterWrapper.getOriginAdapter().unregisterAdapterDataObserver(mAdapterDataObserver);
        }

        adapter.registerAdapterDataObserver(mAdapterDataObserver);

        mAdapterWrapper = new SwipeAdapterWrapper(adapter);
        mAdapterWrapper.setSwipeMenuCreator(mDefaultMenuCreator);
        mAdapterWrapper.setSwipeMenuItemClickListener(mDefaultMenuItemClickListener);
        mAdapterWrapper.setSwipeItemClickListener(mDefaultItemClickListener);
        super.setAdapter(mAdapterWrapper);

        if (mHeaderViewList.size() > 0)
        {
            for (View view : mHeaderViewList)
            {
                mAdapterWrapper.addHeaderView(view);
            }
        }
        if (mFooterViewList.size() > 0)
        {
            for (View view : mFooterViewList)
            {
                mAdapterWrapper.addFooterView(view);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        if (mAdapterWrapper != null)
        {
            mAdapterWrapper.getOriginAdapter().unregisterAdapterDataObserver(mAdapterDataObserver);
        }
        super.onDetachedFromWindow();
    }

    private AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver()
    {
        @Override
        public void onChanged()
        {
            mAdapterWrapper.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount)
        {
            positionStart += getHeaderItemCount();
            mAdapterWrapper.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload)
        {
            positionStart += getHeaderItemCount();
            mAdapterWrapper.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount)
        {
            positionStart += getHeaderItemCount();
            mAdapterWrapper.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount)
        {
            positionStart += getHeaderItemCount();
            mAdapterWrapper.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount)
        {
            fromPosition += getHeaderItemCount();
            toPosition += getHeaderItemCount();
            mAdapterWrapper.notifyItemMoved(fromPosition, toPosition);
        }
    };

    private List<View> mHeaderViewList = new ArrayList<>();
    private List<View> mFooterViewList = new ArrayList<>();

    /**
     * Add view at the top.
     */
    public void addHeaderView(View view)
    {
        if (mAdapterWrapper != null)
        {
            throw new IllegalStateException("Cannot add header view, setAdapter has already been called.");
        }
        mHeaderViewList.add(view);
    }

    /**
     * Add view at the bottom.
     */
    public void addFooterView(View view)
    {
        if (mAdapterWrapper != null)
        {
            throw new IllegalStateException("Cannot add footer view, setAdapter has already been called.");
        }
        mFooterViewList.add(view);
    }

    /**
     * Get size of headers.
     */
    public int getHeaderItemCount()
    {
        if (mAdapterWrapper == null)
        {
            return 0;
        }
        return mAdapterWrapper.getHeaderItemCount();
    }

    /**
     * Get size of footer.
     */
    public int getFooterItemCount()
    {
        if (mAdapterWrapper == null)
        {
            return 0;
        }
        return mAdapterWrapper.getFooterItemCount();
    }

    /**
     * Get ViewType of item.
     */
    public int getItemViewType(int position)
    {
        if (mAdapterWrapper == null)
        {
            return 0;
        }
        return mAdapterWrapper.getItemViewType(position);
    }

    /**
     * open menu on left.
     *
     * @param position position.
     */
    public void smoothOpenLeftMenu(int position)
    {
        smoothOpenMenu(position, LEFT_DIRECTION, SwipeMenuLayout.DEFAULT_SCROLLER_DURATION);
    }

    /**
     * open menu on left.
     *
     * @param position position.
     * @param duration time millis.
     */
    public void smoothOpenLeftMenu(int position, int duration)
    {
        smoothOpenMenu(position, LEFT_DIRECTION, duration);
    }

    /**
     * open menu on right.
     *
     * @param position position.
     */
    public void smoothOpenRightMenu(int position)
    {
        smoothOpenMenu(position, RIGHT_DIRECTION, SwipeMenuLayout.DEFAULT_SCROLLER_DURATION);
    }

    /**
     * open menu on right.
     *
     * @param position position.
     * @param duration time millis.
     */
    public void smoothOpenRightMenu(int position, int duration)
    {
        smoothOpenMenu(position, RIGHT_DIRECTION, duration);
    }

    /**
     * open menu.
     *
     * @param position  position.
     * @param direction use {@link #LEFT_DIRECTION}, {@link #RIGHT_DIRECTION}.
     * @param duration  time millis.
     */
    public void smoothOpenMenu(int position, @DirectionMode int direction, int duration)
    {
        if (mOldSwipedLayout != null)
        {
            if (mOldSwipedLayout.isMenuOpen())
            {
                mOldSwipedLayout.smoothCloseMenu();
            }
        }
        position += getHeaderItemCount();
        ViewHolder vh = findViewHolderForAdapterPosition(position);
        if (vh != null)
        {
            View itemView = getSwipeMenuView(vh.itemView);
            if (itemView instanceof SwipeMenuLayout)
            {
                mOldSwipedLayout = (SwipeMenuLayout) itemView;
                if (direction == RIGHT_DIRECTION)
                {
                    mOldTouchedPosition = position;
                    mOldSwipedLayout.smoothOpenRightMenu(duration);
                }
                else if (direction == LEFT_DIRECTION)
                {
                    mOldTouchedPosition = position;
                    mOldSwipedLayout.smoothOpenLeftMenu(duration);
                }
            }
        }
    }

    /**
     * Close menu.
     */
    public void smoothCloseMenu()
    {
        if (mOldSwipedLayout != null && mOldSwipedLayout.isMenuOpen())
        {
            mOldSwipedLayout.smoothCloseMenu();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e)
    {
        boolean isIntercepted = super.onInterceptTouchEvent(e);
        if (allowSwipeDelete)  // swipe and menu conflict.
        {
            return isIntercepted;
        }
        else
        {
            if (e.getPointerCount() > 1)
            {
                return true;
            }
            int action = e.getAction();
            int x = (int) e.getX();
            int y = (int) e.getY();
            switch (action)
            {
                case MotionEvent.ACTION_DOWN:
                {
                    mDownX = x;
                    mDownY = y;
                    isIntercepted = false;

                    int touchingPosition = getChildAdapterPosition(findChildViewUnder(x, y));
                    if (touchingPosition != mOldTouchedPosition && mOldSwipedLayout != null && mOldSwipedLayout.isMenuOpen())
                    {
                        mOldSwipedLayout.smoothCloseMenu();
                        //isIntercepted = true;
                    }

                    if (isIntercepted)
                    {
                        mOldSwipedLayout = null;
                        mOldTouchedPosition = INVALID_POSITION;
                    }
                    else
                    {
                        ViewHolder vh = findViewHolderForAdapterPosition(touchingPosition);
                        if (vh != null)
                        {
                            View itemView = getSwipeMenuView(vh.itemView);
                            if (itemView instanceof SwipeMenuLayout)
                            {
                                mOldSwipedLayout = (SwipeMenuLayout) itemView;
                                mOldTouchedPosition = touchingPosition;
                            }
                        }
                    }
                    break;
                }
                // They are sensitive to retain sliding and inertia.
                case MotionEvent.ACTION_MOVE:
                {
                    isIntercepted = handleUnDown(x, y, isIntercepted);
                    if (mOldSwipedLayout == null)
                    {
                        break;
                    }
                    ViewParent viewParent = getParent();
                    if (viewParent == null)
                    {
                        break;
                    }

                    int disX = mDownX - x;
                    // 向左滑，显示右侧菜单，或者关闭左侧菜单。
                    boolean showRightCloseLeft = disX > 0 && (mOldSwipedLayout.hasRightMenu() || mOldSwipedLayout.isLeftCompleteOpen());
                    // 向右滑，显示左侧菜单，或者关闭右侧菜单。
                    boolean showLeftCloseRight = disX < 0 && (mOldSwipedLayout.hasLeftMenu() || mOldSwipedLayout.isRightCompleteOpen());
                    viewParent.requestDisallowInterceptTouchEvent(showRightCloseLeft || showLeftCloseRight);
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                {
                    isIntercepted = handleUnDown(x, y, isIntercepted);
                    break;
                }
            }


        }
        return isIntercepted;
    }

    private boolean handleUnDown(int x, int y, boolean defaultValue)
    {
        int disX = mDownX - x;
        int disY = mDownY - y;

        // swipe
        if (Math.abs(disX) > mScaleTouchSlop && Math.abs(disX) > Math.abs(disY))
        {
            return false;
        }
        // click
        if (Math.abs(disY) < mScaleTouchSlop && Math.abs(disX) < mScaleTouchSlop)
        {
            return false;
        }
        return defaultValue;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        int action = e.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOldSwipedLayout != null && mOldSwipedLayout.isMenuOpen())
                {
                    mOldSwipedLayout.smoothCloseMenu();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onTouchEvent(e);
    }

    private View getSwipeMenuView(View itemView)
    {
        if (itemView instanceof SwipeMenuLayout)
        {
            return itemView;
        }
        List<View> unvisited = new ArrayList<>();
        unvisited.add(itemView);
        while (!unvisited.isEmpty())
        {
            View child = unvisited.remove(0);
            if (!(child instanceof ViewGroup))
            { // view
                continue;
            }
            if (child instanceof SwipeMenuLayout)
            {
                return child;
            }
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) unvisited.add(group.getChildAt(i));
        }
        return itemView;
    }

    private int mScrollState = -1;
    
    @Override
    public void onScrollStateChanged(int state)
    {
        this.mScrollState = state;
    }


}
