package com.example.foodwasteapplication

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(ListFragment())

        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        val scanButton = findViewById<ImageButton>(R.id.scanButton)
        val recipesButton = findViewById<ImageButton>(R.id.recipesButton)

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

    private fun loadFragment(fragment: Fragment)
    {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun setTitleText(text: String)
    {
        val titleView = findViewById<TextView>(R.id.topTitle)
        titleView.text = text
    }
}
