package com.example.foodwasteapplication

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hideSystemUI()

        // Loads ListFragment as the default.
        loadFragment(ListFragment())

        // Gets references for the navigation buttons.
        val homeButton = findViewById<ImageButton>(R.id.homeButton)
        val scanButton = findViewById<ImageButton>(R.id.scanButton)
        val recipesButton = findViewById<ImageButton>(R.id.recipesButton)

        // Loads the respective fragment when the buttons are pressed.
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

    @Suppress("DEPRECATION")
    fun hideSystemUI()
    {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }



    private fun loadFragment(fragment: Fragment)
    {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
    }

    // Called from fragments to update the title text.
    fun setTitleText(text: String)
    {
        val titleView = findViewById<TextView>(R.id.topTitle)
        titleView.text = text
    }
}
