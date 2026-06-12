package edu.watumull.presencify.feature.attendance.recognize_student

import android.graphics.BitmapFactory

actual fun ByteArray.toPlatformImage(): PlatformImage? =
    BitmapFactory.decodeByteArray(this, 0, size)
