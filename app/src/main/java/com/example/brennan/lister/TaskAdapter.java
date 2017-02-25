package com.example.brennan.lister;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Custom adapter for the Task class
 * Created by Brennan on 2/20/2017.
 */

public class TaskAdapter extends ArrayAdapter<Task> {
    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Task task = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView taskTitle = (TextView) convertView.findViewById(R.id.taskTitle);

        // Populate the data into the template view using the data object
        taskTitle.setText(task.getTitle());
        // set task color by its priority
        switch(task.getPriority()){
            case "Low Priority":
                // set bg color to light green
                convertView.setBackgroundColor(Color.rgb(173, 237, 173));
                break;
            case "High Priority":
                // set bg color to gentle red
                convertView.setBackgroundColor(Color.rgb(239, 110, 110));
                break;
            default:
                // normal priority, set bg color to light yellow
                convertView.setBackgroundColor(Color.rgb(253, 253, 150));
                break;
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
