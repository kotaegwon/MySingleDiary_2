package com.ko.mysingledairy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ko.mysingledairy.databinding.FragmentListBinding
import timber.log.Timber

class MainListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView +")

        binding = FragmentListBinding.inflate(layoutInflater)
        return binding.root

        Timber.d("onCreateView +")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated +")

        super.onViewCreated(view, savedInstanceState)

        Timber.d("onViewCreated +")

    }
}