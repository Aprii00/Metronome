package com.example.metronome.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date



@Entity(tableName = "records")
data class Record(
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Int = 0,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "hz") var hz: Int,
        @ColumnInfo(name = "timeSignature") var timeSignature: Int,
        @ColumnInfo(name = "bpm") var bpm: Int,
        @ColumnInfo(name = "timeStamp") var timeStamp: Long
)