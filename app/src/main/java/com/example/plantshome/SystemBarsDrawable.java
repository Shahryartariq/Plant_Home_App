package com.example.plantshome;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Paints only the system bar strips (status bar, navigation bar, cutouts) in one colour,
 * leaving the rest transparent so the screen isn't drawn twice.
 */
final class SystemBarsDrawable extends Drawable {

    private final Paint paint = new Paint();
    private int left;
    private int top;
    private int right;
    private int bottom;

    SystemBarsDrawable(int color) {
        paint.setColor(color);
    }

    void setInsets(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        invalidateSelf();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect b = getBounds();
        if (top > 0) {
            canvas.drawRect(b.left, b.top, b.right, b.top + top, paint);
        }
        if (bottom > 0) {
            canvas.drawRect(b.left, b.bottom - bottom, b.right, b.bottom, paint);
        }
        if (left > 0) {
            canvas.drawRect(b.left, b.top, b.left + left, b.bottom, paint);
        }
        if (right > 0) {
            canvas.drawRect(b.right - right, b.top, b.right, b.bottom, paint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
