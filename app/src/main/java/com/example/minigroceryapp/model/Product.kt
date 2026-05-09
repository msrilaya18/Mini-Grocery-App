package com.example.minigroceryapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val category: String
) : Parcelable
