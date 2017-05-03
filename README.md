# Lister

This app's purpose is to create and set tasks with priority to be viewable for different dates. 
It also supports a view to create persistent tasks which exist until they are dismissed.

### Start the app
Main activity: this view defaults to the date of "today" when you open the app. If you have any tasks already scheduled for today, they will show in this view.
To create a task: tap the floating action button. 

#### Creating a Task
Fill out the task name. The date is auto-populated to be the date in the title menu of the activity where the floating action button was activated. 
Assign a priority of low, normal or high to a task. The default priority is low. Click create to add the task to the list.

Created tasks appear in the list in the order they were created by default. When a task is finished, clicking "Done" will dismiss the task with a slideout animation.
In a List, tasks are able to be sorted by priority (descending and ascending). To sort, tap the overflow menu to choose your sorting method.


#### Scheduling a Task
To choose a different date view, tap the calendar icon in the title menu. This brings up the calendar view. By default, "today" will be selected on the calendar. 
Choose a different date and click the floating action button to return to the main List view. If tasks exist for the selected date, they will appear.
Otherwise the user can create tasks for the new date in this view, as normal.

#### Persistent List
To access the persistent tasks (which are not tied to specific date), tap the line-item list icon in the top menu from the main activity.
This will show the persistent task list. These tasks are separate from all other tasks and this feature is useful when you have a looming task which cannot necessarily be 
assigned to a known date, such as "Rake the leaves- Priority: Normal."