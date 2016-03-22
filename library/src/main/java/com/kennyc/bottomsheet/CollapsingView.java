package com.kennyc.bottomsheet;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

/**
 * Created by kcampagna on 8/11/15.
 */
public class CollapsingView extends FrameLayout {

    private ViewDragHelper mDragHelper;

    private int mMinCollapseHeight;

    private int mHeight;

    private CollapseListener mListener;

    private boolean mEnableDrag = true;

    public CollapsingView(Context context) {
        super(context);
    }

    public CollapsingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CollapsingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDragHelper = ViewDragHelper.create(this, 0.8f, new DragCallback());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mDragHelper.shouldInterceptTouchEvent(event)) {
            return true;
        }

        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mMinCollapseHeight = h / 2;
    }

    public void setCollapseListener(CollapseListener listener) {
        mListener = listener;
    }

    public void enableDrag(boolean drag) {
        mEnableDrag = drag;
    }

    private class DragCallback extends ViewDragHelper.Callback {
        private static final float CLOSE_VELOCITY = 800.0f;

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (!mEnableDrag) return false;

            if (child instanceof LinearLayout && child.getId() == R.id.container) {
                // Check that our GridView can't be scrolled up. If it can' don't allow the view to be captured
                GridView gv = (GridView) child.findViewById(R.id.grid);

                if (gv != null) {
                    return !gv.canScrollVertically(-1);
                }
            }

            return true;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight();
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top > 0 ? top : 0;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (yvel >= CLOSE_VELOCITY || releasedChild.getTop() >= mMinCollapseHeight) {
                mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), mHeight);
            } else {
                mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), 0);
            }

            invalidate();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (top >= mHeight && mListener != null) mListener.onCollapse();
        }
    }

    interface CollapseListener {
        void onCollapse();
    }
}
