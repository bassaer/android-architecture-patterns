package com.github.bassaer.todomvp.data.source

import com.github.bassaer.todomvp.data.Task

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * Created by nakayama on 2018/03/19.
 */
class TasksRepository(
        val tasksRemoteDataSource: TasksDataSource,
        val tasksLocalDataSource: TasksDataSource
) : TasksDataSource {
    var cachedTasks: LinkedHashMap<String, Task> = LinkedHashMap()
    var cacheIsDirty = false

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        if (cachedTasks.isEmpty() && !cacheIsDirty) {
            callback.onTasksLoaded(ArrayList(cachedTasks.values))
            return
        }
        if (cacheIsDirty) {
            getTasksFromRemoteDataSource(callback)
        } else {
            tasksLocalDataSource.getTasks(object : TasksDataSource.LoadTasksCallback {
                override fun onTasksLoaded(tasks: List<Task>) {
                    refreshCache(tasks)
                    callback.onTasksLoaded(ArrayList(cachedTasks.values))
                }

                override fun onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback)
                }
            })
        }
    }

    override fun saveTask(task: Task) {
        cacheAndPerform(task) {
            tasksRemoteDataSource.saveTask(task)
            tasksLocalDataSource.saveTask(task)
        }
    }

    override fun completeTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = true
            tasksRemoteDataSource.completeTask(task)
            tasksLocalDataSource.completeTask(task)
        }
    }

    override fun completeTask(taskId: String) {
        getTaskWithId(taskId)?.let {
            completeTask(it)
        }
    }

    override fun activateTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = false
            tasksRemoteDataSource.activateTask(it)
            tasksLocalDataSource.activateTask(it)
        }
    }

    override fun activateTask(taskId: String) {
        getTaskWithId(taskId)?.let {
            activateTask(it)
        }
    }

    override fun cleanCompletedTasks() {
        tasksRemoteDataSource.cleanCompletedTasks()
        tasksLocalDataSource.cleanCompletedTasks()
        cachedTasks = cachedTasks.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTasksCallback) {
        val taskInCache = getTaskWithId(taskId)
        if (taskInCache != null) {
            callback.onTaskLoaded(taskInCache)
            return
        }
        tasksLocalDataSource.getTask(taskId, object : TasksDataSource.GetTasksCallback {
            override fun onTaskLoaded(task: Task) {
                cacheAndPerform(task) {
                    callback.onTaskLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun refreshTasks() {
        cacheIsDirty = true
    }

    override fun deleteAllTasks() {
        tasksRemoteDataSource.deleteAllTasks()
        tasksLocalDataSource.deleteAllTasks()
        cachedTasks.clear()
    }

    override fun deleteTask(taskId: String) {
        tasksRemoteDataSource.deleteTask(taskId)
        tasksLocalDataSource.deleteTask(taskId)
        cachedTasks.remove(taskId)
    }

    private fun refreshCache(tasks: List<Task>) {
        cachedTasks.clear()
        tasks.forEach {

        }
    }

    private fun refreshLocalDataSource(tasks: List<Task>) {
       tasksLocalDataSource.deleteAllTasks()
       for (task in tasks) {
           tasksLocalDataSource.saveTask(task)
       }
    }

    private fun getTaskWithId(id: String) = cachedTasks[id]

    private inline fun cacheAndPerform(task: Task, perform: (Task) -> Unit) {
        val cachedTask = Task(task.title, task.description, task.id).apply {
            isCompleted = task.isCompleted
        }
        cachedTasks.put(cachedTask.id, cachedTask)
        perform(cachedTask)
    }

    private fun getTasksFromRemoteDataSource(callback: TasksDataSource.LoadTasksCallback) {
        tasksRemoteDataSource.getTasks(object : TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                refreshCache(tasks)
                refreshLocalDataSource(tasks)
                callback.onTasksLoaded(ArrayList(cachedTasks.values))
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    companion object {
        private var INSTANCE: TasksRepository? = null

        @JvmStatic
        fun getInstance(tasksRemoteDataSource: TasksDataSource,
                        tasksLocalDataSource: TasksDataSource): TasksRepository {
            return INSTANCE ?: TasksRepository(tasksRemoteDataSource, tasksLocalDataSource)
                    .apply { INSTANCE = this }
        }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}