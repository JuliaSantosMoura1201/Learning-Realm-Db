package com.example.realmdb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import io.realm.Realm
import java.lang.Exception

class AddNotesActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveNotesButton: Button
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)

        titleEditText = findViewById(R.id.title_EditText)
        descriptionEditText = findViewById(R.id.description_EditText)
        saveNotesButton = findViewById(R.id.saveNotesButton)
        realm = Realm.getDefaultInstance()

        saveNotesButton.setOnClickListener {
            addNotesToDB()
        }
    }

    private fun addNotesToDB(){
        try {

            //Auto Increment ID
            realm.beginTransaction()

            val currentIdNumber: Number? = realm.where(Notes::class.java).max("id")
            val nextID: Int

            nextID = if (currentIdNumber == null){
                1
            }else{
                currentIdNumber.toInt() + 1
            }

            val notes = Notes()
            notes.title = titleEditText.text.toString()
            notes.description = descriptionEditText.text.toString()
            notes.id = nextID

            realm.copyToRealmOrUpdate(notes)
            realm.commitTransaction()

            Toast.makeText(this, "Notes Added Successfully", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }catch (e: Exception){
            Toast.makeText(this, "Failed $e", Toast.LENGTH_SHORT).show()
        }
    }
}
