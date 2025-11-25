package com.example.foodwasteapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

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
    }
}
