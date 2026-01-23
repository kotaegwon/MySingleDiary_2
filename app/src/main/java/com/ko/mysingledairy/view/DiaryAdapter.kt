package com.ko.mysingledairy.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ko.mysingledairy.databinding.ListItem1Binding
import com.ko.mysingledairy.databinding.ListItem2Binding
import timber.log.Timber


class TextViewHolder(val binding: ListItem1Binding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DiaryItem) {
        binding.contentsTextView.text = item.contents
        binding.locationTextView.text = item.address
        binding.dateTextView.text = item.createDateStr

        binding.pictureExistsImageView.visibility = if (!item.picture) View.GONE else View.VISIBLE

        // TODO mood: Int로 변환 기문 아이콘 리소스 설정,

    }
}

class ImageViewHolder(val binding: ListItem2Binding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DiaryItem) {

    }
}

class DiaryAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val itemList = mutableListOf<DiaryUiItem>()

    companion object {
        const val TYPE_TEXT = 0
        const val TYPE_IMAGE = TYPE_TEXT + 1
    }

    /* ---------- 외부에서 데이터 세팅 ---------- */
    fun submitList(items: List<DiaryUiItem>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is DiaryUiItem.Text -> TYPE_TEXT
            is DiaryUiItem.Image -> TYPE_IMAGE
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_TEXT -> {
                val binding = ListItem1Binding.inflate(inflater, parent, false)
                TextViewHolder(binding)
            }

            TYPE_IMAGE -> {
                val binding = ListItem2Binding.inflate(inflater, parent, false)
                ImageViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }

    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (val item = itemList[position]) {
            is DiaryUiItem.Text -> {
                (holder as TextViewHolder).bind(item.diary)
            }

            is DiaryUiItem.Image -> {
                (holder as ImageViewHolder).bind(item.diary)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}