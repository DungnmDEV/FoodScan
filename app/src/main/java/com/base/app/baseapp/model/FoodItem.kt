package com.base.app.baseapp.model

data class FoodItem(
    val name: String,
    val imageUrl: String,
    val category: String,
    val gi: Int,
    val carb: Float,
    val chatXo: Float,
    val gl: Float,
    val typeGI: Int,
    val typeGL: Int,
    val khauPhanKhongBenh:String,
    val khauPhanTienTieuDuong:String,
    val khauPhanTieuDuong1:String,
    val khauPhanTieuDuong2:String,
    val khauPhanBienChungThan:String,
    val id: Int,
    var isChecked: Boolean = false,
    var accuracy: Float = 0f,
)
