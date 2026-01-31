package com.example.foodwasteapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FoodItem::class], version = 1)

// Main database class, extends Rooms base class.
abstract class AppDatabase : RoomDatabase() {

    abstract fun foodItemDao(): FoodItemDao // Gets access to the DAO (where the queries are held).

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null // Stores one instance of the database. Volatile is used for thread safety.

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) { // If an instance exists, return it.
                val instance = Room.databaseBuilder( // Otherwise, create one.
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