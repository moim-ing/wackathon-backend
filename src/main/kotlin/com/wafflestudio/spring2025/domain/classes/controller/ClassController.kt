package com.wafflestudio.spring2025.domain.classes.controller

import com.wafflestudio.spring2025.domain.auth.LoggedInUser
import com.wafflestudio.spring2025.domain.auth.exception.AuthenticationRequiredException
import com.wafflestudio.spring2025.domain.classes.dto.ClassCreateRequest
import com.wafflestudio.spring2025.domain.classes.dto.ClassCreateResponse
import com.wafflestudio.spring2025.domain.classes.dto.ClassDetailResponse
import com.wafflestudio.spring2025.domain.classes.service.ClassService
import com.wafflestudio.spring2025.domain.user.model.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/classes")
class ClassController(
    private val classService: ClassService,
) {
    @PostMapping
    fun createClass(
        @RequestBody request: ClassCreateRequest,
        @LoggedInUser user: User?,
    ): ResponseEntity<ClassCreateResponse> {
        val uid = user?.id ?: throw AuthenticationRequiredException()
        val response = classService.createClass(request, uid)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getClassDetail(
        @PathVariable("id") classId: Long,
        @LoggedInUser user: User?,
    ): ResponseEntity<ClassDetailResponse> {
        val response = classService.getClassDetail(classId)
        return ResponseEntity.ok(response)
    }
}
