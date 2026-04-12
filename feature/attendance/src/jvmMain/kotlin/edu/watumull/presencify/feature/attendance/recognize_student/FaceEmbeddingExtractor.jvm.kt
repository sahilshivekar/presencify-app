package edu.watumull.presencify.feature.attendance.recognize_student

import java.awt.image.BufferedImage

// Tell KMP that on JVM, PlatformImage IS a BufferedImage
actual typealias PlatformImage = BufferedImage

actual class FaceEmbeddingExtractor actual constructor() {

    actual fun initialize(context: Any) {
        // No-op for now
    }

    actual fun alignAndCrop(image: PlatformImage, face: FaceBoundingBox, landmarks: FloatArray): PlatformImage {
        throw NotImplementedError("FaceEmbeddingExtractor.alignAndCrop is not implemented on JVM")
    }

    actual fun generateSingleDescriptor(image: PlatformImage): FloatArray? {
        throw NotImplementedError("FaceEmbeddingExtractor.generateSingleDescriptor is not implemented on iOS")
    }

    actual fun close() {
        // No-op for now
    }
}