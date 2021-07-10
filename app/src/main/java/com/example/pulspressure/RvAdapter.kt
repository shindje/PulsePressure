package com.example.pulspressure

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RvAdapter () : RecyclerView.Adapter<RvAdapter.DataViewHolder>() {
    private var data: ArrayList<Model> = arrayListOf()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val tvHigh: TextView = itemView.findViewById(R.id.tv_high)
        val tvLow: TextView = itemView.findViewById(R.id.tv_low)
        val tvPulse: TextView = itemView.findViewById(R.id.tv_pulse)

        fun bind(model: Model) {
            //tvTime =
            tvHigh.text = model.pressureHigh.toString()
            tvLow.text = model.pressureLow.toString()
            tvPulse.text = model.pulse.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder =
        DataViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_item, parent, false))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val model = data[position]
        holder.bind(model)
    }

    fun setData(newData: ArrayList<Model>) {
        data = newData
        notifyDataSetChanged()
    }
}