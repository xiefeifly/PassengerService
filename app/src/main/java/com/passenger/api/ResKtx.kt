package com.passenger.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Callback

fun <T : Any> retrofit2.Call<T>.toLiveData(): LiveData<T?> {
    var liveData: MutableLiveData<T?> = MutableLiveData<T?>()
    this.enqueue(object : Callback<T> {
        override fun onResponse(call: retrofit2.Call<T>, response: retrofit2.Response<T>) {
            val value: T? = if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
            liveData.postValue(value!!)
        }

        override fun onFailure(call: retrofit2.Call<T>, t: Throwable) {
            liveData.postValue(null)
        }

    })
    return liveData
}