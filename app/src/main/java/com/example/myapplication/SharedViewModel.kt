package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class SharedViewModel : ViewModel() {
    private val _steps = MutableLiveData<Int>()
    val steps: LiveData<Int> get() = _steps

    private val _heartRate = MutableLiveData<Float>()
    val heartRate: LiveData<Float> get() = _heartRate

    fun setSteps(steps: Int) {
        _steps.value = steps
    }

    fun setHeartRate(heartRate: Float) {
        _heartRate.value = heartRate
    }

    // Function to generate dummy data
    fun generateDummyData() {
        val dummySteps = Random.nextInt(1000, 10000)
        val dummyHeartRate = Random.nextFloat() * (100 - 60) + 60
        setSteps(dummySteps)
        setHeartRate(dummyHeartRate)
    }
}
