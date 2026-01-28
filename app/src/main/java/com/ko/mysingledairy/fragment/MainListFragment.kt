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

    private var diaryList: List<DiaryListEntity> = emptyList()
    private lateinit var diaryDao: DiaryDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView +")
        _binding = FragmentListBinding.inflate(inflater, container, false)
        var layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.layoutManager = layoutManager

        adapter = DiaryAdapter(this)
        binding.recyclerview.adapter = adapter

        val db = DiaryDatabase.get(requireContext())
        diaryDao = db.diaryDao()
        Timber.d("onCreateView +")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("onViewCreated +")

        super.onViewCreated(view, savedInstanceState)

        val textColor = ContextCompat.getColor(requireContext(), R.color.white)
        val backGroundColor = ContextCompat.getColor(requireContext(), R.color.orange)

        binding.textListButton.setTextColor(textColor)
        binding.textListButton.setBackgroundColor(backGroundColor)

        binding.textListButton.setOnClickListener(this)
        binding.imageListButton.setOnClickListener(this)
        binding.editDisplayButton.setOnClickListener(this)

//        viewLifecycleOwner.lifecycleScope.launch {
//            diaryDao.deleteAll()
//        }

        diaryDao.getAll().observe(viewLifecycleOwner) { list ->

            diaryList = list

            val uiItems = diaryList.map {
                DiaryUiItem.Text(it)
            }
            adapter.submitList(uiItems)
        }

        Timber.d("onViewCreated +")
    }

    override fun onDestroyView() {
        Timber.d("onDestroyView +")

        super.onDestroyView()
        _binding = null

        Timber.d("onDestroyView -")
    }

    fun selectTab(isText: Boolean) {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.white)
        val normalColor = ContextCompat.getColor(requireContext(), R.color.orange)

        binding.textListButton.setTextColor(if (isText) selectedColor else normalColor)
        binding.textListButton.setBackgroundColor(if (isText) normalColor else selectedColor)

        binding.imageListButton.setTextColor(if (isText) normalColor else selectedColor)
        binding.imageListButton.setBackgroundColor(if (isText) selectedColor else normalColor)
    }

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

    override fun onItemClick(items: DiaryListEntity) {
        findNavController().navigate(
            R.id.action_to_EditFragment,
            Bundle().apply {
                putParcelable("diary_item", items)
            })
        Timber.d("onItemClick: $items")
    }
}