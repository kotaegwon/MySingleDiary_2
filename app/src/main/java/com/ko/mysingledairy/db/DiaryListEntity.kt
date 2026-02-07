package com.ko.mysingledairy.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * 일기 데이터를 저장하기 위한 Entity 클래스
 * Room DB의 diary 테이블과 매핑
 */
@Parcelize // 객체를 Intent, Bundle로 전달하기 위해 Parcelable 자동 구현
@Entity(tableName = "diary")
data class DiaryListEntity(

    /**
     * 일기 고유 ID(기본 키)
     * autoGenerate = true: 자동으로 값 생성
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // 날씨 정보
    @ColumnInfo(name = "weather")
    var weather: String,

    // 작성 날짜
    @ColumnInfo(name = "date")
    var date: String,

    // 작성 위치 주소
    @ColumnInfo(name = "address")
    var address: String,

    // 일기 내용
    @ColumnInfo(name = "content")
    var content: String,

    // 첨부 이미지 경루(없을 수도 있으므로 Nullable)
    @ColumnInfo(name = "picture")
    var picture: String?,

    // 기분 상태 값
    @ColumnInfo(name = "mood")
    var mood: Int
): Parcelable