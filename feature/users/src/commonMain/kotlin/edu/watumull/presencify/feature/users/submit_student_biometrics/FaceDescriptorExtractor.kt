package edu.watumull.presencify.feature.users.submit_student_biometrics

import edu.watumull.presencify.feature.attendance.recognize_student.FaceEmbeddingExtractor
import edu.watumull.presencify.feature.attendance.recognize_student.toPlatformImage

expect suspend fun extractFaceDescriptors(images: List<ByteArray>): List<FloatArray>?
