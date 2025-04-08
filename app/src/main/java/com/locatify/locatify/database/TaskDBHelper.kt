package com.locatify.locatify.database


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.locatify.locatify.fragments.muiBind
import com.locatify.locatify.modals.TaskModal




class TaskDBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {


    companion object {
        private val DB_NAME: String = "taskDB"
        private val DB_VERSION: Int = 1
        private val TABLE_TASK: String = "task_table"
        private val KEY_ID: String = "id";
        private val KEY_TASK_NAME = "Name";
        private val KEY_TASK_DATE = "Date";
        private val KEY_TASK_TIME = "Time";
        private val KEY_TASK_LOC_LAT = "Latitude";
        private val KEY_TASK_LOC_LONG = "Longitude";
    }



    override fun onCreate(db: SQLiteDatabase?) {
        val query = "create table " + TABLE_TASK + "(" + KEY_ID + " integer primary key autoincrement, " + KEY_TASK_NAME +
                " text, " + KEY_TASK_DATE + " date, " + KEY_TASK_TIME + " time, " + KEY_TASK_LOC_LAT +
                " double(10, 4), " + KEY_TASK_LOC_LONG + " double(10, 4)" + ")"
        Log.d("SQL Log", "Create and running")
        db?.execSQL(query);
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists " + TABLE_TASK)
        onCreate(db)
    }



    fun addTask(task: TaskModal): Long {
        var db = this.writableDatabase;

        var values: ContentValues = ContentValues()
        values.put(KEY_TASK_NAME, task.taskName)
        values.put(KEY_TASK_DATE, task.taskDate)
        values.put(KEY_TASK_TIME, task.taskTime)
        values.put(KEY_TASK_LOC_LAT, task.taskLoc?.first)
        values.put(KEY_TASK_LOC_LONG, task.taskLoc?.second)

        val id = db.insert(TABLE_TASK, null, values)
        muiBind.recyclerView.adapter!!.notifyDataSetChanged()
        return id
    }

    fun updateTask(task: TaskModal) {
        var db = writableDatabase;
        var values: ContentValues = ContentValues();
        values.put(KEY_TASK_NAME, task.taskName)
        values.put(KEY_TASK_DATE, task.taskDate)
        values.put(KEY_TASK_TIME, task.taskTime)
        values.put(KEY_TASK_LOC_LAT, task.taskLoc?.first)
        values.put(KEY_TASK_LOC_LONG, task.taskLoc?.second)

        db.update(TABLE_TASK, values, KEY_ID + " = " + task.id, null)
        muiBind.recyclerView.adapter!!.notifyDataSetChanged()
    }

    fun deleteTask(id: Int) {
        var db = writableDatabase;

        db.delete(TABLE_TASK, KEY_ID + " = ? ", arrayOf(id.toString()))
        muiBind.recyclerView.adapter!!.notifyDataSetChanged()
    }

    fun fetchTaskList(): ArrayList<TaskModal> {
        var taskList: ArrayList<TaskModal> = ArrayList()

        var db = this.readableDatabase

        var cursor: Cursor = db.rawQuery("select * from " + TABLE_TASK, null)
        while (cursor.moveToNext()) {
            val id: Int = cursor.getLong(0).toInt()
            val taskName: String = cursor.getString(1)
            val taskDate: String = cursor.getString(2)
            val taskTime: String = cursor.getString(3)
            val taskLocLat: Double = cursor.getDouble(4)
            val taskLocLong: Double = cursor.getDouble(5)

            taskList.add(TaskModal(taskName, taskDate, taskTime, Pair(taskLocLat, taskLocLong), id))
        }
        cursor.close()
        return taskList
    }

}
