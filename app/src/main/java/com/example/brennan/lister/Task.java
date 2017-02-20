package com.example.brennan.lister;

/**
 * Represents a task from the task table in the db
 * Created by Brennan on 2/20/2017.
 */

public class Task {
    private String title;
    private String priority;
    private String date;

    public Task(String title, String priority, String date){
        this.title = title;
        this.priority = priority;
        this.date = date;
    }

    public String getTitle(){
        return title;
    }

    public String getPriority(){
        return priority;
    }

    public String getDate(){
        return date;
    }
}
