package io.dpopkov.apatheiafx.backend

import io.dpopkov.apatheiafx.model.Pomidor
import org.springframework.stereotype.Service

@Service
class PomidorService(
    private val repository: PomidorRepository,
    private val workTaskRepository: WorkTaskRepository,
) {
    fun getAll(): List<Pomidor> {
        val all = repository.findAll()
        return all.map {
            it.toModel()
        }
    }

    fun save(item: Pomidor): Pomidor {
        if (item.isFinished) {
            val entToSave = item.toEntity()
            if (item.workTask != null) {
                val taskId = item.workTask!!.id ?: throw IllegalArgumentException("Attempt to use work task with id=null")
                val entWorkTask = workTaskRepository.findById(taskId)
                    .orElseThrow { IllegalArgumentException("Cannot find work task by id=${item.workTask!!.id}")}
                entToSave.workTask = entWorkTask
            }
            val ent = repository.save(entToSave)
            return ent.toModel()
        } else {
            throw IllegalStateException("Cannot save not finished item")
        }
    }

    fun removeById(id: Long) {
        repository.deleteById(id)
    }

    fun update(item: Pomidor) {
        val itemId =item.id ?: throw IllegalArgumentException("Cannot update item with id=null")
        val found = repository.findById(itemId).orElseThrow {
            IllegalArgumentException("Cannot update non-existing item with id=${item.id}")
        }.toModel()
        found.name = item.name
        repository.save(found.toEntity())
    }
}
