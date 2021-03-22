package ru.vincetti.modules.core.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <X, Y> LiveData<X>.mapNotNull(func: (source: X?) -> Y?): LiveData<Y> {
    val result = MediatorLiveData<Y>()
    result.addSource(this) { x ->
        func.invoke(x)?.let {
            result.value = it
        }
    }
    return result
}

fun <X> LiveData<X?>.filterOutNull(): LiveData<X> {
    val result = MediatorLiveData<X>()
    result.addSource(this) { x ->
        if (x != null) result.value = x
    }
    return result
}
