package edu.watumull.presencify.feature.users.submit_student_biometrics

actual suspend fun extractFaceDescriptors(images: List<ByteArray>): List<FloatArray>? {
    // Not implemented on JVM
    return null
}
