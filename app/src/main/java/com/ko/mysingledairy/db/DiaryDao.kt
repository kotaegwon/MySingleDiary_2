package com.ko.mysingledairy.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    // 삭제 -> 해당 요소 삭제

    @Query("SELECT * FROM diary")
    fun getAll(): LiveData<List<DiaryListEntity>>

    @Insert
    suspend fun saveDB(entities: DiaryListEntity)

    @Query("DELETE FROM diary")
    suspend fun deleteAll()

    @Query("DELETE FROM diary WHERE id = :id")
    fun deleteById(id: Int)

    @Update
    fun modifyDiary(diary: DiaryListEntity)

}