package com.ko.mysingledairy.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ko.mysingledairy.repository.DiaryRepository
import com.ko.mysingledairy.viewmodel.DiaryViewModel

/**
 * Repository를 포함한 ViewModel을 생성하기 위한 Factory 클래스
 * ViewModel에 생성자 파라미터를 전달하기 위해 사용
 */
class DiaryViewModelFactory(private val repo: DiaryRepository) : ViewModelProvider.Factory {

    /**
     * ViewModelProvider가 ViewModel 생성을 요청할 때 호출되는 함수
     *
     * @param modelClass 생성할 ViewModel 클래스 타입
     * @return 생성된 ViewModel 객체
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // 요청된 ViewModel이 DiaryViewModel인지 확인
        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {

            // Repository를 주입하여 ViewModel 생성
            return DiaryViewModel(repo) as T
        }

        // 지원하지 않는 ViewModel일 경우 예외 발생
        throw IllegalArgumentException()
    }
}
