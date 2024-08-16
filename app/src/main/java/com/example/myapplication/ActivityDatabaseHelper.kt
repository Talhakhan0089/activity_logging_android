package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ActivityDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "activitylog.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "activities"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_USER = "user"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_START_TIME = "start_time"
        const val COLUMN_END_TIME = "end_time"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_NAME TEXT," +
                    "$COLUMN_USER TEXT," +
                    "$COLUMN_NOTES TEXT," +
                    "$COLUMN_START_TIME INTEGER," +
                    "$COLUMN_END_TIME INTEGER)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun insertActivity(name: String, user: String, notes: String, startTime: Long, endTime: Long): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_USER, user)
            put(COLUMN_NOTES, notes)
            put(COLUMN_START_TIME, startTime)
            put(COLUMN_END_TIME, endTime)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllActivities(): List<Activity> {
        val db = readableDatabase
        val projection = arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_USER, COLUMN_NOTES, COLUMN_START_TIME, COLUMN_END_TIME)
        val cursor = db.query(
            TABLE_NAME, projection, null, null, null, null, "$COLUMN_START_TIME ASC"
        )
        val activities = mutableListOf<Activity>()
        with(cursor) {
            while (moveToNext()) {
                val activity = Activity(
                    getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    getString(getColumnIndexOrThrow(COLUMN_NAME)),
                    getString(getColumnIndexOrThrow(COLUMN_USER)),
                    getString(getColumnIndexOrThrow(COLUMN_NOTES)),
                    getLong(getColumnIndexOrThrow(COLUMN_START_TIME)),
                    getLong(getColumnIndexOrThrow(COLUMN_END_TIME))
                )
                activities.add(activity)
            }
        }
        cursor.close()
        return activities
    }
}

