package com.example.gpsmap.utils.dialog

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.example.gpsmap.R

object DialogGps {
    fun showGpsDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(R.string.connect_gps)
        dialog.setMessage(context.getString(R.string.gps_summary))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.gps_button_positive)) {
            _, _ -> listener.onClickDialogButton()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.gps_button_negative)) {
            _, _ -> dialog.dismiss()
        }
        dialog.show()
    }
    interface Listener {
        fun onClickDialogButton()
    }
}