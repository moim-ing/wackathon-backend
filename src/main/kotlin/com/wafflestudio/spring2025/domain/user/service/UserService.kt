package com.wafflestudio.spring2025.domain.user.service

import com.wafflestudio.spring2025.domain.auth.exception.AuthErrorCode
import com.wafflestudio.spring2025.domain.auth.exception.AuthValidationException
import com.wafflestudio.spring2025.domain.auth.exception.AuthenticationRequiredException
import com.wafflestudio.spring2025.domain.user.dto.core.UserDto
import com.wafflestudio.spring2025.domain.user.exception.EmailChangeForbiddenException
import com.wafflestudio.spring2025.domain.user.model.User
import com.wafflestudio.spring2025.domain.user.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun me(user: User?): UserDto {
        if (user == null) throw AuthenticationRequiredException()

        return UserDto(
            id = user.id!!,
            email = user.email,
            name = user.name,
        )
    }

    fun patchMe(
        user: User,
        name: String?,
        email: String?,
        password: String?,
    ) {
        if (email != null) {
            throw EmailChangeForbiddenException()
        }
        name?.let { validateName(it) }
        password?.let { validatePassword(it) }

        name?.let { user.name = it }
        password?.let { user.passwordHash = BCrypt.hashpw(it, BCrypt.gensalt()) }
        userRepository.save(user)
    }

    private fun validateName(name: String) {
        if (name.isBlank()) {
            throw AuthValidationException(AuthErrorCode.BAD_NAME)
        }
    }

    private fun validatePassword(password: String) {
        if (password.length < 8) {
            throw AuthValidationException(AuthErrorCode.BAD_PASSWORD)
        }
        if (!password.any { it.isLetter() }) {
            throw AuthValidationException(AuthErrorCode.BAD_PASSWORD)
        }
        if (!password.any { it.isDigit() }) {
            throw AuthValidationException(AuthErrorCode.BAD_PASSWORD)
        }
    }
}
