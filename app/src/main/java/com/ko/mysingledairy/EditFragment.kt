package com.ko.mysingledairy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import timber.log.Timber

class EditFragment : Fragment() {
    companion object{
        private const val TAG = "EditFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView +")
        return super.onCreateView(inflater, container, savedInstanceState)
        Timber.d("onCreateView -")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated +")

        super.onViewCreated(view, savedInstanceState)

        Timber.d("onViewCreated +")

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}