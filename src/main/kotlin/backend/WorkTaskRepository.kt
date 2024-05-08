package io.dpopkov.apatheiafx.backend

import org.springframework.data.repository.CrudRepository

interface WorkTaskRepository : CrudRepository<WorkTaskEntity, Long> {
    override fun findAll(): List<WorkTaskEntity>
}
