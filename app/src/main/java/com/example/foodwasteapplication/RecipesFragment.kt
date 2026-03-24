package com.example.foodwasteapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.LinearLayout
import android.widget.TextView

class RecipesFragment : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? // used to create the fragment UI from the XML.
    {
        return inflater.inflate(R.layout.fragment_recipes, container, false) // inflater converts XML to View, container is the parent layout.
    }

    override fun onResume() // when the fragment becomes visible and interactive.
    {
        super.onResume()
        (activity as MainActivity).setTitleText("Recipes") // cast activity to mainActivity to be able to call the setTitleText Function.
        (activity as MainActivity).hideSystemUI()
        loadRecipes()
    }

    private fun loadRecipes()
    {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val items = db.foodItemDao().getAll()

            val recipes = generateRecipes(items)

            showRecipes(recipes)
        }
    }

    private fun generateRecipes(items:List<FoodItem>): List<String>
    {
        val categories = items.map { it.category }

        val recipes = mutableListOf<String>()

        fun has(cat: String) = categories.contains(cat)

        if (has("pastas") && has("cheeses"))
        {
            recipes.add("Mac & Cheese")
        }

        return recipes
    }

    private fun showRecipes(recipes: List<String>)
    {
        val container = view?.findViewById<LinearLayout>(R.id.recipeContainer)

        container?.removeAllViews()

        for (recipe in recipes)
        {
            val textView = TextView(requireContext())
            textView.text = recipe
            textView.textSize = 18f
            textView.setPadding(16, 16, 16, 16)

            container?.addView(textView)
        }
    }
}
