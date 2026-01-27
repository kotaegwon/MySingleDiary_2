package com.ko.mysingledairy.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    // 날씨, 위치 저장, 출력
    // 저장 버튼 -> 일기, 사진, 사진 유무, 날씨, 위치, 시간 저장
    // 삭제 -> 해당 요소 삭제

    //모든 데이터 추출
    @Query("SELECT * FROM diary")
    fun getAll(): LiveData<List<DiaryListEntity>>


    @Insert
    suspend fun saveDB(entities: DiaryListEntity)

    @Query("DELETE FROM diary")
    suspend fun deleteAll()
}