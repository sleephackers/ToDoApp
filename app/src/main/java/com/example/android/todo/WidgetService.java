package com.example.android.todo;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;



public class WidgetService extends RemoteViewsService {
    private static final String TAG = "WidgetService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "onGetViewFactory: " + "Service called");
        return new WidgetFactory(this.getApplicationContext(), intent);
    }
}
