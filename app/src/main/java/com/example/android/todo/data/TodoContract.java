
package com.example.android.todo.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class TodoContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.todo";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TASK = "tasks";


    private TodoContract() {
    }

    public static final class TodoEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TASK);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASK;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASK;


        public final static String TABLE_NAME = "tasks";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_TASK = "task";

        public final static String COLUMN_DESCRIPTION = "description";

        public final static String COLUMN_DATE = "date";

        public final static String COLUMN_CHECK = "done";


    }

}
