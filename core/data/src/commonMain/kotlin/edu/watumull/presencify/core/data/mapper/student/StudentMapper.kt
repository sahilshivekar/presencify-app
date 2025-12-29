package edu.watumull.presencify.core.data.mapper.student

import edu.watumull.presencify.core.data.dto.student.StudentDto
import edu.watumull.presencify.core.data.mapper.academics.toDomain
import edu.watumull.presencify.core.data.mapper.attendance.toDomain
import edu.watumull.presencify.core.domain.model.student.Student

fun StudentDto.toDomain(): Student = Student(
    id = id,
    name = name,
    gender = gender,
    email = email,
    phone = phone,
    prn = prn,
    password = password,
    parentPhone = parentPhone,
    rollNo = rollNo,
    dob = dob,
    admissionYear = admissionYear,
    address = address,
    city = city,
    state = state,
    pincode = pincode,
    branchId = branchId,
    schemeId = schemeId,
    branch = branch?.toDomain(),
    scheme = scheme?.toDomain(),
    studentSemesters = studentSemesters?.map { it.toDomain() },
    studentDivisions = studentDivisions?.map { it.toDomain() },
    studentBatches = studentBatches?.map { it.toDomain() },
    attendanceStudents = attendanceStudents?.map { it.toDomain() }
)
