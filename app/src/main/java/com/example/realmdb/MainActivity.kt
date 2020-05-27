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
    var titleDialog = ""
    var descriptionDialog = ""
    var idDialog = 0
    val editDialog= EditDialog.newInstance(this)
    val deleteDialog= DeleteDialog.newInstance(this)

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

    }

    override fun onStart() {
        super.onStart()
        notesRV.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        getAllNotes()
    }

    private fun getAllNotes(){
        notesList = ArrayList()
        notesList.clear()

        val results: RealmResults<Notes> = realm.where<Notes>(Notes::class.java).findAll()

        notesRV.adapter = NotesAdapter(results, getScreenWidth(), object : NotesAdapter.NotesListener {
            override fun deleteNote(id: Int) {
                supportFragmentManager.beginTransaction().add(deleteDialog, null).commitAllowingStateLoss()
                idDialog = id
            }

            override fun editNote(id: Int) {
                supportFragmentManager.beginTransaction().add(editDialog, null).commitAllowingStateLoss()
                idDialog = id
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
        if(item.itemId == R.id.deleteOption){
            realm.beginTransaction()
            realm.deleteAll()
            realm.commitTransaction()
            getAllNotes()
        }else{
            startActivity(Intent(this, ResearchActivity::class.java))
        }

        return true
    }

    fun editRealm(){
        val notes: RealmResults<Notes> = realm.where(Notes::class.java).findAll()
        val note: Notes? = notes.where().equalTo("id", idDialog).findFirst()

        if(note != null){
            realm.beginTransaction()
            note.title = titleDialog
            note.description = descriptionDialog
            realm.copyToRealmOrUpdate(note)
        }
        realm.commitTransaction()
        getAllNotes()
    }

    fun deleteRealm(){
        val tasks: RealmResults<Notes> = realm.where(Notes::class.java).findAll()
        val task: Notes? = tasks.where().equalTo("id", idDialog).findFirst()

        if(task!= null){
            realm.beginTransaction()
            task.deleteFromRealm()
        }

        realm.commitTransaction()
        getAllNotes()
    }


}
