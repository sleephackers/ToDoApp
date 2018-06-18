package com.example.android.todo.data;

import android.content.ContentProvider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.todo.data.TodoContract.TodoEntry;

import android.util.Log;

public class TodoProvider extends ContentProvider {

    public static final String LOG_TAG = TodoProvider.class.getSimpleName();
    private static final int TASKS = 100;
    private static final int TASKS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI("com.example.android.todo", "tasks", TASKS);
        sUriMatcher.addURI("com.example.android.todo", "tasks/#", TASKS_ID);
    }

    private TodoDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new TodoDbHelper(getContext());

        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:

                cursor = database.query(TodoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TASKS_ID:

                selection = TodoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(TodoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return insertFixture(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private Uri insertFixture(Uri uri, ContentValues values) {
        String name1 = values.getAsString(TodoEntry.COLUMN_TASK);
        if (name1 == null) {
            throw new IllegalArgumentException("Task requires a gist");
        }
        String name2 = values.getAsString(TodoEntry.COLUMN_DESCRIPTION);
        if (name2 == null) {
            throw new IllegalArgumentException("Task requires a description");
        }
        String date = values.getAsString(TodoEntry.COLUMN_DATE);
        if (date == null) {
            throw new IllegalArgumentException("Task requires valid date");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(TodoEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return updateFixture(uri, contentValues, selection, selectionArgs);
            case TASKS_ID:

                selection = TodoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateFixture(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }

    private int updateFixture(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(TodoEntry.COLUMN_TASK)) {
            String name1 = values.getAsString(TodoEntry.COLUMN_TASK);
            if (name1 == null) {
                throw new IllegalArgumentException("Task requires a gist");
            }

        }
        if (values.containsKey(TodoEntry.COLUMN_DESCRIPTION)) {
            String name2 = values.getAsString(TodoEntry.COLUMN_DESCRIPTION);
            if (name2 == null) {
                throw new IllegalArgumentException("Task requires a description");
            }

        }


        if (values.containsKey(TodoEntry.COLUMN_DATE)) {
            String date = values.getAsString(TodoEntry.COLUMN_DATE);
            if (date == null) {
                throw new IllegalArgumentException("Task requires a valid date");
            }
        }


        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(TodoEntry.TABLE_NAME, values, selection, selectionArgs);


        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;

    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                rowsDeleted = database.delete(TodoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TASKS_ID:
                selection = TodoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TodoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

        }
        return rowsDeleted;

    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return TodoEntry.CONTENT_LIST_TYPE;
            case TASKS_ID:
                return TodoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with task " + match);
        }
    }
}
