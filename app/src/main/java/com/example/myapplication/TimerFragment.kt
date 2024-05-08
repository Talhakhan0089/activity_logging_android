package com.example.myapplication

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private var isRunning = false
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 600000 // 10 minutes

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateCountDownText()

        binding.startPauseButton.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        binding.resetButton.setOnClickListener {
            resetTimer()
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                isRunning = false
                updateButtons()
            }
        }.start()

        isRunning = true
        updateButtons()
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isRunning = false
        updateButtons()
    }

    private fun resetTimer() {
        timeLeftInMillis = 600000 // 10 minutes
        updateCountDownText()
        updateButtons()
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        binding.countdownText.text = timeFormatted
    }

    private fun updateButtons() {
        if (isRunning) {
            binding.startPauseButton.text = "Pause"
        } else {
            binding.startPauseButton.text = "Start"
            if (timeLeftInMillis < 1000) {
                binding.startPauseButton.visibility = View.INVISIBLE
            } else {
                binding.startPauseButton.visibility = View.VISIBLE
            }

            if (timeLeftInMillis < 600000) {
                binding.resetButton.visibility = View.VISIBLE
            } else {
                binding.resetButton.visibility = View.INVISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
