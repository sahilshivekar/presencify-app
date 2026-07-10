package edu.watumull.presencify.feature.attendance.recognize_student

expect class PlatformImage

expect class FaceEmbeddingExtractor {

    fun initialize(context: Any)

    fun generateSingleDescriptor(image: PlatformImage): FloatArray?

    fun compare(
        descriptor1: FloatArray,
        descriptor2: FloatArray
    ): Float

    fun close()
}