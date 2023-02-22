package hu.zsof.tollapps.network.model

import java.sql.Timestamp

data class Event(
    val id: Int,
    var participants: List<String> = mutableListOf(),
    var date: Timestamp,
    var deadline: Timestamp,
)
