package com.example.realmdb

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

class DeleteDialog: DialogFragment() {

    internal lateinit var parentActivity: FragmentActivity

    companion object{
        fun newInstance(activity: FragmentActivity): DialogFragment{
            val dialog = DeleteDialog()
            dialog.parentActivity = activity
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Delete")
        alertDialog.setMessage("Are you sure that you want to delete this note?")
        alertDialog.setPositiveButton("yes") { _, _ ->
            (activity as MainActivity).deleteRealm()
            this.dialog?.dismiss()
        }
        alertDialog.setNegativeButton("no"
        ) { _, _ -> alertDialog.setCancelable(true)}
        return alertDialog.create()
    }
}