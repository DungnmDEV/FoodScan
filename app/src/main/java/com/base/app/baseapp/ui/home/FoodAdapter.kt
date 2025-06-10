package com.base.app.baseapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.base.app.baseapp.R

class FoodAdapter(private val onClick: (String) -> Unit) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    private var foodList: List<String> = listOf()

    fun updateList(newList: List<String>) {
        foodList = newList
        notifyDataSetChanged()
    }

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int = foodList.size

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.tvFoodName.text = foodList[position]
        holder.itemView.setOnClickListener {
            onClick.invoke(foodList[position])
        }
    }
}
