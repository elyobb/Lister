package com.example.brennan.lister;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import org.junit.matchers.JUnitMatchers;

/**
 * Tests functionality of a Task object.
 */
public class TaskTest {

    private static final String TASK_TITLE = "Task Title";
    private static final String TASK_DATE = "04/15/2017";
    private static final String LOW_PRIORITY = "Low Priority";
    private static final String NORMAL_PRIORITY = "Normal Priority";
    private static final String HIGH_PRIORITY = "High Priority";

    /**
     * Tests that the correct task title is set and begotten
     */
    @Test
    public void taskTitleTest(){
        Task task = new Task(TASK_TITLE, NORMAL_PRIORITY, TASK_DATE);
        assertThat(task.getTitle(), is(TASK_TITLE));
    }

    /**
     * Tests that the correct priority is begotten when set to normal
     */
    @Test
    public void normalPriorityTest() {
        Task task = new Task(TASK_TITLE, NORMAL_PRIORITY, TASK_DATE);
        assertThat(task.getPriority(), is("Normal Priority"));
    }

    /**
     * Tests that the correct integer-priority is begotten when set as normal
     */
    @Test
    public void normalPriorityIntTest() {
        Task task = new Task(TASK_TITLE, NORMAL_PRIORITY, TASK_DATE);
        assertThat(task.getPriorityInt(), is(1));
    }

    /**
     * Tests that the correct priority is begotten when set to low priority
     */
    @Test
    public void lowPriorityTest() {
        Task task = new Task(TASK_TITLE, LOW_PRIORITY, TASK_DATE);
        assertThat(task.getPriority(), is("Low Priority"));
    }

    /**
     * Tests that the correct integer-priority is begotten when set to low
     */
    @Test
    public void lowPriorityIntTest() {
        Task task = new Task(TASK_TITLE, LOW_PRIORITY, TASK_DATE);
        assertThat(task.getPriorityInt(), is(0));
    }

    /**
     * Tests that the correct priority is begotten when set to high
     */
    @Test
    public void highPriorityTest() {
        Task task = new Task(TASK_TITLE, HIGH_PRIORITY, TASK_DATE);
        assertThat(task.getPriority(), is("High Priority"));
    }

    /**
     * Tests that the correct integer-priority is begotten when set to high
     */
    @Test
    public void highPriorityIntTest() {
        Task task = new Task(TASK_TITLE, HIGH_PRIORITY, TASK_DATE);
        assertThat(task.getPriorityInt(), is(2));
    }

    /**
     * Tests that the correct date is begotten
     */
    @Test
    public void dateTest(){
        Task task = new Task(TASK_TITLE, NORMAL_PRIORITY, TASK_DATE);
        assertThat(task.getDate(), is(TASK_DATE));
    }
}