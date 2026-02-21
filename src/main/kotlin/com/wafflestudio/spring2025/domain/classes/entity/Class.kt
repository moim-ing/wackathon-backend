package com.wafflestudio.spring2025.domain.classes.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("classes")
class Class(
    @Id
    var id: Long? = null,
    var title: String,
    // store owner as FK id to keep consistency with project User model
    var ownerId: Long,
    @CreatedDate
    var createdAt: Instant? = null,
    @LastModifiedDate
    var updatedAt: Instant? = null,
)
