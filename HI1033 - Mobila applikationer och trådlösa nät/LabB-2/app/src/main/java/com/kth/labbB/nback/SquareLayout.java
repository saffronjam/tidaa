package com.kth.labbB.nback;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * A LinearLayout with side equal to the minimum of the
 * width and height available for this view group (layout).
 * For details, see method onMeasure.
 * <p>
 * NB! Rebuild the project after adding a custom view or
 * view group to your project.
 */
public class SquareLayout extends LinearLayout {

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // called when Android is trying to figure out the (new) size for this view
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int squareSide = Math.min(width, height);
        super.onMeasure(MeasureSpec.makeMeasureSpec(squareSide, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(squareSide, MeasureSpec.EXACTLY));
        setMeasuredDimension(squareSide, squareSide); // make it a square
    }
}
