package com.github.bassaer.todomvp.tasks

import com.github.bassaer.todomvp.BasePresenter
import com.github.bassaer.todomvp.BaseView
import com.github.bassaer.todomvp.data.Task

/**
 * This specifies the contract between the view and the presenter.
 * Created by nakayama on 2018/03/18.
 */
interface TasksContract {
    interface View: BaseView<Presenter> {
        fun setLoadingIndicator(active: Boolean)
        fun showTasks(tasks: List<Task>)
        fun showAddTask()
        fun showTaskDetailsUi(taskId: String)
        fun showTaskMarkedActive()
        fun showCompletedTasksCleared()
        fun showLoadingTasksCleared()
        fun showNoTasks()
        fun showActiveFilterLabel()
        fun showCompletedFilterLabel()
        fun showAllFilters()
        fun showNoActiveTasks()
        fun showNoCompetedTasks()
        fun showSuccessfullySavedMessage()
        fun showFilteringPopUpMenu()
    }


    interface Presenter: BasePresenter {
        fun result(requestCode: Int, resultCode: Int)
        fun loadTasks(forceUpdate: Boolean)
        fun addNewTask()
        fun openTaskDetails(requestedTask: Task)
        fun completeTask(completedTask: Task)
        fun activeTask(activeTask: Task)
        fun clearCompletedTasks()
    }

}