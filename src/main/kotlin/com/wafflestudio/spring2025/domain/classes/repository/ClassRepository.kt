package com.wafflestudio.spring2025.domain.classes.repository

import com.wafflestudio.spring2025.domain.classes.entity.Class
import org.springframework.data.repository.CrudRepository

interface ClassRepository : CrudRepository<Class, Long> {
    fun findByOwnerId(ownerId: Long): List<Class>
}
