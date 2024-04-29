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
}
