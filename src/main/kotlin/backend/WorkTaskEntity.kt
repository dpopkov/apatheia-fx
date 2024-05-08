package io.dpopkov.apatheiafx.backend

import io.dpopkov.apatheiafx.model.WorkTask
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class WorkTaskEntity(
    val title: String,
    @ManyToOne
    val parent: WorkTaskEntity? = null,
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    val children: List<WorkTaskEntity> = mutableListOf(),
    val description: String? = null,
    val createdAt: LocalDateTime,
    val finishedAt: LocalDateTime? = null,
    @Id
    @GeneratedValue
    var id: Long? = null,
) {
    override fun toString(): String {
        return "WorkTaskEntity[id=$id, title=$title, parent=${parent?.title}, createdAt=$createdAt]"
    }

    fun toModel(): WorkTask {
        return WorkTask(
            title = this.title,
            parent = this.parent?.toModel(),
            description = this.description,
            createdAt = this.createdAt,
            finishedAt = this.finishedAt,
            id = this.id,
        )
    }
}

fun WorkTask.toEntity(): WorkTaskEntity {
    if (this.parent != null) {
        throw IllegalArgumentException("Cannot convert with non-null parent, use converter instead")
    }
    return WorkTaskEntity(
        title = this.title,
        description = this.description,
        createdAt = this.createdAt,
        finishedAt = this.finishedAt,
    )
}
