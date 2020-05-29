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
import com.example.realmdb.notify.AlarmScheduler
import com.example.realmdb.notify.NotificationHelper
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_add_notes.*
import java.io.IOException
import java.lang.Exception

class AddNotesActivity : AppCompatActivity(){

    private lateinit var realm: Realm
    private var filePath: Uri? = null
    private var notificate: Boolean = false
    private var alarm: Boolean = false


    companion object{
        const val PICK_IMAGE_REQUEST = 1234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)

        realm = Realm.getDefaultInstance()

        saveNotesButton.setOnClickListener {
            addNotesToDB()
            if(alarm){
                AlarmScheduler.scheduleAlarmsForReminder(
                    this,
                    setNotesData(autoIncrementId()))
            }
            if(notificate){
                NotificationHelper.createSampleDataNotification(this@AddNotesActivity, setNotesData(autoIncrementId()), false)
            }
        }

        constraint.setOnClickListener {
            showFileChooser()
        }

        seeMapIV.setOnClickListener {
            showMap()
        }

        switch1.setOnClickListener {
            switch1.setImageResource(R.drawable.ic_notifications_active_black_24dp)
            notificate = true
        }
        switchTime.setOnClickListener {
            switchTime.setImageResource(R.drawable.ic_alarm_on_black_24dp)
            alarm = true
        }
    }

    private fun showMap(){
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("address", editPlace.text.toString())
        startActivity(intent)
    }

    private fun addNotesToDB(){
        try {
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(setNotesData(autoIncrementId()))
            realm.commitTransaction()

            backMainActivity()

        }catch (e: Exception){
            Toast.makeText(this, "Failed $e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotification(){



    }

    private fun autoIncrementId(): Int{
        val currentIdNumber: Number? = realm.where(Notes::class.java).max("id")
        val nextID: Int

        nextID = if (currentIdNumber == null){
            1
        }else{
            currentIdNumber.toInt() + 1
        }
        return nextID
    }
    private fun setNotesData(nextID: Int): Notes{
        val notes = Notes()
        notes.title = title_EditText.text.toString()
        notes.description = description_EditText.text.toString()
        notes.id = nextID

        val date =  editDate.text.toString()
        notes.day = configDay(date)
        notes.month = configMonth(date)
        notes.year = configYear(date)

        val time = editTime.text.toString()
        notes.hour = configHour(time)
        notes.minute = configMinute(time)
        notes.place = editPlace.text.toString()
        notes.image = filePath.toString()
        return notes
    }

    private fun configDay(date: String): String{
        return date.substring(0, 2)
    }

    private fun configMonth(date: String): String{
        return date.substring(3, 5)
    }

    private fun configYear(date: String): String{
        return date.substring(6, 10)
    }

    private fun configHour(time: String): String{
        return time.substring(0, 2)
    }

    private fun configMinute(time: String): String{
        return time.substring(3,5)
    }

    private fun backMainActivity(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showFileChooser(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            filePath = data.data
            try {
                configImageAppearance()
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun configImageAppearance(){
        viewFlipper.displayedChild = viewFlipper.displayedChild + 1
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
        imageNote.setImageBitmap(bitmap)
    }
}
