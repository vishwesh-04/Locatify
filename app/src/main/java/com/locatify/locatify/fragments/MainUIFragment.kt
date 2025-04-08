package com.locatify.locatify.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.locatify.locatify.MainActivity
import com.locatify.locatify.R
import com.locatify.locatify.adapters.RecyclerTaskAdapter
import com.locatify.locatify.database.TaskDBHelper
import com.locatify.locatify.databinding.FragmentMainUIBinding
import com.locatify.locatify.modals.TaskModal

lateinit var muiBind: FragmentMainUIBinding
class MainUIFragment : Fragment() {

    lateinit var taskList: ArrayList<TaskModal>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as MainActivity).supportActionBar?.title = "Locatify"

        muiBind = FragmentMainUIBinding.inflate(inflater)

        taskList = (requireActivity() as MainActivity).taskDBHelper.fetchTaskList()

        muiBind.recyclerView.layoutManager = LinearLayoutManager(requireActivity());
        muiBind.recyclerView.adapter = RecyclerTaskAdapter(requireActivity(), taskList)
        muiBind.fabAddTask.setOnClickListener {
            (requireActivity() as MainActivity).loadFragment(AddTaskFragment(), 1)
        }

        muiBind.recyclerView.adapter?.notifyDataSetChanged()
        return muiBind.root
    }

    override fun onResume() {
        super.onResume()

    }


}