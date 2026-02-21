package com.wafflestudio.spring2025.domain.sessions.repository

import com.wafflestudio.spring2025.domain.sessions.entity.Session
import org.springframework.data.repository.CrudRepository

interface SessionRepository : CrudRepository<Session, Long> {
    fun findByClassId(classId: Long): List<Session>
}
