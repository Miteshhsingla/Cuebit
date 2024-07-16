package com.cuebit.io.DataModels

data class TaskDataModel(
    var id: String?= null,
    var title: String?= null,
    var description: String?= null,
    var category: String?= null,
    var dateAndTime: String?= null,
    var frequency: String ?= null,
    var markAsDone: String ?= "0"
)



