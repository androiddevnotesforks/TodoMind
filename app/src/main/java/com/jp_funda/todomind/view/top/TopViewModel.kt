package com.jp_funda.todomind.view.top

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jp_funda.todomind.data.repositories.mind_map.MindMapRepository
import com.jp_funda.todomind.data.repositories.mind_map.entity.MindMap
import com.jp_funda.todomind.data.repositories.task.TaskRepository
import com.jp_funda.todomind.data.repositories.task.entity.Task
import com.jp_funda.todomind.data.repositories.task.entity.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class TopViewModel @Inject constructor(
    private  val mindMapRepository: MindMapRepository
) : ViewModel() {
    private var _mostRecentlyUpdatedMindMap = MutableLiveData<MindMap>(null)
    val mostRecentlyUpdatedMindMap: LiveData<MindMap> = _mostRecentlyUpdatedMindMap

    // Dispose
    private val disposables = CompositeDisposable()

    fun getMostRecentlyUpdatedMindMapWithDelay() {
        disposables.add(
            Single
                .create<Int> { emitter ->
                    emitter.onSuccess(0)
                }
                .delay(1000, TimeUnit.MILLISECONDS)
                .doOnSuccess {
                    getMostRecentlyUpdatedMindMap()
                }
                .subscribe()
        )
    }

    private fun getMostRecentlyUpdatedMindMap() {
        disposables.add(
            mindMapRepository.getMostRecentlyUpdatedMindMap()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    _mostRecentlyUpdatedMindMap.value = it
                }
                .subscribe()
        )
    }

    override fun onCleared() {
        disposables.clear()
    }
}