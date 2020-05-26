package com.example.realmdb

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults
import kotlinx.android.synthetic.main.notes_rv_layout.view.*

class NotesAdapter (private val notesList: RealmResults<Notes>, private val width: Int)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.notes_rv_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.cardNotes.layoutParams.width = (width/2) - 30
        holder.itemView.titleTV.text = notesList[position]!!.title
        holder.itemView.descTV.text = notesList[position]!!.description
        holder.itemView.idTV.text = notesList[position]!!.id.toString()
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v)
}