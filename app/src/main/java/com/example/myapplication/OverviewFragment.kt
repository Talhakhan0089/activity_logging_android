package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentOverviewBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import androidx.navigation.fragment.findNavController

class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: ActivityDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        dbHelper = ActivityDatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.summaryButton.setOnClickListener {
            displaySummaryMetrics()
        }

        binding.detailedButton.setOnClickListener {
            binding.detailedButton.setOnClickListener {
                // Navigate to the ActivityListFragment
                findNavController().navigate(R.id.action_overviewFragment_to_activityListFragment)
            }
        }

        // Display summary metrics by default
        displaySummaryMetrics()
    }

    private fun displaySummaryMetrics() {
        // Fetch data from database and update the chart
        val activities = dbHelper.getAllActivities()
        val pieEntries = mutableListOf<PieEntry>()
        val activityMap = activities.groupBy { it.name }

        activityMap.forEach { (name, activityList) ->
            val totalDuration = activityList.sumOf { it.endTime - it.startTime }
            pieEntries.add(PieEntry(totalDuration.toFloat(), name))
        }

        val dataSet = PieDataSet(pieEntries, "Activities")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val pieData = PieData(dataSet)

        binding.pieChart.data = pieData
        binding.pieChart.invalidate() // Refresh the chart
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



