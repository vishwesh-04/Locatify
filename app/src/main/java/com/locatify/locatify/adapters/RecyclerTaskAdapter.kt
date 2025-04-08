package com.locatify.locatify.adapters

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.locatify.locatify.MainActivity
import com.locatify.locatify.R
import com.locatify.locatify.database.TaskDBHelper
import com.locatify.locatify.fragments.AddTaskFragment
import com.locatify.locatify.fragments.TaskDetailsFragment
import com.locatify.locatify.modals.TaskModal

class RecyclerTaskAdapter(var context: Context, var arrTaskList: ArrayList<TaskModal>): RecyclerView.Adapter<RecyclerTaskAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var cardLinearView: LinearLayout = itemView.findViewById(R.id.cardLinearView)
        var txtTaskName: TextView = itemView.findViewById(R.id.txtTaskName)
        var txtTaskTime: TextView = itemView.findViewById(R.id.txtTaskTime)
        var btnTaskMore: ImageButton = itemView.findViewById(R.id.btnTaskMore)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerTaskAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.task_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerTaskAdapter.ViewHolder, position: Int) {



        holder.txtTaskName.text = arrTaskList[position].taskName
        holder.txtTaskTime.text = arrTaskList[position].taskTime
        holder.cardLinearView.setOnClickListener {
            (context as MainActivity).loadFragment(TaskDetailsFragment.newInstance(arrTaskList[position].taskName, arrTaskList[position].taskDate, arrTaskList[position].taskTime, arrTaskList[position].taskLoc), 1)
        }
        holder.btnTaskMore.setOnClickListener{view ->
            val popupMenu: PopupMenu = PopupMenu(context, view)
            popupMenu.menuInflater.inflate(R.menu.menu_more, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                if(item.itemId == R.id.btnMenuDel) {
                    val dialog: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
                    dialog.setTitle("Delete task")
                    dialog.setMessage("Are you sure to delete following task\n${arrTaskList[position].taskName}?")
                    dialog.setPositiveButton("Yes", object: DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            TaskDBHelper(context).apply {
                                this.writableDatabase
                            }.deleteTask(arrTaskList[position].id!!)
                            dialog?.dismiss()
                        }
                    })
                    dialog.setNegativeButton("No", object : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            Toast.makeText(context, "No", Toast.LENGTH_SHORT).show()
                            dialog?.dismiss()
                        }
                    })
                    dialog.show()
                }
                else if (item.itemId == R.id.btnMenuUpdate) {

                }
                else {
                    Toast.makeText(context, "Not Implemented yet", Toast.LENGTH_SHORT).show()
                }
                true
            }
            popupMenu.show()
        }


    }

    override fun getItemCount(): Int {
        return arrTaskList.size
    }
}