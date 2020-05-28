package com.example.realmdb

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_add_notes.*
import java.io.IOException
import java.lang.Exception

class AddNotesActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveNotesButton: Button
    private lateinit var realm: Realm
    private var filePath: Uri? = null


    companion object{
        const val PICK_IMAGE_REQUEST = 1234
    }
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

        constraint.setOnClickListener {
            showFileChooser()
        }

        seeMapIV.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("address", editPlace.text.toString())
            startActivity(intent)
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
            notes.date = editDate.text.toString()
            notes.time = editTime.text.toString()
            notes.place = editPlace.text.toString()
            notes.image = filePath.toString()

            realm.copyToRealmOrUpdate(notes)
            realm.commitTransaction()

            Toast.makeText(this, "Notes Added Successfully", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }catch (e: Exception){
            Toast.makeText(this, "Failed $e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showFileChooser(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST &&
                resultCode == Activity.RESULT_OK &&
                data != null &&
                data.data != null){
            filePath = data.data

            try {
                println(viewFlipper.displayedChild)
                viewFlipper.displayedChild = viewFlipper.displayedChild + 1
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imageNote.setImageBitmap(bitmap)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
}
