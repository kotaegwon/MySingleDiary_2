package com.ko.mysingledairy.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room 데이터베이스를 관리하는 Database 클래스
 * 앱 전체에서 하나의 인스턴스만 사용하도록 Singleton 패턴 적용
 */
@Database(
    // 사용할 Entity 클래스 등록
    entities = [DiaryListEntity::class],

    // DB 버전(스키마 변경시 증가 필요)
    version = 2,

    // 스키마 내보내기 비활성화
    exportSchema = false
)
abstract class DiaryDatabase : RoomDatabase() {

    /**
     * DAO 객체 반환
     */
    abstract fun diaryDao(): DiaryDao

    companion object {

        // Database 인스턴스(싱글톤)
        private var INSTANCE: DiaryDatabase? = null

        /**
         * Database 인스턴스를 반환하는 함수
         * 여러 스레드에서 동시에 접근해도 안전하도록 synchronized 처리
         */
        fun get(context: Context): DiaryDatabase =
            INSTANCE ?: synchronized(this) {

                // 이미 생성된 인스턴스가 있다면 재사용
                INSTANCE ?: Room.databaseBuilder(
                    // Application Context 사용(메모리 누수 방지)
                    context.applicationContext,
                    // Database 클래스 지정
                    DiaryDatabase::class.java,
                    // DB 파일명
                    "diary_db"
                )
                    // 버전 변경 시 기존 데이터 삭제 후 재생성
                    .fallbackToDestructiveMigration()
                    // DB 생성
                    .build()
                    // 생성된 인스턴스를 저장
                    .also { INSTANCE = it }
            }
    }
}