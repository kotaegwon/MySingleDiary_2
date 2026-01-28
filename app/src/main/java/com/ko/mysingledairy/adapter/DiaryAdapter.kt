package com.ko.mysingledairy.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ko.mysingledairy.R
import com.ko.mysingledairy.databinding.ListItem1Binding
import com.ko.mysingledairy.databinding.ListItem2Binding
import com.ko.mysingledairy.db.DiaryListEntity
import java.io.File

class DiaryAdapter(val listener: Listener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface Listener {
        fun onItemClick(items: DiaryListEntity)
    }

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
        val uiItem = itemList[position]

        when (uiItem) {
            is DiaryUiItem.Text -> {
                (holder as TextViewHolder).bind(uiItem.diary)
            }

            is DiaryUiItem.Image -> {
                (holder as ImageViewHolder).bind(uiItem.diary)
            }
        }

        holder.itemView.setOnClickListener {
            when (uiItem) {
                is DiaryUiItem.Text -> {
                    listener.onItemClick(uiItem.diary)
                }

                is DiaryUiItem.Image -> {
                    listener.onItemClick(uiItem.diary)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class TextViewHolder(val binding: ListItem1Binding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DiaryListEntity) {
            binding.contentsTextView.text = item.content
            binding.locationTextView.text = item.address
            binding.dateTextView.text = item.date
            binding.pictureExistsImageView.isVisible = !item.picture.isNullOrEmpty()

            binding.moodImageView.setImageResource(
                when (item.mood) {
                    1 -> R.drawable.smile1_48
                    2 -> R.drawable.smile2_48
                    3 -> R.drawable.smile3_48
                    4 -> R.drawable.smile4_48
                    5 -> R.drawable.smile5_48
                    else -> R.drawable.smile1_48
                }
            )
            binding.weatherImageView.setImageResource(
                when (item.weather) {
                    "맑음" -> R.drawable.weather_1
                    "흐림" -> R.drawable.weather_4
                    "비" -> R.drawable.weather_5
                    "눈" -> R.drawable.weather_7
                    else -> R.drawable.weather_1
                }
            )
        }
    }

    class ImageViewHolder(val binding: ListItem2Binding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DiaryListEntity) {
            binding.contentsTextView2.text = item.content
            binding.locationTextView2.text = item.address
            binding.dateTextView2.text = item.date

            item.picture?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    binding.pictureImageView.setImageURI(Uri.fromFile(file))
                }
            }

            binding.moodImageView2.setImageResource(
                when (item.mood) {
                    1 -> R.drawable.smile1_48
                    2 -> R.drawable.smile2_48
                    3 -> R.drawable.smile3_48
                    4 -> R.drawable.smile4_48
                    5 -> R.drawable.smile5_48
                    else -> R.drawable.smile1_48
                }
            )
            binding.weatherImageView2.setImageResource(
                when (item.weather) {
                    "맑음" -> R.drawable.weather_1
                    "흐림" -> R.drawable.weather_4
                    "비" -> R.drawable.weather_5
                    "눈" -> R.drawable.weather_7
                    else -> R.drawable.weather_1
                }
            )
        }
    }
}