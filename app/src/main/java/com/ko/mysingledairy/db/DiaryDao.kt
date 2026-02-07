package com.ko.mysingledairy.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * diary 테이블에 접근하기 위한 DAO 인터페이스
 * 데이터 베이스 CRUD 작업을 담당
 */
@Dao
interface DiaryDao {

    /**
     * 모든 일기 데이터를 조회
     * LiveData로 반환하여 데이터 변경 시 UI 자동 갱신
     */
    @Query("SELECT * FROM diary")
    fun getAll(): LiveData<List<DiaryListEntity>>

    /**
     * 일기 데이터 자장
     * suspend 함수: 코루틴 환경에서 비동기 실행
     */
    @Insert
    suspend fun saveDB(entities: DiaryListEntity)

    /**
     * 모든 일기 데이터 삭제
     */
    @Query("DELETE FROM diary")
    suspend fun deleteAll()

    /**
     * 특정 ID에 해당하는 일기 삭제
     */
    @Query("DELETE FROM diary WHERE id = :id")
    suspend fun deleteById(id: Int)

    /**
     * 기본 일기 데이터 수정
     */
    @Update
    fun modifyDiary(diary: DiaryListEntity)
}