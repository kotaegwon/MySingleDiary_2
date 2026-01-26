package com.ko.mysingledairy.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ko.mysingledairy.R
import com.ko.mysingledairy.databinding.FragmentEditBinding
import com.ko.mysingledairy.DiaryViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class EditFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentEditBinding

    private val viewModel: DiaryViewModel by viewModels()

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 위치 정보 Flow
                launch {
                    viewModel.cityDistrict.collect { cityDistrict ->
                        binding.locationTextView.text =
                            cityDistrict ?: "위치 정보 없음"
                    }
                }

                // 날씨 정보 Flow
                launch {
                    viewModel.weather.collect { weather ->

                        // 날씨 아이콘 표시 여부
                        binding.weatherIcon.visibility =
                            if (weather.isNullOrEmpty()) {
                                View.GONE
                            } else {
                                View.VISIBLE
                            }
                    }
                }
            }
        }

        Timber.d("onViewCreated -")
    }

    override fun onDestroyView() {
        Timber.d("onDestroyView +")

        super.onDestroyView()

        Timber.d("onDestroyView -")
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.closeButton -> {
                findNavController().popBackStack()
            }

            R.id.saveButton -> {

            }

            R.id.deleteButton -> {

            }
        }
    }
}