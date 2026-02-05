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

/**
 * 일기 목록 RecyclerView를 관리하는 Adapter 클래스
 * - 텍스트 타입 / 이미지 타입 아이템을 구분하여 표시
 * - 클릭 이벤트를 외부(Fragment / Activity) 로 전달
 */
class DiaryAdapter(val listener: Listener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * 화면에 표시할 UI 아이템 리스트
     */
    val itemList = mutableListOf<DiaryUiItem>()

    /**
     * 아이템 클릭 이벤트를 전달하기 위한 인터페이스
     */
    interface Listener {
        fun onItemClick(items: DiaryListEntity)
    }

    /**
     * RecyclerView 아이템 타입 상수
     */
    companion object {
        const val TYPE_TEXT = 0
        const val TYPE_IMAGE = TYPE_TEXT + 1
    }

    /**
     * 새로운 데이터를 Adapter에 전달하는 함수
     */
    fun submitList(items: List<DiaryUiItem>) {

        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    /**
     * 현재 position의 아이템 타입 반환
     */
    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is DiaryUiItem.Text -> TYPE_TEXT
            is DiaryUiItem.Image -> TYPE_IMAGE
        }
    }

    /**
     * viewType에 따라 ViewHolder 생성
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            // 내용
            TYPE_TEXT -> {
                val binding = ListItem1Binding.inflate(inflater, parent, false)
                TextViewHolder(binding)
            }

            // 사진
            TYPE_IMAGE -> {
                val binding = ListItem2Binding.inflate(inflater, parent, false)
                ImageViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }

    }

    /**
     * ViewHolder에 데이터 바인딩
     */
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val uiItem = itemList[position]

        // 타입에 따라 바인딩 처리
        when (uiItem) {
            is DiaryUiItem.Text -> {
                (holder as TextViewHolder).bind(uiItem.diary)
            }

            is DiaryUiItem.Image -> {
                (holder as ImageViewHolder).bind(uiItem.diary)
            }
        }

        // 아이템 클릭 이벤트 처리
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

    /**
     * 전체 아이템 개수 반환
     */
    override fun getItemCount(): Int {
        return itemList.size
    }

    /**
     * 내용 리스트 전용 아이템 ViewHodler
     */
    class TextViewHolder(val binding: ListItem1Binding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * 내용 아이템 데이터 바인딩
         */
        fun bind(item: DiaryListEntity) {
            binding.contentsTextView.text = item.content
            binding.locationTextView.text = item.address
            binding.dateTextView.text = item.date

            // 사진 유무에 따라 아이콘 표시 여부 설정
            binding.pictureExistsImageView.isVisible = !item.picture.isNullOrEmpty()

            // 기분 아이콘 설정
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

            // 날씨 아이콘 설정
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

    /**
     * 사진 리스트 전용 아이템 ViewHodler
     */
    class ImageViewHolder(val binding: ListItem2Binding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * 사진 아이템 데이터 바인딩
         */
        fun bind(item: DiaryListEntity) {
            binding.contentsTextView2.text = item.content
            binding.locationTextView2.text = item.address
            binding.dateTextView2.text = item.date

            // 사진 파일이 존재할 경우 이미지 표시
            item.picture?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    binding.pictureImageView.setImageURI(Uri.fromFile(file))
                }
            }

            // 기분 아이콘 설정
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

            // 날씨 아이콘 설정
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