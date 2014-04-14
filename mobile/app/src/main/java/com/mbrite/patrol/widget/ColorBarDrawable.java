package com.mbrite.patrol.widget;

import android.graphics.drawable.*;
import android.graphics.*;
import android.graphics.Color;

public class ColorBarDrawable extends Drawable {

    private float percent;

    public ColorBarDrawable(float percent) {
        this.percent = percent;
    }

    @Override
    public void draw(Canvas canvas) {
        // get drawable dimensions
        Rect bounds = getBounds();

        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        // draw background gradient
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(143, 188, 143));
        canvas.drawRect(0, 0, percent * width, height, backgroundPaint);
        backgroundPaint.setColor(Color.rgb(135, 206, 250));
        canvas.drawRect(percent * width, 0, width, height, backgroundPaint);
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}