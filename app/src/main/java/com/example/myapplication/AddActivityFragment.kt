package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentAddActivityBinding
import java.text.SimpleDateFormat
import java.util.*

class AddActivityFragment : Fragment() {

    private var _binding: FragmentAddActivityBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: ActivityDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddActivityBinding.inflate(inflater, container, false)
        dbHelper = ActivityDatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSave.setOnClickListener {
            val activityName = binding.editTextActivityName.text.toString()
            val user = binding.editTextUser.text.toString()
            val notes = binding.editTextNotes.text.toString()
            val startTime = binding.editTextStartTime.text.toString()
            val endTime = binding.editTextEndTime.text.toString()

            try {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val startDate = timeFormat.parse(startTime)
                val endDate = timeFormat.parse(endTime)

                if (startDate != null && endDate != null) {
                    val startMillis = startDate.time
                    val endMillis = endDate.time

                    val id = dbHelper.insertActivity(activityName, user, notes, startMillis, endMillis)
                    if (id != -1L) {
                        showToast("Activity saved successfully")
                        findNavController().navigate(R.id.action_addActivityFragment_to_activityListFragment)
                    } else {
                        showToast("Failed to save activity")
                    }
                } else {
                    showToast("Invalid time format")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Invalid time format")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
