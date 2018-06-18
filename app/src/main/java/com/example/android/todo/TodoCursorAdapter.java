package com.example.android.todo;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.todo.data.TodoContract;


public class TodoCursorAdapter extends CursorAdapter {


    public TodoCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);

        int name1ColumnIndex = cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TASK);

        String team1 = cursor.getString(name1ColumnIndex);

        nameTextView.setText(team1);
    }
}

