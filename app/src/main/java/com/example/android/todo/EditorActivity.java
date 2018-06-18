package com.example.android.todo;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.todo.data.TodoContract.TodoEntry;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_TASK_LOADER = 0;

    private boolean fall;

    private Uri mCurrentTaskUri;


    private EditText mnameEditText;


    private EditText mdescriptionEditText;

    private EditText mdateEditText;

    private CheckBox mdone;

    private boolean mTaskHasChanged = false;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mTaskHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        Intent intent = getIntent();
        mCurrentTaskUri = intent.getData();


        if (mCurrentTaskUri == null) {
            setTitle("INSERT TASK");

            invalidateOptionsMenu();

        } else {
            setTitle("EDIT TASK");


            getLoaderManager().initLoader(EXISTING_TASK_LOADER, null, this);
        }

        mnameEditText = (EditText) findViewById(R.id.name);
        mdescriptionEditText = (EditText) findViewById(R.id.description);
        mdateEditText = (EditText) findViewById(R.id.date);
        mdone = (CheckBox) findViewById(R.id.done);
        if(mCurrentTaskUri == null)
            mdone.setVisibility(View.INVISIBLE);
        else
            mdone.setVisibility(View.VISIBLE);

        mnameEditText.setOnTouchListener(mTouchListener);
        mdescriptionEditText.setOnTouchListener(mTouchListener);
        mdateEditText.setOnTouchListener(mTouchListener);

    }
    private void saveTask() {
        String nameString = mnameEditText.getText().toString().trim();
        String descriptionString = mdescriptionEditText.getText().toString().trim();
        String dateString = mdateEditText.getText().toString().trim();
        Boolean check;
        if(mCurrentTaskUri == null)
            check=false;
        else
            check = mdone.isChecked();
        if(mdone.isChecked())
            deleteTask();

        if (nameString.isEmpty() || descriptionString.isEmpty() || dateString.isEmpty()) {
            fall = false;
            Toast.makeText(this, "ENTER PROPER DETAILS",
                    Toast.LENGTH_SHORT).show();
        } else {

            fall = true;


            ContentValues values = new ContentValues();
            values.put(TodoEntry.COLUMN_TASK, nameString);
            values.put(TodoEntry.COLUMN_DESCRIPTION, descriptionString);
            values.put(TodoEntry.COLUMN_DATE, dateString);
            values.put(TodoEntry.COLUMN_CHECK,check.toString());


            if (mCurrentTaskUri == null) {
                Uri newUri = getContentResolver().insert(TodoEntry.CONTENT_URI, values);

                if (newUri == null) {
                    Toast.makeText(this, "INSERT FAILED",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "INSERT SUCCESSFUL",
                            Toast.LENGTH_SHORT).show();
                }
            } else {

                int rowsAffected = getContentResolver().update(mCurrentTaskUri, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, "UPDATE FAILED",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "UPDATE SUCCESSFUL",
                            Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveTask();
                if (fall) {
                    WidgetProvider.sendRefreshBroadcast(this);
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mTaskHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (!mTaskHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                TodoEntry._ID,
                TodoEntry.COLUMN_TASK,
                TodoEntry.COLUMN_DESCRIPTION,
                TodoEntry.COLUMN_DATE,
                TodoEntry.COLUMN_CHECK
        };

        return new CursorLoader(this,   // Parent activity context
                mCurrentTaskUri,         // Query the content URI for the current
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }


        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(TodoEntry.COLUMN_TASK);
            int descriptionColumnIndex = cursor.getColumnIndex(TodoEntry.COLUMN_DESCRIPTION);
            int dateColumnIndex = cursor.getColumnIndex(TodoEntry.COLUMN_DATE);
            int checkColumnIndex = cursor.getColumnIndex(TodoEntry.COLUMN_CHECK);


            // Extract out the value from the Cursor for the given column index
            final String name = cursor.getString(nameColumnIndex);
            final String description = cursor.getString(descriptionColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String check = cursor.getString(checkColumnIndex);

            // Update the views on the screen with the values from the database
            mnameEditText.setText(name);
            mdescriptionEditText.setText(description);
            mdateEditText.setText(date);
            if(check.equals("true"))
                mdone.setChecked(true);
            else
                mdone.setChecked(false);


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mnameEditText.setText("");
        mdescriptionEditText.setText("");
        mdateEditText.setText("");
        mdone.setChecked(false);

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("DISCARD", discardButtonClickListener);
        builder.setNegativeButton("KEEP EDITING", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentTaskUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this Task");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteTask();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteTask() {
        if (mCurrentTaskUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentTaskUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Error with deleting Task",
                        Toast.LENGTH_SHORT).show();
            } else {
                WidgetProvider.sendRefreshBroadcast(this);
                Toast.makeText(this, "Task deleted",
                        Toast.LENGTH_SHORT).show();
            }
            finish();

        }
    }

}