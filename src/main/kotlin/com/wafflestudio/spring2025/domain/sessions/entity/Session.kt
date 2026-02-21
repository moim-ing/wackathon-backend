package com.wafflestudio.spring2025.domain.sessions.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("sessions")
class Session(
    @Id
    var id: Long? = null,
    @Column("class_id")
    var classId: Long,
    @Column("video_id")
    var videoId: String,
    @Column("source_key")
    var sourceKey: String? = null,
    var status: SessionStatus = SessionStatus.ACTIVE,
    @CreatedDate
    @Column("created_at")
    var createdAt: Instant? = null,
    @LastModifiedDate
    @Column("updated_at")
    var updatedAt: Instant? = null,
)

enum class SessionStatus {
    ACTIVE,
    PAUSED,
    CLOSED,
}
