package com.example.akshirsa.contactsexplorer.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by akshirsa on 10/22/2014.
 */
public class ContactsListView extends ListView {
    boolean mDragMode;
    boolean mSwipeMode;

    int mStartPosition;
    int mEndPosition;
    int mDragPointOffset;		//Used to adjust drag view location

    private int action_down_x = 0;
    private int action_up_x = 0;
    private int difference = 0;

    ImageView mDragView;
    GestureDetector mGestureDetector;

    DropListener mDropListener;
    RemoveListener mRemoveListener;
    DragListener mDragListener;
    SwipeListener mSwipeListener;

    public ContactsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDropListener(DropListener l) {
        mDropListener = l;
    }

    public void setRemoveListener(RemoveListener l) {
        mRemoveListener = l;
    }

    public void setDragListener(DragListener l) {
        mDragListener = l;
    }

    public void setSwipeListener(SwipeListener l) {
        mSwipeListener = l;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();

        if (action == MotionEvent.ACTION_DOWN && x < this.getWidth()/4)
        {
            mDragMode = true;
            Log.d("onTouchEvent", "This is a drag mode");
        }
        else if(action == MotionEvent.ACTION_DOWN && x > (3 * this.getWidth())/4)
        {
            Log.d("onTouchEvent", "This is a swipe mode");
            mSwipeMode = true;
        }

        if (mDragMode) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mStartPosition = pointToPosition(x, y);
                    if (mStartPosition != INVALID_POSITION) {
                        Log.d("Listener", "Start Position : " + mStartPosition);
                        int mItemPosition = mStartPosition - getFirstVisiblePosition();
                        mDragPointOffset = y - getChildAt(mItemPosition).getTop();
                        mDragPointOffset -= ((int) ev.getRawY()) - y;
                        startDrag(mItemPosition, y);
                        drag(0, y);// replace 0 with x if desired
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    drag(0, y);// replace 0 with x if desired
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                default:
                    mDragMode = false;
                    mEndPosition = pointToPosition(x, y);
                    stopDrag(mStartPosition - getFirstVisiblePosition());
                    if (mDropListener != null && mStartPosition != INVALID_POSITION && mEndPosition != INVALID_POSITION) {
                        Log.d("Listener", "End Position : " + mEndPosition);
                        mDropListener.onDrop(mStartPosition, mEndPosition);
                    }
                    break;
            }
        }
        else if(mSwipeMode)
        {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    action_down_x = (int) ev.getX();
                    //Log.d("action", "ACTION_DOWN - ");
                    break;
                case MotionEvent.ACTION_MOVE:
                    //Log.d("action", "ACTION_MOVE - ");
                    action_up_x = (int) ev.getX();
                    difference = action_down_x - action_up_x;
                    break;
                case MotionEvent.ACTION_UP:
                    //Log.d("action", "ACTION_UP - ");
                    if(difference > 30) {
                        mEndPosition = pointToPosition(x, y);
                        Log.d("Listener", "End Position : " + mEndPosition);
                        mSwipeListener.onSwipeDone(mEndPosition);
                    }
                    else
                        return super.onTouchEvent(ev);

                    action_down_x = 0;
                    action_up_x = 0;
                    difference = 0;
                    break;
            }
            return true;
        }
        else
            return super.onTouchEvent(ev);

        return true;
    }


    // -------------------------------------------------------------
    // Handle drag n drop events

    // move the drag view
    private void drag(int x, int y) {
        if (mDragView != null) {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mDragView.getLayoutParams();
            layoutParams.x = x;
            layoutParams.y = y - mDragPointOffset;
            WindowManager mWindowManager = (WindowManager) getContext()
                    .getSystemService(Context.WINDOW_SERVICE);
            mWindowManager.updateViewLayout(mDragView, layoutParams);

            if (mDragListener != null)
                mDragListener.onDrag(x, y, null);// change null to "this" when ready to use
        }
    }

    // enable the drag view for dragging
    private void startDrag(int itemIndex, int y) {
        stopDrag(itemIndex);

        View item = getChildAt(itemIndex);
        if (item == null) return;
        item.setDrawingCacheEnabled(true);
        if (mDragListener != null)
            mDragListener.onStartDrag(item);

        // Create a copy of the drawing cache so that it does not get recycled
        // by the framework when the list tries to clean up memory
        Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());

        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP;
        mWindowParams.x = 0;
        mWindowParams.y = y - mDragPointOffset;

        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = 0;

        Context context = getContext();
        ImageView v = new ImageView(context);
        v.setImageBitmap(bitmap);

        WindowManager mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(v, mWindowParams);
        mDragView = v;
    }

    // destroy drag view
    private void stopDrag(int itemIndex) {
        if (mDragView != null) {
            if (mDragListener != null)
                mDragListener.onStopDrag(getChildAt(itemIndex));
            mDragView.setVisibility(GONE);
            WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
    }


    // --------------------------------------------------
    // Handle swipe events


}
