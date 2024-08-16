package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentDashboardBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.tasks.Task

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()
    private val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.navigateToTimerButton.setOnClickListener {
            findNavController().navigate(R.id.timerFragment)
        }
        binding.buttonAddActivity.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_addActivityFragment)
        }

        binding.buttonViewActivities.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_activityListFragment)
        }

        binding.overviewButton.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_overviewFragment)
        }

        binding.connectWearableButton.setOnClickListener {
            authenticateGoogleFit()
        }

        viewModel.steps.observe(viewLifecycleOwner, Observer { steps ->
            Toast.makeText(requireContext(), "Steps: $steps", Toast.LENGTH_SHORT).show()
        })

        viewModel.heartRate.observe(viewLifecycleOwner, Observer { heartRate ->
            Toast.makeText(requireContext(), "Heart Rate: $heartRate", Toast.LENGTH_SHORT).show()
        })
    }

    private fun authenticateGoogleFit() {
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .build()

        val account = GoogleSignIn.getAccountForExtension(requireActivity(), fitnessOptions)

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this,
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                account,
                fitnessOptions
            )
        } else {
            accessGoogleFitData(account)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            accessGoogleFitData(account)
        } catch (e: ApiException) {
            Toast.makeText(requireContext(), "Failed to sign in with Google: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun accessGoogleFitData(account: GoogleSignInAccount) {
        Fitness.getHistoryClient(requireContext(), account)
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { dataSet ->
                val totalSteps = dataSet.dataPoints.firstOrNull()?.getValue(Field.FIELD_STEPS)?.asInt() ?: 0
                viewModel.setSteps(totalSteps)
                Toast.makeText(requireContext(), "Total steps: $totalSteps", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to read steps: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        Fitness.getHistoryClient(requireContext(), account)
            .readDailyTotal(DataType.TYPE_HEART_RATE_BPM)
            .addOnSuccessListener { dataSet ->
                val heartRate = dataSet.dataPoints.firstOrNull()?.getValue(Field.FIELD_BPM)?.asFloat() ?: 0f
                viewModel.setHeartRate(heartRate)
                Toast.makeText(requireContext(), "Heart rate: $heartRate", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to read heart rate: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        // Generate dummy data after successful connection
        viewModel.generateDummyData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
