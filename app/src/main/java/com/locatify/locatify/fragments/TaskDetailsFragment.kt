package com.locatify.locatify.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.locatify.locatify.MainActivity
import com.locatify.locatify.R
import com.locatify.locatify.databinding.FragmentTaskDetailsBinding

private const val TASK_NAME = "task_name"
private const val TASK_DATE = "task_date"
private const val TASK_TIME = "task_time"
private const val TASK_LOC = "task_loc"

class TaskDetailsFragment : Fragment() {

    private var taskName: String = ""
    private var taskDate: String = ""
    private var taskTime: String = ""
    private var taskLoc: Pair<Double, Double>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskName = arguments?.getString(TASK_NAME).toString()
        taskDate = arguments?.getString(TASK_DATE).toString()
        taskTime = arguments?.getString(TASK_TIME).toString()
        taskLoc = arguments?.get(TASK_LOC) as Pair<Double, Double>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var tdfBind: FragmentTaskDetailsBinding = FragmentTaskDetailsBinding.inflate(inflater)

        (requireActivity() as MainActivity).supportActionBar?.title = "Task Details"
        tdfBind.dTaskName.text = tdfBind.dTaskName.text.toString() + ": " + taskName
        tdfBind.dTaskDate.text = tdfBind.dTaskDate.text.toString() + ": " + taskDate
        tdfBind.dTaskTime.text = tdfBind.dTaskTime.text.toString() + ": " + taskTime
        tdfBind.dTaskLoc.text = tdfBind.dTaskLoc.text.toString() + ": " + "Latitude: ${taskLoc?.first}\t Longitude: ${taskLoc?.second}"

        return tdfBind.root
    }

    companion object {

        @JvmStatic
        fun newInstance(taskName: String, taskDate: String? = null, taskTime: String? = null, taskLoc: Pair<Double, Double>? = null) =
            TaskDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(TASK_NAME, taskName)
                    putString(TASK_DATE, taskDate)
                    putString(TASK_TIME, taskTime)
                    putSerializable(TASK_LOC, taskLoc)
                }
            }
    }
}