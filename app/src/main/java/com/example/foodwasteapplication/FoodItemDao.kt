package com.example.foodwasteapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// This file is the SQL Queries that are used by the app when talking to the database.

@Dao
interface FoodItemDao {

    @Insert
    suspend fun insert(item: FoodItem): Long

    @Query("SELECT * FROM food_items ORDER BY expiryDateEpochDay ASC")
    suspend fun  getAll(): List<FoodItem>

    @Query("DELETE FROM food_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}