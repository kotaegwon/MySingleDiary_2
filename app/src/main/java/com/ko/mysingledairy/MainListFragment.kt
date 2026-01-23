package com.ko.mysingledairy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ko.mysingledairy.databinding.FragmentListBinding
import com.ko.mysingledairy.view.DiaryAdapter
import com.ko.mysingledairy.view.DiaryItem
import com.ko.mysingledairy.view.DiaryUiItem
import timber.log.Timber

class MainListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var adapter: DiaryAdapter

    private fun loadDummyTextItems() {
        // 임의 DiaryItem 생성
        val diaryItems = listOf(
            DiaryItem(
                id = 1,
                weather = "맑음",
                address = "서울 강남구",
                locationX = "37.4979",
                locationY = "127.0276",
                contents = "오늘은 기분이 좋았다.",
                mood = "행복",
                picture = false,
                createDateStr = "2026-01-23"
            ),
            DiaryItem(
                id = 2,
                weather = "비",
                address = "서울 마포구",
                locationX = "37.5665",
                locationY = "126.9780",
                contents = "비 오는 날 커피가 최고다.",
                mood = "차분",
                picture = true,
                createDateStr = "2026-01-22"
            ),
            DiaryItem(
                id = 3,
                weather = "흐림",
                address = "서울 송파구",
                locationX = "37.5145",
                locationY = "127.1056",
                contents = "하루가 빨리 지나갔다.",
                mood = "보통",
                picture = false,
                createDateStr = "2026-01-21"
            )
        )

        // DiaryItem → DiaryUiItem.Text 로 변환
        // map: 각 요소를 하나씩 변환해서 새 리스트 생성
        val uiItems = diaryItems.map {
            DiaryUiItem.Text(it)
        }

        // Adapter에 전달
        adapter.submitList(uiItems)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView +")

        binding = FragmentListBinding.inflate(layoutInflater)
        var layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.layoutManager = layoutManager

        adapter = DiaryAdapter()
        binding.recyclerview.adapter = adapter

        return binding.root

        Timber.d("onCreateView +")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated +")

        super.onViewCreated(view, savedInstanceState)

        loadDummyTextItems()

        Timber.d("onViewCreated +")
    }

    override fun onDestroyView() {
        Timber.d("onDestroyView +")
        super.onDestroyView()
        Timber.d("onDestroyView -")
    }
}