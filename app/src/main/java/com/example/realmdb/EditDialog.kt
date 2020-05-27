package com.example.realmdb

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

class EditDialog : DialogFragment() {

    internal lateinit var parentActivity: FragmentActivity
    private lateinit var btn : Button
    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var alertDialog: AlertDialog.Builder

    companion object{
        fun newInstance(activity: FragmentActivity): DialogFragment{
            val dialog = EditDialog()
            dialog.parentActivity = activity
            return dialog
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view= View.inflate(context, R.layout.edit_fragment, null)

        alertDialog = AlertDialog.Builder(parentActivity)
        alertDialog.setView(view)

        btn = view.findViewById(R.id.btnEdit)
        title = view.findViewById(R.id.editTitle)
        description = view.findViewById(R.id.editDescription)

        return alertDialog.create()
    }

    override fun onStart() {
        super.onStart()
        btn.setOnClickListener {
            (activity as MainActivity).titleDialog = title.text.toString()
            (activity as MainActivity).descriptionDialog = description.text.toString()
            (activity as MainActivity).editRealm()
            this.dialog?.dismiss()
        }
    }

}