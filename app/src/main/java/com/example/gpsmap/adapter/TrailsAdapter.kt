package com.example.gpsmap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gpsmap.R
import com.example.gpsmap.database.entity.TrailModel
import com.example.gpsmap.databinding.TrailItemBinding

class TrailsAdapter(private val listener: Listener) : ListAdapter<TrailModel, TrailsAdapter.ItemHolder>(ItemComparator()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trail_item, parent, false)
        return ItemHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position))
    }


    class ItemHolder(view: View, private val listener: Listener): RecyclerView.ViewHolder(view), View.OnClickListener {
        private val binding = TrailItemBinding.bind(view)
        private var trailModelTemp: TrailModel? = null
        init {
            binding.imageButtonDeleteItem.setOnClickListener(this)
            binding.cvTrailItem.setOnClickListener(this)
        }

        fun setData(trailModel: TrailModel) = with(binding) {
            trailModelTemp = trailModel
            tvDateItem.text = trailModel.date
            tvDistanceItem.text = trailModel.distance
            tvTimeItem.text = trailModel.time
            tvAverageVelocityItem.text= trailModel.averageVelocity
        }


        // view - это нажатое view(view на которое мы нажали) - имеено отдельное view внутри item
        // Variant 1
        /*override fun onClick(view: View) {
            when(view.id) {
           R.id.cvTrailItem -> trailModelTemp?.let { listener.onClick(it, ClickType.OPEN) }
           R.id.imageButtonDeleteItem -> trailModelTemp?.let { listener.onClick(it, ClickType.DELETE) }
            }
        }
    }*/

        // Variant 2
        override fun onClick(view: View) {
           val type = when(view.id) {
                R.id.cvTrailItem -> ClickType.OPEN
                R.id.imageButtonDeleteItem -> ClickType.DELETE
                else -> ClickType.OPEN
           }
            trailModelTemp?.let { listener.onClick(it, type) }
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

    interface Listener {
        fun onClick(model: TrailModel, clickType: ClickType)
    }

    enum class ClickType{
        DELETE,
        OPEN
    }


}