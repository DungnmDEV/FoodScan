package com.base.app.baseapp.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.base.app.baseapp.R
import com.base.app.baseapp.model.FoodItem
import com.base.app.baseapp.utils.Utils.gone
import com.base.app.baseapp.utils.Utils.visible
import com.bumptech.glide.Glide

class FoodAdapter2(private val isSelect: Boolean = false, private val onItemClick: (FoodItem) -> Unit) :
    RecyclerView.Adapter<FoodAdapter2.FoodViewHolder>() {
    private var foodList: MutableList<FoodItem> = mutableListOf()

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFood: ImageView = itemView.findViewById(R.id.imgFood)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvAccuracy: TextView = itemView.findViewById(R.id.tvAccuracy)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_card, parent, false)
        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int = foodList.size

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = foodList[position]
        Glide.with(holder.itemView.context).load(item.imageUrl).into(holder.imgFood)
        holder.tvCategory.text = item.category
        holder.tvName.text = item.name
        holder.tvAccuracy.text = "Độ chính xác: ${String.format("%.2f", item.accuracy)}%"
        if (item.accuracy == 0f) {
            holder.tvAccuracy.text = "Độ chính xác: Tự chọn"
        }
        if (isSelect) {
            holder.checkBox.gone()
            holder.tvAccuracy.gone()
        } else {
            holder.checkBox.visible()
            holder.tvAccuracy.visible()
        }
        holder.checkBox.isChecked = item.isChecked

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->

            item.isChecked = isChecked
            Log.d("TAG123", "onBindViewHolder: ${item.name} ${item.isChecked}")
        }
        holder.itemView.setOnClickListener {
            onItemClick.invoke(item)
        }
    }

    fun getSelectedItems(): List<FoodItem> {
        foodList.forEach {
            Log.d("TAG123", "getSelectedItems: ${it.name} ${it.isChecked}")
        }
        return foodList.filter { it.isChecked }
    }


    fun updateList(newList: List<FoodItem>) {
        foodList.clear()
        foodList.addAll(newList)
        notifyDataSetChanged()
    }

    fun addNewFood(newFood: FoodItem) {
        foodList.add(newFood)
        notifyDataSetChanged()
    }

    fun getList(): List<FoodItem> {
        return foodList
    }
}
