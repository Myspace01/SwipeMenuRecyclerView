package com.umeng.myrecyclerview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Elson on 2017/8/18.
 */

public class SwipeMenuView extends LinearLayout implements View.OnClickListener {

    private SwipeSwitch mSwipeSwitch;
    private SwipeMenuItemClickListener mItemClickListener;
    private int mDirection;
    private RecyclerView.ViewHolder mAdapterVIewHolder;

    public SwipeMenuView(Context context) {
        this(context, null);
    }

    public SwipeMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void createMenu(SwipeMenu swipeMenu, SwipeSwitch swipeSwitch, SwipeMenuItemClickListener swipeMenuItemClickListener, int direction) {
        removeAllViews();

        this.mSwipeSwitch = swipeSwitch;
        this.mItemClickListener = swipeMenuItemClickListener;
        this.mDirection = direction;

        List<SwipeMenuItem> items = swipeMenu.getMenuItems();
        for (int i = 0; i < items.size(); i++) {
            addItem(items.get(i), i);
        }
    }

    public void bindViewHolder(RecyclerView.ViewHolder adapterVIewHolder) {
        this.mAdapterVIewHolder = adapterVIewHolder;
    }

    private void addItem(SwipeMenuItem item, int index) {
        LayoutParams params = new LayoutParams(item.getWidth(), item.getHeight());
        params.weight = item.getWeight();
        LinearLayout parent = new LinearLayout(getContext());
        parent.setId(index);
        parent.setGravity(Gravity.CENTER);
        parent.setOrientation(VERTICAL);
        parent.setLayoutParams(params);
        ViewCompat.setBackground(parent, item.getBackground());
        parent.setOnClickListener(this);
        addView(parent);

        SwipeMenuBridge menuBridge = new SwipeMenuBridge(mDirection, index, mSwipeSwitch, parent);
        parent.setTag(menuBridge);

        if (item.getImage() != null) {
            ImageView iv = createIcon(item);
            menuBridge.mImageView = iv;
            parent.addView(iv);
        }

        if (!TextUtils.isEmpty(item.getText())) {
            TextView tv = createTitle(item);
            menuBridge.mTextView = tv;
            parent.addView(tv);
        }
    }

    private ImageView createIcon(SwipeMenuItem item) {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(item.getImage());
        return imageView;
    }

    private TextView createTitle(SwipeMenuItem item) {
        TextView textView = new TextView(getContext());
        textView.setText(item.getText());
        textView.setGravity(Gravity.CENTER);
        int textSize = item.getTextSize();
        if (textSize > 0)
            textView.setTextSize(textSize);
        ColorStateList textColor = item.getTitleColor();
        if (textColor != null)
            textView.setTextColor(textColor);
        int textAppearance = item.getTextAppearance();
        if (textAppearance != 0)
            TextViewCompat.setTextAppearance(textView, textAppearance);
        Typeface typeface = item.getTextTypeface();
        if (typeface != null)
            textView.setTypeface(typeface);
        return textView;
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null && mSwipeSwitch.isMenuOpen()) {
            SwipeMenuBridge menuBridge = (SwipeMenuBridge) v.getTag();
            menuBridge.mAdapterPosition = mAdapterVIewHolder.getAdapterPosition();
            mItemClickListener.onItemClick(menuBridge);
        }
    }
}