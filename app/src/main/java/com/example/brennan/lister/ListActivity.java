package com.example.brennan.lister;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ListActivity extends AppCompatActivity {
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // check if the date was passed by an intent
        Intent intent = getIntent();
        String passedDate = intent.getStringExtra("selectedDate");
        if(passedDate!=null){
            toolbar.setTitle(passedDate);
        }
        else{
            // default set title to current date, to view current tasks for day
            toolbar.setTitle(getCurrentDateStr());
        }

        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create new task dialog
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                prepopulateDateField(toolbar, promptsView);

                // populate priority spinner
                Spinner dropdown = (Spinner)promptsView.findViewById(R.id.taskPrioritySpinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ListActivity.this, R.array.priority_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                dropdown.setAdapter(adapter);
                dropdown.setSelection(1);

                alertDialogBuilder
                        .setTitle("Create a Task")
                        .setCancelable(false)
                        .setPositiveButton("CREATE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // validate inputs

                                        // create task in database, refresh list view

                                    }
                                })
                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_calendar){
            openCalendarActivity(findViewById(id));
        }

        return super.onOptionsItemSelected(item);
    }

    public String getCurrentDateStr(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat(getResources().getString(R.string.date_format_full));
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }

    public void openCalendarActivity(View view){
        Intent intent = new Intent(ListActivity.this, CalendarActivity.class);
        startActivity(intent);
    }

    public void prepopulateDateField(Toolbar toolbar, View promptsView){
        String titleDate = toolbar.getTitle().toString();
        SimpleDateFormat to = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat from = new SimpleDateFormat("E, MMM d, yyyy");
        String parsedDate = "";
        try {
            parsedDate = to.format(from.parse(titleDate));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        // prepopulate the task date with the current activity title
        EditText date = (EditText) promptsView.findViewById(R.id.taskDate);
        date.setText(parsedDate);
    }
}
