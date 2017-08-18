package com.umeng.myrecyclerview;

import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Elson on 2017/8/18.
 */

public class CompatItemTouchHelper extends ItemTouchHelper
{

    private Callback mCallback;


    public CompatItemTouchHelper(Callback callback)
    {
        super(callback);
        this.mCallback = callback;
    }

    /**
     * Developer callback which controls the behavior of ItemTouchHelper.
     *
     * @return {@link Callback}
     */
    public Callback getCallback()
    {

        return mCallback;
    }
}
