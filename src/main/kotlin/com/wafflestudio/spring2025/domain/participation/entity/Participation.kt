package com.wafflestudio.spring2025.domain.participation.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("participation")
class Participation(
    @Id
    var id: Long? = null,
    var sessionId: Long,
    var userId: Long? = null,
    var guestName: String? = null,
    var verifiedAt: Instant,
    @CreatedDate
    var createdAt: Instant? = null,
    @LastModifiedDate
    var updatedAt: Instant? = null,
)
