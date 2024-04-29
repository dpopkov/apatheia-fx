package io.dpopkov.apatheiafx.backend

import org.springframework.data.repository.CrudRepository

interface PomidorRepository : CrudRepository<PomidorEntity, Long> {
    override fun findAll(): List<PomidorEntity>
}
