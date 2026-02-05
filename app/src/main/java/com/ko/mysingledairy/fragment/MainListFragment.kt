package com.ko.mysingledairy.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ko.mysingledairy.R
import com.ko.mysingledairy.databinding.FragmentListBinding
import com.ko.mysingledairy.adapter.DiaryAdapter
import com.ko.mysingledairy.adapter.DiaryUiItem
import com.ko.mysingledairy.db.DiaryDao
import com.ko.mysingledairy.db.DiaryDatabase
import com.ko.mysingledairy.db.DiaryListEntity
import kotlinx.coroutines.launch
import timber.log.Timber

class MainListFragment : Fragment(), View.OnClickListener, DiaryAdapter.Listener {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DiaryAdapter

    //  전체 다이어리 데이터 리스트
    private var diaryList: List<DiaryListEntity> = emptyList()

    // Room DAO 객체
    private lateinit var diaryDao: DiaryDao

    /**
     * Fragment의 View를 생성하는 함수
     * ViewBinding, RecyclerView, Adapter, DB 초기화를 수행
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView +")
        _binding = FragmentListBinding.inflate(inflater, container, false)

        // RecyclerView 레이아웃 설정
        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.layoutManager = layoutManager

        // Adapter 연결
        adapter = DiaryAdapter(this)
        binding.recyclerview.adapter = adapter

        // Room Database 초기화
        val db = DiaryDatabase.get(requireContext())
        diaryDao = db.diaryDao()
        Timber.d("onCreateView +")

        return binding.root
    }

    /**
     * View 생성 이후 호출되는 함수
     * 버튼 초기화 및 DB 데이터 관찰을 설정
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated +")

        super.onViewCreated(view, savedInstanceState)

        // 기본 선택 탭 색상 설정
        val textColor = ContextCompat.getColor(requireContext(), R.color.white)
        val backGroundColor = ContextCompat.getColor(requireContext(), R.color.orange)

        binding.textListButton.setTextColor(textColor)
        binding.textListButton.setBackgroundColor(backGroundColor)

        // 버튼 클릭 리스너 등록
        binding.textListButton.setOnClickListener(this)
        binding.imageListButton.setOnClickListener(this)
        binding.editDisplayButton.setOnClickListener(this)

//        viewLifecycleOwner.lifecycleScope.launch {
//            diaryDao.deleteAll()
//        }

        // Room DB 데이터 관찰(LiveData)
        diaryDao.getAll().observe(viewLifecycleOwner) { list ->

            diaryList = list

            // 기본적으로 내용 타입으로 변환
            val uiItems = diaryList.map {
                DiaryUiItem.Text(it)
            }
            adapter.submitList(uiItems)
        }

        Timber.d("onViewCreated +")
    }

    /**
     * View가 제거될 때 Binding 해제
     * 메모리 누수 방지용
     */
    override fun onDestroyView() {
        Timber.d("onDestroyView +")

        super.onDestroyView()
        _binding = null

        Timber.d("onDestroyView -")
    }

    /**
     * 상단 탭 버튼 UI 상태 변경
     * 선택된 버튼 생상을 변경
     */
    fun selectTab(isText: Boolean) {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.white)
        val normalColor = ContextCompat.getColor(requireContext(), R.color.orange)

        binding.textListButton.setTextColor(if (isText) selectedColor else normalColor)
        binding.textListButton.setBackgroundColor(if (isText) normalColor else selectedColor)

        binding.imageListButton.setTextColor(if (isText) normalColor else selectedColor)
        binding.imageListButton.setBackgroundColor(if (isText) selectedColor else normalColor)
    }

    /**
     * 상단 버튼 클릭 처리 함수
     * 내용 / 사진 타입 전환 및 화면 이동 담당
     */
    override fun onClick(view: View?) {
        val uiItem = when (view?.id) {
            R.id.textListButton -> {
                selectTab(true)

                diaryList.map {
                    DiaryUiItem.Text(it)
                }
            }

            R.id.imageListButton -> {
                selectTab(false)

                diaryList.map {
                    DiaryUiItem.Image(it)
                }
            }

            R.id.editDisplayButton -> {
                findNavController().navigate(R.id.action_to_EditFragment)
                null
            }

            else -> null
        }

        uiItem?.let { adapter.submitList(uiItem) }
    }

    /**
     * RecyclerView 아이템 클릭 처리
     * 선택된 아이템을 EditFragment로 전달
     */
    override fun onItemClick(items: DiaryListEntity) {
        findNavController().navigate(
            R.id.action_to_EditFragment,
            Bundle().apply {
                putParcelable("diary_item", items)
            })
        Timber.d("onItemClick: $items")
    }
}