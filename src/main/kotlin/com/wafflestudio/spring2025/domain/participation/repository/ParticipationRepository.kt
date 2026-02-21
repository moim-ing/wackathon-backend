package com.wafflestudio.spring2025.domain.participation.repository

import com.wafflestudio.spring2025.domain.participation.entity.Participation
import org.springframework.data.repository.CrudRepository

interface ParticipationRepository : CrudRepository<Participation, Long> {
    fun countBySessionId(sessionId: Long): Int

    fun findBySessionId(sessionId: Long): List<Participation>
}
