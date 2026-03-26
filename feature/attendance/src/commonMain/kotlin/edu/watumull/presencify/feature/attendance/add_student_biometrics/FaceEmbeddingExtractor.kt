package edu.watumull.presencify.feature.attendance.add_student_biometrics

expect class FaceEmbeddingExtractor() {
    suspend fun extractEmbedding(imageBytes: ByteArray): FloatArray?
}
