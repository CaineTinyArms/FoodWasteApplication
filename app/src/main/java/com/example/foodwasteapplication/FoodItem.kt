package com.example.foodwasteapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items") // Tells Room (the database library) this class is a table in the database.

// FoodItem is used in the database, to represent one row.
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val barcode: String,
    val name: String,
    val imageUrl: String?,
    val expiryDateEpochDay: Long // stored as "days since 1970", as it makes it easier to sort.
)