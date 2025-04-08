package com.locatify.locatify.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.FragmentResultListener
import com.bumptech.glide.Glide
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.tooltip.TooltipDrawable
import com.locatify.locatify.MainActivity
import com.locatify.locatify.R
import com.locatify.locatify.databinding.FragmentAddTaskBinding
import com.locatify.locatify.modals.TaskModal
import com.locatify.locatify.receiver.AlarmReceiver
import java.sql.Time
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AddTaskFragment : Fragment() {

    private var taskDate: String? = null
    private var taskTime: String? = null
    private var taskName: String? = null
    private var taskLoc: Pair<Double, Double>? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var atfBind: FragmentAddTaskBinding = FragmentAddTaskBinding.inflate(inflater)

        (requireActivity() as MainActivity).supportActionBar?.title = "Add Task"

        val location: Location? = (requireActivity() as MainActivity).getLocation()
        val latitude = if(location != null) location.latitude else 0.0
        val longitude = if(location != null) location.longitude else 0.0
//        val mapsUrl = "https://maps.googleapis.com/maps/api/staticmap?center=${latitude},${longitude}&zoom=zoom_level&size=${atfBind.mapImg.width}x${atfBind.mapImg.height}&markers=color:red%7Clabel:S%7C${latitude},${longitude}&key=${R.string.google_maps_api_key}"
//
//        Log.d("mapUrl", mapsUrl.toString())
//        Glide.with(requireActivity())
//            .load(mapsUrl)
//            .into(atfBind.mapImg)
//
//

        atfBind.btnPickDate.setOnClickListener {

            val currentCal = Calendar.getInstance()

            var dateDialog: Dialog = DatePickerDialog(requireActivity(), object : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    currentCal.set(Calendar.YEAR, year)
                    currentCal.set(Calendar.MONTH, month)
                    currentCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    taskDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentCal.time)
                    atfBind.taskDate.text = atfBind.taskDate.text.toString() + taskDate
                    Toast.makeText(requireActivity(), taskDate, Toast.LENGTH_SHORT).show()
                }
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            dateDialog.show()
        }


        atfBind.btnPickTime.setOnClickListener {

            val currentCal = Calendar.getInstance()

            var timeDialog: Dialog = TimePickerDialog(requireActivity(), object : TimePickerDialog.OnTimeSetListener{
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    currentCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    currentCal.set(Calendar.MINUTE, minute)
                    taskTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentCal.time)
                    atfBind.taskTime.text = atfBind.taskTime.text.toString() + taskTime
                    Toast.makeText(requireActivity(), taskTime, Toast.LENGTH_SHORT).show()
                }
            }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false)
            timeDialog.show()
        }



        atfBind.btnAddTask.setOnClickListener {
            taskName = atfBind.edtTaskName.text.toString()
            if(taskName != "") {
                val taskModal: TaskModal = TaskModal(taskName.toString(), taskDate.toString(), taskTime.toString(), taskLoc)
                val id:Long = (requireActivity() as MainActivity).taskDBHelper.addTask(taskModal)
                if(taskDate != null && taskTime != null) {
                    createAlarm(taskModal, id.toInt())
                }
                if(id.toInt() != -1) {
                    (requireActivity() as MainActivity).supportFragmentManager.popBackStack()
                }
                else {
                    Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT).show()
                }

            }
            else {
                Toast.makeText(requireActivity(), "Task Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

//        (requireActivity() as MainActivity).supportFragmentManager.beginTransaction().add(R.id.mapCont, MapsFragment()).commit()
        atfBind.mapCont.setOnClickListener {
            (requireActivity() as MainActivity).loadFragment(MapsFragment(), 2);
            parentFragmentManager.setFragmentResultListener("mapLocation", viewLifecycleOwner, object : FragmentResultListener {
                override fun onFragmentResult(requestKey: String, result: Bundle) {
                    taskLoc = Pair<Double, Double>(result.getDouble("latitude"), result.getDouble("longitude"))
                    atfBind.txtLoc.text = "Latitude: ${taskLoc?.first}\t Longitude: ${taskLoc?.second}"
                }

            })
        }

        return atfBind.root
    }

    private fun createAlarm(taskModal: TaskModal, id:Int) {
        val dt = taskModal.taskDate + " " + taskTime + ":00"
        val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date: Date = sdf.parse(dt)
        val calendar = Calendar.getInstance()
        calendar.time = date
        Log.d("SDF", calendar.timeInMillis.toString())
        val timeForAlarm: Long = calendar.timeInMillis

        val pi: PendingIntent = PendingIntent.getBroadcast(requireActivity(), id, Intent(requireActivity(), AlarmReceiver::class.java).apply {
            this.putExtra("taskId", taskModal.id)
        }, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager: AlarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeForAlarm, pi)
            Toast.makeText(requireActivity(), "Executed Alarm", Toast.LENGTH_SHORT).show()
        }
        catch (e: SecurityException) {
            e.printStackTrace()
        }
    }


}

