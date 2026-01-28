package com.example.foodwasteapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FoodItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun foodItemDao(): FoodItemDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "food_waste.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}