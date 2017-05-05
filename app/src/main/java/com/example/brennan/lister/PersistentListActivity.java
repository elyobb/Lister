package com.example.brennan.lister;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brennan.lister.db.TaskContract;
import com.example.brennan.lister.db.TaskDbHelper;
import com.example.brennan.lister.util.AnimationHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import com.example.brennan.lister.util.Utility;
/**
 * Created by Brennan on 3/19/2017.
 */

public class PersistentListActivity extends AppCompatActivity{
    final Context context = this;
    private ListView taskListView;
    private TaskDbHelper taskDbHelper;
    private TaskAdapter taskAdapter;
    private static final String PERSISTENT_DATE_VALUE = "PERSISTENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persistent_list);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        taskListView = (ListView) findViewById(R.id.taskList);
        taskDbHelper = new TaskDbHelper(this);
        taskListView = (ListView) findViewById(R.id.taskList);
        setPopulationAnimation(taskListView);
        populatePersistentTasks();
        // the floating action button and all its listener functionality
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create new task dialog
                LayoutInflater li = LayoutInflater.from(context);
                final View promptsView = li.inflate(R.layout.persistent_task_prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                // populate priority spinner
                Spinner dropdown = (Spinner) promptsView.findViewById(R.id.taskPrioritySpinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(PersistentListActivity.this, R.array.priority_array, android.R.layout.simple_spinner_item);
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

                                Spinner taskPrioritySpinner = (Spinner) promptsView.findViewById(R.id.taskPrioritySpinner);
                                String taskPriority = taskPrioritySpinner.getSelectedItem().toString();
                                // check input before inserting new task into db
                                if (validTitle(taskTitle)) {
                                    // insert into db if task data is valid
                                    SQLiteDatabase db = taskDbHelper.getWritableDatabase();
                                    ContentValues values = new ContentValues();
                                    values.put(TaskContract.TaskEntry.COL_TASK_TITLE, taskTitle);
                                    values.put(TaskContract.TaskEntry.COL_TASK_PRIORITY, taskPriority);
                                    values.put(TaskContract.TaskEntry.COL_TASK_DATE, PERSISTENT_DATE_VALUE);

                                    db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                            null,
                                            values,
                                            SQLiteDatabase.CONFLICT_REPLACE);

                                    db.close();
                                    //refresh task list in case the new task was for today
                                    populatePersistentTasks();
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
        if(id == R.id.action_view_persistent_list){
            // just send user back to scheduled list they came from
            this.onBackPressed();
        }
         else if (id == R.id.action_calendar) {
            openCalendarActivity(findViewById(id));
        }
        else if (id == R.id.action_sort_desc){
            Utility.sortByPriorityDesc(taskAdapter);
        }
        else if (id == R.id.action_sort_asc){
            Utility.sortByPriorityAsc(taskAdapter);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens the calendar view to select dates from
     *
     * @param view calling object (button)
     */
    private void openCalendarActivity(View view) {
        Intent intent = new Intent(PersistentListActivity.this, CalendarActivity.class);
        startActivity(intent);
    }

    /**
     * Animates the task list in to the view (fades in one by one)
     * @param v
     *          the list view to show population animation for
     */
    private void setPopulationAnimation(ListView v) {
        AnimationSet set = new AnimationSet(true);

        Animation animation = AnimationHelper.createFadeInAnimation();
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        v.setLayoutAnimation(controller);
    }

    /**
     * Validates task title text
     * @param titleText
     *          the title text to validate
     * @return true if title text is valid, a toast alert and false otherwise
     */
    public boolean validTitle(String titleText){
        boolean valid = true;
        if (titleText.equals("")) {
            Toast.makeText(PersistentListActivity.this, "Task title cannot be empty!",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }
        return valid;
    }

    public void populatePersistentTasks(){
        ArrayList<Task> taskList = new ArrayList<Task>();
        SQLiteDatabase db = taskDbHelper.getReadableDatabase();

        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE, TaskContract.TaskEntry.COL_TASK_PRIORITY, TaskContract.TaskEntry.COL_TASK_DATE},
                TaskContract.TaskEntry.COL_TASK_DATE + " = '" + PERSISTENT_DATE_VALUE + "'", null, null, null, null);
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
                TaskContract.TaskEntry.COL_TASK_TITLE + " = '"+ task +"' AND " +
                TaskContract.TaskEntry.COL_TASK_DATE + " = '"+PERSISTENT_DATE_VALUE+"'",
                null);
        db.close();

        ListView taskListView = (ListView) parent.getParent().getParent();
        final int position = taskListView.getPositionForView(parent);

        final Animation animation = AnimationUtils.loadAnimation(PersistentListActivity.this, android.R.anim.slide_out_right);
        parent.startAnimation(animation);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                taskAdapter.remove(taskAdapter.getItem(position));
                taskAdapter.notifyDataSetChanged();
                animation.cancel();
            }
        },100);
        //populateTasksForTitleDate();
    }
}
