package com.example.brennan.lister.util;

import com.example.brennan.lister.Task;
import com.example.brennan.lister.TaskAdapter;
import java.util.Comparator;

/**
 * Created by Brennan on 4/29/2017.
 */

public class Utility {

    public static void sortByPriorityDesc(TaskAdapter taskAdapter){
        taskAdapter.sort(new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return t2.getPriorityInt() - (t1.getPriorityInt());
            }
        });
        taskAdapter.notifyDataSetChanged();
    }

    public static void sortByPriorityAsc(TaskAdapter taskAdapter){
        taskAdapter.sort(new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return t1.getPriorityInt() - (t2.getPriorityInt());
            }
        });
        taskAdapter.notifyDataSetChanged();
    }

}
