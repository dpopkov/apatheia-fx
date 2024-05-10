package io.dpopkov.apatheiafx.backend

import io.dpopkov.apatheiafx.model.WorkTask
import org.springframework.stereotype.Component

@Component
class WorkTaskConverter(
    private val workTaskRepository: WorkTaskRepository
) {
    fun toEntity(workTask: WorkTask): WorkTaskEntity {
        if (workTask.parent == null) {
            return workTask.toEntity()
        }
        val parentId = workTask.parent?.id
        val parentEntity = if (parentId != null) {
            workTaskRepository.findById(parentId).orElseThrow {
                IllegalArgumentException("Cannot find parent by id $parentId")
            }
        } else null
        return WorkTaskEntity(
            title = workTask.title,
            parent = parentEntity,
            description = workTask.description,
            createdAt = workTask.createdAt,
            finishedAt = workTask.finishedAt,
            id = workTask.id,
        )
    }
}
