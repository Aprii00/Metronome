package com.example.metronome.database

import androidx.room.*

@Dao
interface RecordDAO {
    @Query("select * from records")
    fun getAll() : List<Record>

    @Insert
    fun insert(record : Record)

    @Delete
    fun delete(record : Record)
}