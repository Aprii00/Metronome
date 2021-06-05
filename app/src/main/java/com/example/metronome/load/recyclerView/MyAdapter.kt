package com.example.metronome.load.recyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.metronome.R
import com.example.metronome.database.Record
import java.text.SimpleDateFormat
import java.util.*

class MyAdapter(private var recordsList : List<Record>, private val listener: OnItemClickListener, private val listener2: OnItemLongClickListener) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    public fun addList(newList: List<Record>){
        recordsList = newList
        notifyDataSetChanged()
    }

    inner class MyViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView), View.OnClickListener, View.OnLongClickListener {
        val nameText : TextView = itemView.findViewById(R.id.text_item_list_name)
        val dateText : TextView = itemView.findViewById(R.id.text_item_list_date)

        init{
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this);
        }
        override fun onClick(v: View?){
            val position : Int = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            val position : Int = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener2.onItemLongClick(position)
            }
            return true
        }

    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    interface OnItemLongClickListener{
        fun onItemLongClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return recordsList.size
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        val currentItem = recordsList[position]
        holder.nameText.text = currentItem.name
        holder.dateText.text = SimpleDateFormat("dd/MM/yy HH:mm").format(Date(currentItem.timeStamp)).toString()
    }

}
