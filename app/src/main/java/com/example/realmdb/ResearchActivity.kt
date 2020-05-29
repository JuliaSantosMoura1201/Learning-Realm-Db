package com.example.realmdb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_research.*
import java.lang.Exception

class ResearchActivity : AppCompatActivity() {

    private var listOfItems = arrayOf("id", "title")
    var type = ""
    private lateinit var realm: Realm
    var idDialog = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_research)

        realm = Realm.getDefaultInstance()
        spinner!!.onItemSelectedListener
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOfItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null) {
                    type = parent.getItemAtPosition(position).toString()
                }
            }

        }

        btn_search.setOnClickListener { defineResearchType() }

        cardViewResearch.setOnClickListener {
            val intent = Intent(this, AddNotesActivity::class.java)
            intent.putExtra("id", idDialog)
            startActivity(intent)
        }
    }


    private fun defineResearchType(){
        val tasks: RealmResults<Notes> = realm.where(Notes::class.java).findAll()
        cardViewResearch.visibility = View.GONE

        if(type == "title"){
            makeResearchTitle(tasks)
            return
        }

        makeResearchId(tasks)
    }
    private fun makeResearchTitle(tasks: RealmResults<Notes>){

        val task: Notes? = tasks.where().equalTo(type, edtNote.text.toString()).findFirst()

        if(task!= null){
            fieldCardView(task)
        }else{
            showToast()
        }
    }

    private fun makeResearchId(tasks: RealmResults<Notes>){

        var note = 1

        try {
            note = edtNote.text.toString().toInt()
        }catch (e: Exception){
            showToast()
        }

        val task: Notes? = tasks.where().equalTo(type, note).findFirst()

        if(task!= null){
            fieldCardView(task)
        }else{
            showToast()
        }
    }

    private fun fieldCardView(task: Notes){
        cardViewResearch.visibility = View.VISIBLE
        idDialog = task.id!!
        titleResearchTV.text = task.title
        descResearchTV.text = task.description
        idResearchTV.text = task.id.toString()
    }

    private fun showToast(){
        Toast.makeText(this, "Esse item é inválido ou não existe\nPor favor tente novamente", Toast.LENGTH_SHORT).show()
    }
}
