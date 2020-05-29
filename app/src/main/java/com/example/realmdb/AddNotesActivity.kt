package com.example.realmdb

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.example.realmdb.notify.AlarmScheduler
import com.example.realmdb.notify.NotificationHelper
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_add_notes.*
import java.io.IOException
import java.lang.Exception
import java.util.*

class AddNotesActivity : AppCompatActivity(){

    private lateinit var realm: Realm
    private var filePath: Uri? = null
    private var notificate: Boolean = false
    private var alarm: Boolean = false
    var id : Int = 0

    companion object{
        const val PICK_IMAGE_REQUEST = 1234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)

        realm = Realm.getDefaultInstance()


        val intent = intent
        val bundle :Bundle? = intent.extras
        id = bundle?.getInt("id") ?:0

        if(validateId()){
            fieldComponents()
        }

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

        editDate.setOnClickListener {
            dpd()
        }

        editTime.setOnClickListener{
            displayTimeDialog()
        }
    }

    private fun validateId(): Boolean{
        if(id != 0){
            return true
        }
        return false
    }

    private fun showMap(){
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("address", editPlace.text.toString())
        startActivity(intent)
    }

    private fun fieldComponents(){
        val tasks: RealmResults<Notes> = realm.where(Notes::class.java).findAll()
        val notes: Notes? = tasks.where().equalTo("id", id).findFirst()

        title_EditText.setText(notes?.title)
        description_EditText.setText(notes?.description)
        editPlace.setText(notes?.place)
        editDate.text = notes?.day.plus("/").plus(notes?.month).plus("/").plus(notes?.year)
        editTime.text = notes?.hour.plus(":").plus(notes?.minute)
    }

    private fun addNotesToDB(){
        try {
            realm.beginTransaction()

            if(!validateId()){
               id = autoIncrementId()
            }
            realm.copyToRealmOrUpdate(setNotesData(id))
            realm.commitTransaction()

            backMainActivity()

        }catch (e: Exception){
            Toast.makeText(this, "Failed $e", Toast.LENGTH_SHORT).show()
        }
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

    private fun dpd(){
        val c = Calendar.getInstance()
        val myYear = c.get(Calendar.YEAR)
        val myMonth = c.get(Calendar.MONTH)
        val myDay = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            var newMonth: String = month.toString()
            if(month <= 9){
                newMonth = "0$month"
            }
            editDate.text  = dayOfMonth.toString().plus("/").plus(newMonth).plus("/").plus(year)
        }, myYear, myMonth, myDay)
        return dpd.show()
    }

    private fun displayTimeDialog(){
        val hour  = 0
        val min = 0
        val tpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            var newHour = hourOfDay.toString()
            var newMinute = minute.toString()


            if(hourOfDay < 10){
                newHour = "0$newHour"
            }
            if (minute < 10){
                newMinute = "0$newMinute"
            }

            editTime.text = newHour.plus(":").plus(newMinute)
        }, hour, min, false)
        tpd.show()
    }
}
