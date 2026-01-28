package com.example.foodwasteapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodItemDao {

    @Insert
    suspend fun insert(item: FoodItem): Long

    @Query("SELECT * FROM food_items ORDER BY expiryDateEpochDay ASC")
    suspend fun  getAll(): List<FoodItem>
}