package edu.watumull.presencify.core.data.mapper.academics

import edu.watumull.presencify.core.data.dto.academics.DivisionCourseDto
import edu.watumull.presencify.core.data.dto.academics.DivisionCoursesDto
import edu.watumull.presencify.core.data.dto.academics.SemesterCoursesDto
import edu.watumull.presencify.core.domain.model.academics.DivisionCourse
import edu.watumull.presencify.core.domain.model.academics.DivisionCourses
import edu.watumull.presencify.core.domain.model.academics.SemesterCourses

fun DivisionCourseDto.toDomain(): DivisionCourse = DivisionCourse(
    id = id,
    divisionId = divisionId,
    courseId = courseId,
    course = course?.toDomain(),
    division = division?.toDomain()
)

fun SemesterCoursesDto.toDomain(): SemesterCourses = SemesterCourses(
    compulsoryCourses = compulsoryCourses.map { it.toDomain() },
    optionalCourses = optionalCourses.map { it.toDomain() }
)

fun DivisionCoursesDto.toDomain(): DivisionCourses = DivisionCourses(
    compulsoryCourses = compulsoryCourses.map { it.toDomain() },
    optionalCourses = optionalCourses.map { it.toDomain() }
)
