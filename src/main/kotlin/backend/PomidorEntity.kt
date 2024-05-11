package io.dpopkov.apatheiafx.backend

import io.dpopkov.apatheiafx.model.Pomidor
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime
import java.time.Duration

@Entity
class PomidorEntity(
    val name: String,
    val start: LocalDateTime,
    val finish: LocalDateTime,
    @ManyToOne
    var workTask: WorkTaskEntity? = null,
    @Id
    @GeneratedValue
    var id: Long? = null,
) {
    fun toModel(): Pomidor {
        return Pomidor(
            name = this.name,
            start = this.start,
            finish = this.finish,
            durationMinutes = Duration.between(this.start, this.finish).toMinutes(),
            id = this.id
        ).apply {
            val entWorkTask = this@PomidorEntity.workTask
            if (entWorkTask != null) {
                this.workTask = entWorkTask.toModel()
            }
        }
    }
}

fun Pomidor.toEntity(): PomidorEntity {
    if (!this.isFinished) {
        throw IllegalStateException("Cannot translate to entity an unfinished item")
    }
    return PomidorEntity(
        name = this.name,
        start = this.start,
        finish = this.finish!!,
        id = this.id,
    )
}

