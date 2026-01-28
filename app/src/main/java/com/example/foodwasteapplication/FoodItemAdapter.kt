package com.example.foodwasteapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class FoodItemAdapter(
    private var items: List<FoodItem> = emptyList()
) : RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder>() {

    fun submitList(newItems: List<FoodItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return FoodItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    class FoodItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.itemName)
        private val expiryText: TextView = itemView.findViewById(R.id.itemExpiry)

        fun bind(item: FoodItem) {
            nameText.text = item.name

            val expiryDate = LocalDate.ofEpochDay(item.expiryDateEpochDay)
            expiryText.text = "Expires: $expiryDate"
        }
    }
}