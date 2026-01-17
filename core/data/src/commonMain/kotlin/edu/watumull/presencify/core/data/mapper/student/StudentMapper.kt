package edu.watumull.presencify.core.data.mapper.student

import edu.watumull.presencify.core.data.dto.student.StudentDto
import edu.watumull.presencify.core.data.mapper.academics.toDomain
import edu.watumull.presencify.core.data.mapper.attendance.toDomain
import edu.watumull.presencify.core.domain.model.student.Student

fun StudentDto.toDomain(): Student = Student(
    id = id,
    firstName = firstName,
    lastName = lastName,
    middleName = middleName,
    gender = gender,
    email = email,
    parentEmail = parentEmail,
    phoneNumber = phoneNumber,
    prn = prn,
    password = password,
    rollNo = rollNo,
    dob = dob,
    admissionYear = admissionYear,
    admissionType = admissionType,
    branchId = branchId,
    schemeId = schemeId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    refreshToken = refreshToken,
    studentImageUrl = studentImageUrl,
    studentImgPublicId = studentImgPublicId,
    branch = branch?.toDomain(),
    scheme = scheme?.toDomain(),
    studentSemesters = studentSemesters?.map { it.toDomain() },
    studentDivisions = studentDivisions?.map { it.toDomain() },
    studentBatches = studentBatches?.map { it.toDomain() },
    attendanceStudents = attendanceStudents?.map { it.toDomain() }
)
