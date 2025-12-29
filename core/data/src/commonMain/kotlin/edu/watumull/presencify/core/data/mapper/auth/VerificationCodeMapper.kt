package edu.watumull.presencify.core.data.mapper.auth

import edu.watumull.presencify.core.data.dto.auth.VerificationCodeDto
import edu.watumull.presencify.core.domain.model.auth.VerificationCode

fun VerificationCodeDto.toDomain(): VerificationCode =
    VerificationCode(
        id = id,
        email = email,
        code = code,
        purpose = purpose,
        expiresAt = expiresAt
    )
