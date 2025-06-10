package com.base.app.baseapp.model

data class FoodItem(
    val name: String,
    val category: String,
    val accuracy: Int,
    val imageUrl: String,
    var isChecked: Boolean = false
)
