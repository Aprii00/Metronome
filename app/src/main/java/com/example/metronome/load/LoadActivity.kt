package com.example.metronome.load

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.metronome.R
import com.example.metronome.database.AppDatabase
import com.example.metronome.database.Record
import com.example.metronome.load.recyclerView.MyAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoadActivity : AppCompatActivity(), MyAdapter.OnItemClickListener, MyAdapter.OnItemLongClickListener{
    private lateinit var database : AppDatabase
    private var myList = emptyList<Record>().toMutableList()
    private var adapter = MyAdapter(myList, this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.load_acttivity_layout)

        val myRecycler = findViewById<RecyclerView>(R.id.MyRecyclerView)

        myRecycler.adapter = adapter
        myRecycler.layoutManager = LinearLayoutManager(this)
        myRecycler.setHasFixedSize(true)

        myRecycler.addItemDecoration(DividerItemDecoration(myRecycler.context, DividerItemDecoration.VERTICAL))
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

            myList = database.recordDao().getAll().toMutableList()

            runOnUiThread {
                adapter.addList(myList)
            }
        }
    }

    override fun onItemClick(position: Int) {
        val myIntent = Intent()
        myIntent.putExtra("keyHzR", myList[position].hz).putExtra("keyTimeSignatureR",  myList[position].timeSignature).putExtra("keyBpmR",  myList[position].bpm)
        setResult(Activity.RESULT_OK, myIntent)
        finish()
    }

    override fun onItemLongClick(position: Int) {
        Log.e("position", position.toString())
        GlobalScope.launch {
            database.recordDao().delete(myList[position])
            myList.removeAt(position)
            runOnUiThread {
                adapter.notifyItemRemoved(position)
            }
        }
    }
}