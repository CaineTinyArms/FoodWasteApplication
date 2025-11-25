package com.example.foodwasteapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? // used to create the fragment UI from the XML.
    {
        return inflater.inflate(R.layout.fragment_list, container, false) // inflater converts XML to View, container is the parent layout.
    }

    override fun onResume() // when the fragment becomes visible and interactive.
    {
        super.onResume()
        (activity as MainActivity).setTitleText("Food List") // cast activity to mainActivity to be able to call the setTitleText Function.
    }
}



