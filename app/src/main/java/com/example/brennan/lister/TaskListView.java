package com.example.brennan.lister;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.widget.ListView;
import android.widget.AdapterView;

import java.util.ArrayList;

import com.example.brennan.lister.util.AnimationHelper;

/**
 * Created by Brennan on 2/25/2017.
 */

public class TaskListView extends ListView {

    private TaskAdapter taskAdapter;

    public TaskListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TaskListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TaskListView(Context context) {
        super(context);
        init();
    }


    public void addTask(ArrayList<Task> tasks) {
        taskAdapter = new TaskAdapter(getContext(), tasks);
        setAdapter(taskAdapter);
    }

    private void init() {
        setPopulationAnimation();
    }

    // Add a new animation for when the view is laid out
    // this will add our items to the list in a nice controlled fashion top to bottom
    private void setPopulationAnimation() {
        AnimationSet set = new AnimationSet(true);

        Animation animation = AnimationHelper.createFadeInAnimation();
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        setLayoutAnimation(controller);
    }
}
