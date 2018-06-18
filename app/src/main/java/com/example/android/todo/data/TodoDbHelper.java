package com.example.android.todo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.todo.data.TodoContract.TodoEntry;

public class TodoDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = TodoDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "tasklist.db";

    private static final int DATABASE_VERSION = 1;

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_FIXTURES_TABLE = "CREATE TABLE " + TodoContract.TodoEntry.TABLE_NAME + " ("
                + TodoContract.TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TodoContract.TodoEntry.COLUMN_TASK+ " TEXT NOT NULL, "
                + TodoContract.TodoEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + TodoContract.TodoEntry.COLUMN_DATE + " DATE NOT NULL , "
                + TodoEntry.COLUMN_CHECK + " TEXT );";


        db.execSQL(SQL_CREATE_FIXTURES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}