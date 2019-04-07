package com.example.fiarebaseapp.views.fragments


import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.example.fiarebaseapp.R

class MainFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findNavController().navigate(R.id.action_mainFragment_to_productListFragment)
    }
}
