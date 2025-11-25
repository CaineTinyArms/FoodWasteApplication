package com.example.foodwasteapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ScanFragment : Fragment()
{
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onResume()
    {
        super.onResume()
        (activity as MainActivity).setTitleText("Scan Item")
    }
}