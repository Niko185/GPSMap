package com.example.gpsmap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.createBitmap
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gpsmap.R
import com.example.gpsmap.database.entity.TrailModel
import com.example.gpsmap.databinding.TrailItemBinding

class TrailsAdapter : ListAdapter<TrailModel, TrailsAdapter.ItemHolder>(ItemComparator()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trail_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position))
    }


    class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = TrailItemBinding.bind(view)

        fun setData(trailModel: TrailModel) = with(binding) {
            tvDateItem.text = trailModel.date
            tvDistanceItem.text = trailModel.distance
            tvTimeItem.text = trailModel.time
            tvAverageVelocityItem.text= trailModel.averageVelocity
        }
    }


    class ItemComparator() : DiffUtil.ItemCallback<TrailModel>(){
        override fun areItemsTheSame(oldItem: TrailModel, newItem: TrailModel): Boolean {
           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TrailModel, newItem: TrailModel): Boolean {
           return oldItem == newItem
        }

    }
}