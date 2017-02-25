package com.example.brennan.lister;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brennan.lister.db.TaskContract;
import com.example.brennan.lister.db.TaskDbHelper;
import com.example.brennan.lister.db.TextValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListActivity extends AppCompatActivity {
    final Context context = this;
    private TaskListView taskListView;
    private TaskDbHelper taskDbHelper;
    private TaskAdapter taskAdapter;
    private String titleDateDbFormat = "";

    /**
     * interface method implementations
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // check if the date was passed by an intent
        Intent intent = getIntent();
        String passedDate = intent.getStringExtra("selectedDate");
        if (passedDate != null) {
            // this happens when user returns to list screen after selecting a date in calendar view
            toolbar.setTitle(passedDate);
            // update private db format title
            this.titleDateDbFormat = convertDateToDbFormat(passedDate);

        } else {
            String currentDateStr = getCurrentDateStr();
            // default set title to current date, to view current tasks for day
            toolbar.setTitle(currentDateStr);
            // update private db format title
            this.titleDateDbFormat = convertDateToDbFormat(currentDateStr);
        }
        setSupportActionBar(toolbar);
        taskDbHelper = new TaskDbHelper(this);
        taskListView = (TaskListView) findViewById(R.id.taskList);
        populateTasksForTitleDate();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create new task dialog
                LayoutInflater li = LayoutInflater.from(context);
                final View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                prepopulateDateFieldFromTitle(promptsView);

                // populate priority spinner
                Spinner dropdown = (Spinner) promptsView.findViewById(R.id.taskPrioritySpinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ListActivity.this, R.array.priority_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                dropdown.setAdapter(adapter);
                dropdown.setSelection(1);


                alertDialogBuilder
                        .setTitle("Create a Task")
                        .setCancelable(false)
                        .setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // only close dialog if input was valid and inserted to db
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {

                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                boolean goodToCloseDialog = false;

                                //retrieve input data from dialog
                                final EditText taskTitleField = (EditText) promptsView.findViewById(R.id.taskTitleField);
                                String taskTitle = taskTitleField.getText().toString();
                                final EditText taskDateField = (EditText) promptsView.findViewById(R.id.taskDateField);
                                String taskDate = taskDateField.getText().toString();

                                Spinner taskPrioritySpinner = (Spinner) promptsView.findViewById(R.id.taskPrioritySpinner);
                                String taskPriority = taskPrioritySpinner.getSelectedItem().toString();
                                // check input before inserting new task into db
                                if (validTitleAndDate(taskTitle, taskDate)) {
                                    // insert into db if task data is valid
                                    SQLiteDatabase db = taskDbHelper.getWritableDatabase();
                                    ContentValues values = new ContentValues();
                                    values.put(TaskContract.TaskEntry.COL_TASK_TITLE, taskTitle);
                                    values.put(TaskContract.TaskEntry.COL_TASK_PRIORITY, taskPriority);
                                    values.put(TaskContract.TaskEntry.COL_TASK_DATE, taskDate);

                                    db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                            null,
                                            values,
                                            SQLiteDatabase.CONFLICT_REPLACE);

                                    db.close();
                                    //refresh task list in case the new task was for today
                                    populateTasksForTitleDate();
                                    goodToCloseDialog = true;
                                }
                                if (goodToCloseDialog) {
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });

                alertDialog.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                // finally, show dialog
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_calendar) {
            openCalendarActivity(findViewById(id));
        }
        return super.onOptionsItemSelected(item);
    }


    /**----------------------------------------------
     * utility functions
     * -------------------------------------------**/

    /**
     * Gets current system date and returns as String
     *
     * @return current system date in "E, MMMM d, yyyy" format
     */
    private String getCurrentDateStr() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat(getResources().getString(R.string.date_format_full));
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    /**
     * Opens the calendar view to select dates from
     *
     * @param view calling object (button)
     */
    private void openCalendarActivity(View view) {
        Intent intent = new Intent(ListActivity.this, CalendarActivity.class);
        startActivity(intent);
    }

    /**
     * Converts the longform date format to be used by the Task table in a shorter format
     * "E, MMMM d, yyyy" -> "mm, dd, yyyy"
     *
     * @param dateToConvert the date to convert to database-friendly shorthand
     * @return converted Date string
     */
    private String convertDateToDbFormat(String dateToConvert) {
        SimpleDateFormat fromFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_abbreviated));
        String convertedDate = "";
        try {
            Date fromDate = fromFormat.parse(dateToConvert);
            SimpleDateFormat dbFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_db));
            convertedDate = dbFormat.format(fromDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    /**
     * Checks if title and date of task are valid entries from user
     *
     * @param titleText the title to check for validity
     * @param dateText  the date to check for validity
     * @return true if all input was valid, false otherwise
     */
    private boolean validTitleAndDate(String titleText, String dateText) {
        boolean allValid = true;
        if (titleText.equals("")) {
            Toast.makeText(ListActivity.this, "Task title cannot be empty!",
                    Toast.LENGTH_LONG).show();
            allValid = false;
        }
        if (dateText.equals("")) {
            Toast.makeText(ListActivity.this, "Task date cannot be empty!",
                    Toast.LENGTH_LONG).show();
            allValid = false;
        }
        //try to parse date now, eventually just include a date picker
        SimpleDateFormat dbFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_db));
        try {
            Date parsedDate = dbFormat.parse(dateText);
            Date now = Calendar.getInstance().getTime();
            String formattedNow = dbFormat.format(now);
            Date convertedNow = dbFormat.parse(formattedNow);
            if (parsedDate.before(convertedNow)) {
                Toast.makeText(ListActivity.this, "Task date cannot be in the past.",
                        Toast.LENGTH_LONG).show();
                allValid = false;
            }
        } catch (ParseException pe) {
            Toast.makeText(ListActivity.this, "Invalid date format. Use 'mm-dd-yyyy' style.",
                    Toast.LENGTH_LONG).show();
            allValid = false;
        }
        return allValid;
    }


    /**----------------------------------------------
     * database functionality
     * -------------------------------------------**/

    /**
     * Prepopulates the user input for a task date with the date currently in the
     * app's title bar.
     *
     * @param promptsView the View representing all input fields
     */
    private void prepopulateDateFieldFromTitle(View promptsView) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String parsedDate = convertDateToDbFormat(toolbar.getTitle().toString());
        // prepopulate the task date with the current activity title
        EditText date = (EditText) promptsView.findViewById(R.id.taskDateField);
        date.setText(parsedDate);
    }

    /**
     * Populates the list activity with all Tasks which have dates matching the date in the title
     * toolbar.
     */
    private void populateTasksForTitleDate() {
        ArrayList<Task> taskList = new ArrayList<Task>();
        SQLiteDatabase db = taskDbHelper.getReadableDatabase();

        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE, TaskContract.TaskEntry.COL_TASK_PRIORITY, TaskContract.TaskEntry.COL_TASK_DATE},
                TaskContract.TaskEntry.COL_TASK_DATE + " = '" + titleDateDbFormat + "'", null, null, null, null);
        while (cursor.moveToNext()) {
            int titleIdx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            int priorityIdx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_PRIORITY);
            int dateIdx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DATE);
            //create a Task object
            Task newTask = new Task(cursor.getString(titleIdx), cursor.getString(priorityIdx), cursor.getString(dateIdx));
            taskList.add(newTask);
        }

        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(this, taskList);
            taskListView.setAdapter(taskAdapter);
        } else {
            taskAdapter.clear();
            taskAdapter.addAll(taskList);
            taskAdapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();
    }

    /**
     * Deletes a task from the Task table, and refreshes the main task view.
     *
     * @param view the calling object
     */
    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.taskTitle);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = taskDbHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        populateTasksForTitleDate();
    }
}
