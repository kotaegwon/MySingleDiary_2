package com.ko.mysingledairy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ko.mysingledairy.databinding.FragmentEditBinding
import timber.log.Timber

class EditFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView +")

        binding = FragmentEditBinding.inflate(inflater, container, false)

        Timber.d("onCreateView -")
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated +")

        super.onViewCreated(view, savedInstanceState)

        binding.closeButton.setOnClickListener(this)

        binding.moodSlider.addOnChangeListener { _, value, _ ->
            val mood = value.toInt()
            Timber.d("onViewCreated: moodSlider value = $mood")
        }
        Timber.d("onViewCreated +")

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.closeButton -> {
                findNavController().popBackStack()
            }
        }
    }
}