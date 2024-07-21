package com.cuebit.io.DataModels

data class AlarmDataModel(
    var alarmId: String ?= null,
    var task_habit_id: String ?= null,
    var date: String ?= null,
    var time: String ?= null
)
