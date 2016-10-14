package org.ligi.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by ligi on 14.10.16.
 */
public class SquareImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        val size = Math.min(parentWidth, parentHeight)
        this.setMeasuredDimension(size, size)
    }
}