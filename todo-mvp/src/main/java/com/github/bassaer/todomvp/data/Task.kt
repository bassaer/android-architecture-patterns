package com.github.bassaer.todomvp.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * Model class for a Task
 * Created by nakayama on 2018/03/18.
 */
@Entity(tableName = "tasks")
data class Task (@ColumnInfo(name = "title") var title: String = "",
          @ColumnInfo(name = "description") var description: String = "",

          @PrimaryKey @ColumnInfo(name = "entryid") var id: String = UUID.randomUUID().toString()
) {
    @ColumnInfo(name = "completed") var isCompleted:Boolean = false

    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() && description.isEmpty()
}