package edu.watumull.presencify.feature.attendance.recognize_student

import platform.UIKit.UIImage

actual typealias PlatformImage = UIImage

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