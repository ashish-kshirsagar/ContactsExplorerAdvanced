package com.example.akshirsa.contactsexplorer.View;

import android.view.View;
import android.widget.ListView;

/**
 * Created by akshirsa on 10/22/2014.
 */
public interface DragListener {
    /**
     * Called when a drag starts.
     * itemView - the view of the item to be dragged i.e. the drag view
     */
    void onStartDrag(View itemView);

    /**
     * Called when a drag is to be performed.
     * x - horizontal coordinate of MotionEvent.
     * y - verital coordinate of MotionEvent.
     * listView - the listView
     */
    void onDrag(int x, int y, ListView listView);

    /**
     * Called when a drag stops.
     * Any changes in onStartDrag need to be undone here
     * so that the view can be used in the list again.
     * itemView - the view of the item to be dragged i.e. the drag view
     */
    void onStopDrag(View itemView);
}
