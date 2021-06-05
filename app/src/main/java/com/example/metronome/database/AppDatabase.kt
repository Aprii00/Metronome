package com.example.metronome.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [(Record::class)], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao() : RecordDAO
}