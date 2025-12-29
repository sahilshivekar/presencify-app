package edu.watumull.presencify.core.data.mapper.admin

import edu.watumull.presencify.core.data.dto.admin.AdminDto
import edu.watumull.presencify.core.domain.model.admin.Admin

fun AdminDto.toDomain(): Admin = Admin(
    id = id,
    email = email,
    username = username,
    password = password,
    refreshToken = refreshToken,
    isVerified = isVerified
)
