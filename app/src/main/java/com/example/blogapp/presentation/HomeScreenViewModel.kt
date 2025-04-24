package com.example.blogapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.example.blogapp.core.Result
import com.example.blogapp.domain.home.HomeScreenRepo
import kotlinx.coroutines.Dispatchers

class HomeScreenViewModel(private val repo: HomeScreenRepo) : ViewModel() {
    fun fetchLatestPost() = liveData(Dispatchers.IO){
        emit(Result.Loading())
        runCatching {
            repo.getLatestPost()
        }.onSuccess { flowList ->
            flowList.collect {
                emit(it)
            }
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))

        }
    }
}

class HomeScreenViewModelFactory(private val repo: HomeScreenRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(HomeScreenRepo::class.java).newInstance(repo)
    }
}