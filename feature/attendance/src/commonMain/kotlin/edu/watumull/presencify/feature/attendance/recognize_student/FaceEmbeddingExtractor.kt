package edu.watumull.presencify.feature.attendance.recognize_student

// 1. Create a platform-agnostic image type
expect class PlatformImage

data class FaceBoundingBox(val x: Float, val y: Float, val w: Float, val h: Float)

expect class FaceEmbeddingExtractor() {

    // Notice we removed OrtSession and OrtEnvironment.
    // They are implementation details and belong ONLY in Android.

    // 2. Use 'Any' so we can pass the Android Context safely from Compose
    fun initialize(context: Any)

    // 3. Use PlatformImage instead of Bitmap
    fun alignAndCrop(image: PlatformImage, face: FaceBoundingBox, landmarks: FloatArray): PlatformImage

    /**
     * Runs the complete pipeline: Detect Face -> Crop -> Extract Embedding -> Normalize.
     * Returns null if no face is detected.
     */
    fun generateSingleDescriptor(image: PlatformImage): FloatArray?

    fun close()
}