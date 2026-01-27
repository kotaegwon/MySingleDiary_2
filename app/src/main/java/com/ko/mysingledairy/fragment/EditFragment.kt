package com.ko.mysingledairy.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ko.mysingledairy.BuildConfig
import com.ko.mysingledairy.DiaryViewModel
import com.ko.mysingledairy.R
import com.ko.mysingledairy.databinding.FragmentEditBinding
import com.ko.mysingledairy.db.DiaryDao
import com.ko.mysingledairy.db.DiaryDatabase
import com.ko.mysingledairy.db.DiaryListEntity
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentEditBinding

    private val viewModel: DiaryViewModel by viewModels()

    private lateinit var diaryDao: DiaryDao

    private var currentWeather: String? = null
    private var currentMood: Int = 0
    private var currentPhotoUri: Uri? = null

    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Timber.d("선택된 이미지 URI: $it")
                // 여기서 이미지 처리 or 저장
                currentPhotoUri = it
                binding.pictureImageView.setImageURI(it)
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Timber.d("촬영 성공: $photoUri")
                // photoUri 사용
                currentPhotoUri = photoUri
                binding.pictureImageView.setImageURI(photoUri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView +")

        binding = FragmentEditBinding.inflate(inflater, container, false)

        val db = DiaryDatabase.get(requireContext())
        diaryDao = db.diaryDao()

        Timber.d("onCreateView -")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated +")

        super.onViewCreated(view, savedInstanceState)

        binding.dateTextView.text = getUIDate()

        binding.moodSlider.addOnChangeListener { _, value, _ ->
            val mood = value.toInt()
            currentMood = mood
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
                        setWeatherIcon(weather.toString())
                        currentWeather = weather.toString()
                    }
                }
            }
        }

        binding.closeButton.setOnClickListener(this)
        binding.saveButton.setOnClickListener(this)
        binding.deleteButton.setOnClickListener(this)
        binding.pictureImageView.setOnClickListener(this)

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
                val content = binding.contentInput.text.toString()
                val address = binding.locationTextView.text.toString()
                val diaryEntities = DiaryListEntity(
                    weather = currentWeather.toString(),
                    date = getDBDate(),
                    address = address,
                    content = content,
                    picture = currentPhotoUri.toString(),
                    mood = currentMood
                )

                lifecycleScope.launch {
                    Timber.d("entities ; $diaryEntities")
                    diaryDao.saveDB(diaryEntities)
                }

                Toast.makeText(requireContext(), "저장 완료", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }

            R.id.deleteButton -> {

            }

            R.id.pictureImageView -> {
                showPictureSetDialog()
            }
        }
    }

    fun setWeatherIcon(weather: String) {
        when (weather) {
            "맑음" -> binding.weatherIcon.setImageResource(R.drawable.weather_1)
            "흐림" -> binding.weatherIcon.setImageResource(R.drawable.weather_4)
            "비" -> binding.weatherIcon.setImageResource(R.drawable.weather_5)
            "눈" -> binding.weatherIcon.setImageResource(R.drawable.weather_7)
        }
    }

    fun getDBDate(): String = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA).format(Date())

    fun getUIDate(): String = SimpleDateFormat("MM월 dd일", Locale.KOREA).format(Date())

    fun showPictureSetDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_radio, null)

        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnSelect = dialogView.findViewById<Button>(R.id.btnSelect)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSelect.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            when (selectedId) {
                -1 -> {
                    Toast.makeText(context, "선택하지 않았습니다.", Toast.LENGTH_SHORT).show()
                }

                R.id.radio1 -> {
                    showPhotoCaptureActivity()
                    dialog.dismiss()
                }

                R.id.radio2 -> {
                    showPhotoSelectionActivity()
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    fun showPhotoSelectionActivity() {
        photoPickerLauncher.launch("image/*")
    }

    fun createFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().cacheDir   // cacheDir 권장
        return File.createTempFile(
            "IMG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    fun showPhotoCaptureActivity() {
        photoFile = createFile()

        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            photoFile
        )

        cameraLauncher.launch(photoUri)
    }
}