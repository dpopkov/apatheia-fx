package io.dpopkov.apatheiafx.backend

import io.dpopkov.apatheiafx.model.WorkTask
import org.springframework.stereotype.Service

@Service
class WorkTaskService(
    private val repository: WorkTaskRepository,
    private val woktTaskConverter: WorkTaskConverter,
) {
    fun getAll(): List<WorkTask> {
        val all = repository.findAll()
        return all.map {
            it.toModel()
        }
    }

    fun save(item: WorkTask): WorkTask {
        val entToSave: WorkTaskEntity = woktTaskConverter.toEntity(item)
        val entSaved = repository.save(entToSave)
        return entSaved.toModel()
    }

    fun removeById(id: Long) {
        repository.deleteById(id)
    }

    fun update(item: WorkTask) {
        val itemId =item.id ?: throw IllegalArgumentException("Cannot update work task with id=null")
        val found = repository.findById(itemId).orElseThrow {
            IllegalArgumentException("Cannot find work task id=$itemId")
        }
        found.title = item.title
        repository.save(found)
    }
}
