package com.jp_funda.todomind.view.top

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jp_funda.todomind.data.repositories.mind_map.MindMapRepository
import com.jp_funda.todomind.data.repositories.mind_map.entity.MindMap
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class TopViewModel @Inject constructor(
    private val mindMapRepository: MindMapRepository
) : ViewModel() {
    private var _mostRecentlyUpdatedMindMap = MutableLiveData<MindMap?>(null)
    val mostRecentlyUpdatedMindMap: LiveData<MindMap?> = _mostRecentlyUpdatedMindMap

    // Dispose
    private val disposables = CompositeDisposable()

    fun getMostRecentlyUpdatedMindMap() {
        disposables.add(
            mindMapRepository.getMostRecentlyUpdatedMindMap()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    _mostRecentlyUpdatedMindMap.value = it
                }
                .doOnError {
                    _mostRecentlyUpdatedMindMap.value = null
                }
                .subscribe({}, {}) // add lambdas to avoid onError not implemented error
        )
    }

    override fun onCleared() {
        disposables.clear()
    }
}