package com.locatify.locatify.modals

import java.io.Serializable

class TaskModal(var taskName: String, var taskDate: String, var taskTime: String, var taskLoc: Pair<Double, Double>? = null, var id:Int? = null) {
}