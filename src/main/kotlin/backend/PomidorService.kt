package io.dpopkov.apatheiafx.backend

import io.dpopkov.apatheiafx.model.Pomidor
import org.springframework.stereotype.Service

@Service
class PomidorService(
    private val repository: PomidorRepository
) {
    fun getAll(): List<Pomidor> {
        val all = repository.findAll()
        return all.map {
            it.toModel()
        }
    }

    fun save(item: Pomidor): Pomidor {
        if (item.isFinished) {
            val ent = repository.save(item.toEntity())
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
