package io.dpopkov.apatheiafx.model

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

data class Pomidor(
    val name: String = defaultName(),
    val start: LocalDateTime = LocalDateTime.now(),
    var finish: LocalDateTime? = null,
    var durationMinutes: Long = -1,
    var id: Long? = null,
) {
    val isFinished: Boolean
        get() = finish != null && durationMinutes != -1L

    fun finishIt(finishTime: LocalDateTime = LocalDateTime.now()) {
        finish = finishTime
        durationMinutes = Duration.between(start, finish).toMinutes()
    }

    companion object {
        fun defaultName(): String {
            val n = LocalTime.now()
            return "p-${n.minute}:${n.second}"
        }
    }
}

