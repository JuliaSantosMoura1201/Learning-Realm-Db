package com.example.realmdb

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults
import kotlinx.android.synthetic.main.notes_rv_layout.view.*

class NotesAdapter (private val context: Context, private val notesList: RealmResults<Notes>, private val width: Int, private val listener: NotesListener)
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
        holder.itemView.titleResearchTV.text = notesList[position]!!.title
        holder.itemView.descResearchTV.text = notesList[position]!!.description
        holder.itemView.idResearchTV.text = notesList[position]!!.id.toString()
        holder.itemView.deleteIV.setOnClickListener {
            notesList[position]!!.id?.let { it1 -> listener.deleteNote(it1) }
        }
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v)

    interface NotesListener{
        fun deleteNote(id: Int)
    }
}

