package com.game.walkingpixels.controller.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ResponsiveButton extends androidx.appcompat.widget.AppCompatButton {
    public ResponsiveButton(Context context) {
        super(context);
    }

    public ResponsiveButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResponsiveButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            getForeground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }
        else if (event.getAction() == MotionEvent.ACTION_UP && isEnabled()) {
            getForeground().setColorFilter(null);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if(enabled)
            getForeground().setColorFilter(null);
        else
            getForeground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        super.setEnabled(enabled);
    }
}
