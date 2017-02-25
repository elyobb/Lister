package com.example.brennan.lister.db;

import android.provider.BaseColumns;

/**
 * Stores constants for accessing Task table data.
 * Created by Brennan on 2/20/2017.
 */
public class TaskContract {
    public static final String DB_NAME = "com.example.brennan.lister.db";
    public static final int DB_VERSION = 2;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";

        public static final String COL_TASK_TITLE = "title";
        public static final String COL_TASK_PRIORITY = "priority";
        // the date the task is scheduled for
        public static final String COL_TASK_DATE = "date";
    }
}
