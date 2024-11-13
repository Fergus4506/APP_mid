package com.example.mid_work

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MydbCostHelper(context: Context,name:String=database, factory:SQLiteDatabase.CursorFactory ?= null, version:Int = v):SQLiteOpenHelper(context,name,factory,version) {
    companion object{
        private const val database = "costTable"
        private const val v = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
        CREATE TABLE IF NOT EXISTS costTable (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        item TEXT,
        time TEXT,
        month TEXT,
        year TEXT,
        cls TEXT,
        cost INTEGER,
        size INTEGER,
        using_cost INTEGER
)
""".trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?,oldVersion:Int,newVersion:Int) {
        db?.execSQL("DROP TABLE IF EXISTS costTable")
        onCreate(db)
    }
}