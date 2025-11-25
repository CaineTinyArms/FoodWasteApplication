package com.example.foodwasteapplication

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load the home screen by default
        loadFragment(ListFragment())

        // Get references to UI elements
        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        val scanButton = findViewById<ImageButton>(R.id.scanButton)
        val recipesButton = findViewById<ImageButton>(R.id.recipesButton)

        // Bottom navigation button listeners
        homeButton.setOnClickListener {
            loadFragment(ListFragment())
        }

        scanButton.setOnClickListener {
            loadFragment(ScanFragment())
        }

        recipesButton.setOnClickListener {
            loadFragment(RecipesFragment())
        }
    }

    // Function to swap fragments
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Allow fragments to update the title bar
    fun setTitleText(text: String) {
        val titleView = findViewById<TextView>(R.id.topTitle)
        titleView.text = text
    }
}
