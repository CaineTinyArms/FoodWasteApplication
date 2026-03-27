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
import android.widget.ImageView

data class Recipe(
    val name: String,
    val imageRes: Int,
    val ingredientsUsed: List<String>
)

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

    private fun generateRecipes(items: List<FoodItem>): List<Recipe>
    {
        val recipes = mutableListOf<Recipe>()

        fun has(cat: String) = items.any { it.category == cat }

        fun getItemsForCategory(cat: String): List<String> {
            return items
                .filter { it.category == cat }
                .map { it.name }
        }

        if (has("pastas") && has("cheeses"))
        {
            val pastaItems = getItemsForCategory("pastas")
            val cheeseItems = getItemsForCategory("cheeses")

            recipes.add(
                Recipe(
                    name = "Mac & Cheese",
                    imageRes = android.R.drawable.ic_menu_gallery,
                    ingredientsUsed = (pastaItems + cheeseItems).distinct()
                )
            )
        }

        if (recipes.isEmpty())
        {
            recipes.add(
                Recipe(
                    name = "No recipes found",
                    imageRes = android.R.drawable.ic_menu_report_image,
                    ingredientsUsed = emptyList()
                )
            )
        }

        return recipes
    }

    private fun showRecipes(recipes: List<Recipe>)
    {
        val container = view?.findViewById<LinearLayout>(R.id.recipeContainer)

        container?.removeAllViews()

        for (recipe in recipes)
        {
            val recipeLayout = LinearLayout(requireContext())
            recipeLayout.orientation = LinearLayout.VERTICAL
            recipeLayout.setPadding(16, 16, 16, 16)

            val title = TextView(requireContext())
            title.text = recipe.name
            title.textSize = 20f

            val image = ImageView(requireContext())
            image.setImageResource(recipe.imageRes)
            image.layoutParams = LinearLayout.LayoutParams(200, 200)

            val ingredients = TextView(requireContext())
            ingredients.text = "Uses: ${recipe.ingredientsUsed.joinToString(", ")}"
            ingredients.textSize = 14f

            recipeLayout.addView(title)
            recipeLayout.addView(image)
            recipeLayout.addView(ingredients)

            container?.addView(recipeLayout)
        }
    }
}
