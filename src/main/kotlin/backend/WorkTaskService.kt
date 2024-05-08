package io.dpopkov.apatheiafx.backend

import io.dpopkov.apatheiafx.model.WorkTask
import org.springframework.stereotype.Service

@Service
class WorkTaskService(
    private val repository: WorkTaskRepository,
    private val wortTaskConverter: WorkTaskConverter,
) {
    fun getAll(): List<WorkTask> {
        val all = repository.findAll()
        return all.map {
            it.toModel()
        }
    }

    fun save(item: WorkTask): WorkTask {
        val entToSave: WorkTaskEntity = if (item.parent == null) {
            item.toEntity()
        } else {
            wortTaskConverter.toEntity(item)
        }
        val entSaved = repository.save(entToSave)
        return entSaved.toModel()
    }
}
