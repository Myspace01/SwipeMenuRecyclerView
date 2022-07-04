package com.umeng.recylerviewtest;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.umeng.myrecyclerview.DefaultItemDecoration;
import com.umeng.myrecyclerview.SwipeItemClickListener;
import com.umeng.myrecyclerview.SwipeMenu;
import com.umeng.myrecyclerview.SwipeMenuBridge;
import com.umeng.myrecyclerview.SwipeMenuCreator;
import com.umeng.myrecyclerview.SwipeMenuItem;
import com.umeng.myrecyclerview.SwipeMenuItemClickListener;
import com.umeng.myrecyclerview.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeItemClickListener
{

    private SwipeMenuRecyclerView recycler_view;
    private RecyclerView.Adapter mAdapter;
    private List<String> strings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler_view = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);

        recycler_view.setSwipeMenuCreator(swipeMenuCreator);
        recycler_view.setSwipeMenuItemClickListener(mMenuItemClickListener);

        strings = new ArrayList<>();
        for (int i = 0; i < 20; i++)
        {

            strings.add("====" + i);

        }

        mAdapter = new MainAdapter(strings);

        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.colorAccent)));
        recycler_view.setAdapter(mAdapter);

        // RecyclerView的Item的点击事件。
        recycler_view.setSwipeItemClickListener(this);
    }


    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator()
    {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType)
        {
            int width = 100;

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 根据ViewType来决定哪一个item该如何添加菜单。
            // 这里模拟业务，实际开发根据自己的业务计算。
            SwipeMenuItem addItem = new SwipeMenuItem(MainActivity.this).setBackground(R.drawable.selector_green).setText("删除").setTextColor(Color.WHITE)
                    .setWidth(width).setHeight(height);
            swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener()
    {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge)
        {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION)
            {
                strings.remove(adapterPosition);
                mAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();

            }
            else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION)
            {
                Toast.makeText(MainActivity.this, "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public void onItemClick(View itemView, int position)
    {
        Toast.makeText(this, "第" + position + "个", Toast.LENGTH_SHORT).show();
    }
}
