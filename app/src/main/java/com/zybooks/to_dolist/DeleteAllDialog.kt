package com.zybooks.to_dolist

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DeleteAllDialog: DialogFragment() {
    interface OnYesClickListener {
        fun onYesClick()
    }

    private lateinit var listener: OnYesClickListener

    override fun onCreateDialog(savedInstanceState: Bundle?)
            : Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.delete_all_dialog)
        builder.setMessage(R.string.delete_all_dialog_message)
        builder.setPositiveButton(R.string.yes) { _, _ ->
            listener.onYesClick()
        }
        builder.setNegativeButton(R.string.no, null)
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnYesClickListener
    }
}