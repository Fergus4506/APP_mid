package com.example.mid_work

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MydbUserHelper(context: Context,name:String=database, factory:SQLiteDatabase.CursorFactory ?= null, version:Int = v):SQLiteOpenHelper(context,name,factory,version) {
    companion object{
        private const val database = "UserTable"
        private const val v = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
        CREATE TABLE IF NOT EXISTS UserTable (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT,
        needpay INTEGER
)
""".trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?,oldVersion:Int,newVersion:Int) {
        db?.execSQL("DROP TABLE IF EXISTS UserTable")
        onCreate(db)
    }
}