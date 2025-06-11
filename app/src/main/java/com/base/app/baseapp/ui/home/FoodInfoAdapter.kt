package com.base.app.baseapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.base.app.baseapp.databinding.ItemFoodCard2Binding
import com.base.app.baseapp.databinding.ItemFoodCardBinding
import com.base.app.baseapp.model.FoodItem
import com.base.app.baseapp.utils.Utils
import com.base.app.baseapp.utils.Utils.gone
import com.base.app.baseapp.utils.Utils.visible
import com.bumptech.glide.Glide

class FoodInfoAdapter: RecyclerView.Adapter<FoodInfoAdapter.FoodViewHolder>() {

    private var foodList: MutableList<FoodItem> = mutableListOf()

    inner class FoodViewHolder(val binding: ItemFoodCard2Binding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodCard2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun getItemCount(): Int = foodList.size

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = foodList[position]
        val binding = holder.binding

        Glide.with(binding.root.context).load(item.imageUrl).into(binding.imgFood)
        binding.tvCategory.text = item.category
        binding.tvName.text = item.name
        val typeGi = when {
            item.typeGI == 1 -> "Thấp"
            item.typeGI == 2 -> "Trung bình"
            else -> "Cao"
        }
        val typeGl = when {
            item.typeGL == 1 -> "Thấp"
            item.typeGL == 2 -> "Trung bình"
            else -> "Cao"
        }
        val khauPhan = when {
            Utils.currentUser?.state == 0-> item.khauPhanKhongBenh
            Utils.currentUser?.state == 1-> item.khauPhanTienTieuDuong
            Utils.currentUser?.state == 2-> item.khauPhanTieuDuong1
            Utils.currentUser?.state == 3-> item.khauPhanTieuDuong2
            else -> item.khauPhanBienChungThan
        }

        binding.tvInfor.text = "Chỉ số GI: ${item.gi}\n" +
            "\n" +
            "Chỉ số Carb (g/100g): ${item.carb}\n" +
            "\n" +
            "Chất xơ: (g/100g): ${item.chatXo}\n" +
            "\n" +
            "Chỉ số GL: ${item.gl}\n" +
            "\n" +
            "Nhóm GI: $typeGi\n" +
            "\n" +
            "Nhóm GL: $typeGl\n" +
            "\n" +
            "Khẩu phần nên ăn: $khauPhan"

        binding.cv.setOnClickListener {
            if(binding.tvInfor.isVisible){
                binding.tvInfor.gone()
                binding.btnMore.rotation = 0f
            }else{
                binding.tvInfor.visible()
                binding.btnMore.rotation = -90f
            }
        }
    }

    fun updateList(newList: List<FoodItem>) {
        foodList.clear()
        foodList.addAll(newList)
        notifyDataSetChanged()
    }

}
