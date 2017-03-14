package com.paandw.peter.androidchallenge;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Creates the lines between items in lists... Basically makes them look cleaner
 * Created by Peter Wells on 3/14/2017.
 */

public class ListDivider extends RecyclerView.ItemDecoration{
    private Drawable divider;

    public ListDivider(Context context){
        divider = ContextCompat.getDrawable(context, R.drawable.line_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state){
        int left = parent.getPaddingLeft() + 250;
        int right = parent.getWidth() - parent.getPaddingRight() - 50;

        int children = parent.getChildCount();
        for(int i = 0; i < children; i++){
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
            int top = child.getBottom() - params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}
