package com.example.metronome.save

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.metronome.R
import com.example.metronome.database.AppDatabase
import com.example.metronome.database.Record
import kotlinx.android.synthetic.main.save_acttivity_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class SaveActivity : AppCompatActivity(){
    private lateinit var database : AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.save_acttivity_layout)

        GlobalScope.launch {
            try {
                database = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "records.db"
                ).fallbackToDestructiveMigration().build()
            } catch (e: Exception) {
                Log.d("record2021", e.message.toString())
            }
        }
    }

    fun saveNewRecord(view: View) {
        GlobalScope.launch {
            database.recordDao().insert(Record(
                name = editTextName.text.toString(),
                hz = intent.getIntExtra("keyHz", 60),
                timeSignature = intent.getIntExtra("keyTimeSignature", 4),
                bpm = intent.getIntExtra("keyBpn", 60),
                timeStamp = Date().time
            ))
        }

        val myIntent = Intent()
        setResult(Activity.RESULT_OK, myIntent)
        finish()
    }
}