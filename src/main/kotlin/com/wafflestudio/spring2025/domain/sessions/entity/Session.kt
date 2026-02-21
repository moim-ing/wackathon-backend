package com.wafflestudio.spring2025.domain.sessions.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("sessions")
class Session(
    @Id
    var id: Long? = null,
    var classId: Long,
    var videoUrl: String,
    var referenceS3Key: String? = null,
    var status: String = "PREPARING",
    @CreatedDate
    var createdAt: Instant? = null,
    @LastModifiedDate
    var updatedAt: Instant? = null,
)
