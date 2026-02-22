package com.wafflestudio.spring2025.domain.auth

import com.wafflestudio.spring2025.domain.auth.exception.AuthenticationRequiredException
import com.wafflestudio.spring2025.domain.user.model.User
import com.wafflestudio.spring2025.domain.user.repository.UserRepository
import kotlin.reflect.jvm.kotlinFunction
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserArgumentResolver(
    private val userRepository: UserRepository,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(LoggedInUser::class.java) && parameter.parameterType == User::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): User? {
        val userId = webRequest.getAttribute("userId", 0) as? Long ?: return null
        val kotlinParam = parameter.kotlinFunction?.parameters
            ?.getOrNull(parameter.parameterIndex + 1)
        val isNullable = kotlinParam?.type?.isMarkedNullable == true
        return if (isNullable) {
            userRepository.findById(userId).orElse(null)
        } else {
            userRepository.findById(userId).orElseThrow { AuthenticationRequiredException() }
        }
    }
}
