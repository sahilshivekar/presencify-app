package edu.watumull.presencify.feature.attendance.recognize_student

import java.awt.image.BufferedImage

actual typealias PlatformImage = BufferedImage

actual class FaceEmbeddingExtractor {

    actual fun initialize(context: Any) {
        TODO("Not yet implemented")
    }

    actual fun generateSingleDescriptor(image: PlatformImage): FloatArray? {
        TODO("Not yet implemented")
    }

    actual fun compare(
        descriptor1: FloatArray,
        descriptor2: FloatArray
    ): Float {
        TODO("Not yet implemented")
    }

    actual fun close() {
        TODO("Not yet implemented")
    }
}