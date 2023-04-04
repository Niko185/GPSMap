package com.example.gpsmap.utils.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.gpsmap.R
import com.example.gpsmap.database.entity.TrailModel
import com.example.gpsmap.databinding.DialogSaveBinding

object DialogManager {
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

    fun showSaveDialog(context: Context, trailModel: TrailModel?, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val binding = DialogSaveBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val dialog = builder.create()

       binding.apply {
           val time = "Time: ${trailModel?.time}"
           val averageVelocity = "Average velocity: ${trailModel?.averageVelocity} km/h"
           val actionVelocity = "Action velocity: ${trailModel?.actionVelocity} km/h"
           val distance = "Distance: ${trailModel?.distance} km"


           tvTimeDialog.text = time
           tvAverageVelocityDialog.text = averageVelocity
           tvActionVelociryDialog.text = actionVelocity
           tvDistanceDialog.text = distance

           buttonSaveDialog.setOnClickListener{
               listener.onClickDialogButton()
                dialog.dismiss()
           }
           buttonCancelDialog.setOnClickListener {
                dialog.dismiss()
           }
       }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    interface Listener {
        fun onClickDialogButton()
    }
}