package com.example.realmdb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import io.realm.RealmResults
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var addNotes: FloatingActionButton
    private lateinit var notesRV: RecyclerView
    private lateinit var notesList: ArrayList<Notes>
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addNotes = findViewById(R.id.addNotes)
        notesRV = findViewById(R.id.notesRV)
        realm = Realm.getDefaultInstance()

        addNotes.setOnClickListener {
            startActivity(Intent(this, AddNotesActivity::class.java))
            finish()
        }

        notesRV.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        getAllNotes()

    }

    private fun getAllNotes(){
        notesList = ArrayList()
        notesList.clear()

        val results: RealmResults<Notes> = realm.where<Notes>(Notes::class.java).findAll()

        notesRV.adapter = NotesAdapter(this, results, getScreenWidth(), object : NotesAdapter.NotesListener {
            override fun deleteNote(id: Int) {

                val tasks: RealmResults<Notes> = realm.where(Notes::class.java).findAll()
                val task: Notes? = tasks.where().equalTo("id", id).findFirst()

                if(task!= null){
                    realm.beginTransaction()
                    task.deleteFromRealm()
                }

                realm.commitTransaction()
                getAllNotes()
            }

        })

        notesRV.adapter!!.notifyDataSetChanged()
    }

    private fun getScreenWidth(): Int{
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()
        getAllNotes()
        return true
    }
}
