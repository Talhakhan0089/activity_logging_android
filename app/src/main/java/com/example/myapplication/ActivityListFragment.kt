package com.example.myapplication

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentActivityListBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ActivityListFragment : Fragment() {

    private var _binding: FragmentActivityListBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: ActivityDatabaseHelper
    private lateinit var adapter: ActivityAdapter

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActivityListBinding.inflate(inflater, container, false)
        dbHelper = ActivityDatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activities = dbHelper.getAllActivities()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = ActivityAdapter(activities)

        binding.buttonExportJson.setOnClickListener {
            exportToJson(activities)
        }

        binding.buttonExportCsv.setOnClickListener {
            exportToCsv(activities)
        }

        viewModel.steps.observe(viewLifecycleOwner) { steps ->
            binding.stepsTextView.text = "Steps: $steps"
        }

        viewModel.heartRate.observe(viewLifecycleOwner) { heartRate ->
            binding.heartRateTextView.text = "Heart Rate: $heartRate"
        }
    }

    private fun exportToJson(activities: List<Activity>) {
        val jsonArray = JSONArray()
        activities.forEach { activity ->
            val jsonObject = JSONObject()
            jsonObject.put("name", activity.name)
            jsonObject.put("user", activity.user)
            jsonObject.put("notes", activity.notes)
            jsonObject.put("startTime", activity.startTime)
            jsonObject.put("endTime", activity.endTime)
            jsonArray.put(jsonObject)
        }

        val jsonString = jsonArray.toString()
        val fileName = "activities.json"
        saveToFile(jsonString, fileName)
    }

    private fun exportToCsv(activities: List<Activity>) {
        val csvHeader = "Name,User,Notes,Start Time,End Time\n"
        val csvString = StringBuilder(csvHeader)

        activities.forEach { activity ->
            csvString.append("${activity.name},${activity.user},${activity.notes},${activity.startTime},${activity.endTime}\n")
        }

        val fileName = "activities.csv"
        saveToFile(csvString.toString(), fileName)
    }

    private fun saveToFile(data: String, fileName: String) {
        try {
            val file = File(requireContext().getExternalFilesDir(null), fileName)
            val fileWriter = FileWriter(file)
            fileWriter.write(data)
            fileWriter.flush()
            fileWriter.close()
            Toast.makeText(requireContext(), "Exported to $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to export data", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
