package io.dpopkov.apatheiafx.model

import java.time.LocalDateTime

data class WorkTask(
    var title: String,
    var parent: WorkTask? = null,
    var description: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var finishedAt: LocalDateTime? = null,
    var id: Long? = null,
) {
    val pomidors: MutableList<Pomidor> = mutableListOf()
    val pomidorsCount: Long get() = pomidors.size.toLong()
    val totalPomidorsMinutes: Long
        get() = pomidors.sumOf { p -> p.durationMinutes }
    val isFinished: Boolean
        get() = finishedAt != null

    fun finish() {
        finishedAt = LocalDateTime.now()
    }
}
