package edu.watumull.presencify.feature.attendance.add_student_biometrics

actual class FaceEmbeddingExtractor {
    actual suspend fun extractEmbedding(imageBytes: ByteArray): FloatArray? {
        // TODO: Implement Vision Framework embedding extraction on iOS
        return null
    }
}
