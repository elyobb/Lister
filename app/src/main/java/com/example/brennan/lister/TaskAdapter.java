package com.example.brennan.lister;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.graphics.Color;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.brennan.lister.util.AnimationHelper;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import java.util.ArrayList;

/**
 * Custom adapter for the Task class
 * Created by Brennan on 2/20/2017.
 */

public class TaskAdapter extends ArrayAdapter<Task> {
    private Context context;
    private ArrayList<Task> tasks;
    private ArrayList<Task> deleteableTasks;

    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
        this.context = context;
        this.tasks = tasks;
        deleteableTasks = new ArrayList<Task>();
    }


    @Override
    public Task getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(16)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Task task = getItem(position);
        View view = convertView;
        checkIfTaskMarkedAsDeleted(view, task);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView taskTitle = (TextView) convertView.findViewById(R.id.taskTitle);

        // Populate the data into the template view using the data object
        taskTitle.setText(task.getTitle());

        // set task color by its priority
        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.priority_indicator);
        GradientDrawable indicator = (GradientDrawable) layerDrawable
                .findDrawableByLayerId(R.id.priority_indicator);
        int color;
        String colorHex;

        switch (task.getPriority()) {
            case "Normal Priority":
                // set bg color to gentle red
                colorHex = "#" + Integer.toHexString(ContextCompat.getColor(context, R.color.yellow));
                color = Color.parseColor(colorHex);
                indicator.setStroke(25, color);
                break;
            case "Low Priority":
                // set bg color to green
                colorHex = "#" + Integer.toHexString(ContextCompat.getColor(context, R.color.green));
                color = Color.parseColor(colorHex);
                indicator.setStroke(25, color);
                break;
            case "High Priority":
                // set bg color to red
                colorHex = "#" + Integer.toHexString(ContextCompat.getColor(context, R.color.red));
                color = Color.parseColor(colorHex);
                indicator.setStroke(25, color);
                break;
            default:
                break;
        }
        convertView.setBackground(layerDrawable);
        // Return the completed view to render on screen
        checkIfTaskMarkedAsDeleted(view, task);
        return convertView;
    }

    public void delete(int position) {
        deleteableTasks.add(tasks.get(position));
        notifyDataSetChanged();
    }

    public void checkIfTaskMarkedAsDeleted(View view, Task task) {
        for (Task deleteable : deleteableTasks) {
            deleteTaskIfMarkedDeletable(view, task, deleteable);
        }
    }

    private void deleteTaskIfMarkedDeletable(View view, Task task, Task deleteable) {
        if(taskIsDeletable(task, deleteable)){
            Animation fadeout = AnimationHelper.createFadeoutAnimation();
            deleteOnAnimationComplete(fadeout, task);
            animate(view, fadeout);
        }
    }

    private static boolean taskIsDeletable(Task task, Task deleteable) {
        return task.getTitle().equals(deleteable.getTitle());
    }

    private void deleteOnAnimationComplete(Animation fadeout, final Task task) {
        fadeout.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                tasks.remove(task);
                deleteableTasks.remove(task);
                notifyDataSetChanged();
            }
        });
    }

    private static void animate(View view, Animation animation) {
        view.startAnimation(animation);
    }


}
