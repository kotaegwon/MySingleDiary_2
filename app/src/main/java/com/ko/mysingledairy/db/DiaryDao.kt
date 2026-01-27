package com.ko.mysingledairy.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DiaryDao {
    // 날씨, 위치 저장, 출력
    // 저장 버튼 -> 일기, 사진, 사진 유무, 날씨, 위치, 시간 저장
    // 삭제 -> 해당 요소 삭제

    //모든 데이터 추출
    @Query("SELECT * FROM diary")
    suspend fun getAll(): List<DiaryListEntity>



    @Insert
    suspend fun saveDB(entities: DiaryListEntity)
}