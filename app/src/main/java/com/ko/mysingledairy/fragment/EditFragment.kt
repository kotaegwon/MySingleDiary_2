package com.ko.mysingledairy.fragment

import android.content.Context
import android.net.Uri
import android.os.Build
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ko.mysingledairy.BuildConfig
import com.ko.mysingledairy.viewmodel.DiaryViewModel
import com.ko.mysingledairy.R
import com.ko.mysingledairy.databinding.FragmentEditBinding
import com.ko.mysingledairy.db.DiaryDao
import com.ko.mysingledairy.db.DiaryDatabase
import com.ko.mysingledairy.db.DiaryListEntity
import com.ko.mysingledairy.factory.DiaryViewModelFactory
import com.ko.mysingledairy.manager.LocationManager
import com.ko.mysingledairy.manager.WeatherManager
import com.ko.mysingledairy.repository.DiaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * - 다이어리 작성 / 수정 화면
 * - 날씨, 위치 정보를 표시
 * - 기분 슬라이더, 사진 선택 / 촬용 기능 포함
 * - 다이어리 내용 저장, 수정, 삭제 처리
 */
class EditFragment : Fragment(), View.OnClickListener {

    // ViewBinding
    private lateinit var binding: FragmentEditBinding

    // ViewModel
    private lateinit var viewModel: DiaryViewModel

    // Room DAO
    private lateinit var diaryDao: DiaryDao

    // 현재 선택된 상태
    private var currentWeather: String? = null
    private var currentMood: Int = 0
    private var currentPhotoUri: Uri? = null

    // 사진 저장용
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    // 수정 모드에서 선택된 다이어리 아이템
    private lateinit var modifyItem: DiaryListEntity
    private var isModifyMode: Boolean = false

    // 갤러리 선택 ActivityResultLauncher
    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Timber.d("선택된 이미지 URI: $it")
                // 여기서 이미지 처리 or 저장
                val savedPath = copyUriToInternalStorage(it)
                currentPhotoUri = Uri.fromFile(File(savedPath))
                binding.pictureImageView.setImageURI(currentPhotoUri)
            }
        }

    // 카메라 선택 ActivityResultLauncher
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Timber.d("촬영 성공: $photoUri")
                // photoUri 사용
                currentPhotoUri = photoUri
                binding.pictureImageView.setImageURI(photoUri)
            }
        }

    /**
     * Fragment가 Activity에 붙을 때 호출
     * - Repository, ViewModelFactory 생성
     * - ViewModel 초기화
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Repository 생성(위치 + 날씨 관리)
        val repo = DiaryRepository(
            LocationManager(requireContext()),
            WeatherManager()
        )

        // ViewModelFactory 생성
        val factory = DiaryViewModelFactory(repo)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this, factory)
            .get(DiaryViewModel::class.java)
    }

    /**
     * Fragment View 생성
     * - ViewBinding 초기화
     * - Room DAO 초기화
     */
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

    /**
     * View 생성 이후 호출
     * - 오늘 날짜 표시
     * - 전달된 다이어리 아이템 시팅(수정 모드)
     * - 기분 스라이더 리스너
     * - ViewModel 위치/날씨 Flow 수집
     * - 클릭 리스너 등록
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated +")

        super.onViewCreated(view, savedInstanceState)
        binding.dateTextView.text = todayDate()

        // 수정 모드로 전달된 DiaryListEntity 가져오기
        val diary = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(
                "diary_item",
                DiaryListEntity::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("diary_item")
        }

        // 수정 모드 세팅
        diary?.let {
            setBundleArgument(it)
        }

        // Mood Slider 리스너
        binding.moodSlider.addOnChangeListener { _, value, _ ->
            val mood = value.toInt()
            currentMood = mood
        }

        // 위치/날씨 Flow 수집
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

        // 클릭 리스너 등록
        binding.closeButton.setOnClickListener(this)
        binding.saveButton.setOnClickListener(this)
        binding.deleteButton.setOnClickListener(this)
        binding.pictureImageView.setOnClickListener(this)

        Timber.d("onViewCreated -")
    }

    /**
     * View가 제거될 때 호출
     * - 메모리 누수 방지
     */
    override fun onDestroyView() {
        Timber.d("onDestroyView +")

        super.onDestroyView()
        isModifyMode = false

        Timber.d("onDestroyView -")
    }

    override fun onDetach() {
        super.onDetach()
    }

    /**
     * 클릭 이벤트 처리
     * - 닫기, 저장/수정, 삭제, 사진 선택
     */
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.closeButton -> {
                findNavController().popBackStack()
            }

            R.id.saveButton -> {
                var toastText = ""
                val content = binding.contentInput.text.toString()
                val address = binding.locationTextView.text.toString()
                val diaryEntities = DiaryListEntity(
                    weather = currentWeather.toString(),
                    date = todayDate(),
                    address = address,
                    content = content,
                    picture = currentPhotoUri?.path,
                    mood = currentMood
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    if (isModifyMode) {
                        if (currentMood != 0) {
                            modifyItem.mood = currentMood
                        }
                        modifyItem.content = content
                        modifyItem.picture = currentPhotoUri?.path
                        diaryDao.modifyDiary(modifyItem)
                    } else {
                        diaryDao.saveDB(diaryEntities)
                    }
                }

                toastText = if (isModifyMode) {
                    "수정 완료"
                } else {
                    "저장 완료"
                }

                Toast.makeText(requireContext(), toastText, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }


            R.id.deleteButton -> {
                if (isModifyMode) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        diaryDao.deleteById(modifyItem.id)
                    }
                }
                findNavController().popBackStack()
            }

            R.id.pictureImageView -> {
                showPictureSetDialog()
            }
        }
    }

    /**
     * 수정 모드 다이어리 세팅
     */
    fun setBundleArgument(items: DiaryListEntity) {
        isModifyMode = true
        modifyItem = items

        setWeatherIcon(items.weather)
        binding.dateTextView.text = items.date
        binding.locationTextView.text = items.address
        binding.contentInput.setText(items.content)

        items.picture?.let { path ->
            val file = File(path)
            if (file.exists()) {
                binding.pictureImageView.setImageURI(Uri.fromFile(file))
                currentPhotoUri = Uri.fromFile(file)
            }
        }
        binding.moodSlider.value = items.mood.coerceIn(1, 5).toFloat()
    }

    /**
     * 날씨 아이콘 설정
     */
    fun setWeatherIcon(weather: String) {
        when (weather) {
            "맑음" -> binding.weatherIcon.setImageResource(R.drawable.weather_1)
            "흐림" -> binding.weatherIcon.setImageResource(R.drawable.weather_4)
            "비" -> binding.weatherIcon.setImageResource(R.drawable.weather_5)
            "눈" -> binding.weatherIcon.setImageResource(R.drawable.weather_7)
        }
    }

    fun todayDate(): String = SimpleDateFormat("MM월 dd일", Locale.KOREA).format(Date())

    /**
     * 사진 선택/촬영 다이얼로그 표시
     */
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

    /**
     * 갤러리에서 사진 선택
     */
    fun showPhotoSelectionActivity() {
        photoPickerLauncher.launch("image/*")
    }

    /**
     * 임시 파일 생성(카메라 촬영용)
     */
    fun createFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().cacheDir   // cacheDir 권장
        return File.createTempFile(
            "IMG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    /**
     * 카메라 촬영 Activity 호출
     */
    fun showPhotoCaptureActivity() {
        photoFile = createFile()

        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            photoFile
        )

        cameraLauncher.launch(photoUri)
    }

    /**
     * 선택한 URI를 앱 내부 저장소에 복사 후 경로 반환
     */
    private fun copyUriToInternalStorage(uri: Uri): String {
        val resolver = requireContext().contentResolver
        val inputStream = resolver.openInputStream(uri) ?: return ""

        val file = File(
            requireContext().filesDir,
            "diary_${System.currentTimeMillis()}.jpg"
        )

        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        return file.absolutePath
    }
}