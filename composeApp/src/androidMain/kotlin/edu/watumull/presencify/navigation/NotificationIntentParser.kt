package edu.watumull.presencify.navigation

import android.content.Intent
import android.os.Bundle
import edu.watumull.presencify.navigation.notification.ScheduleNotificationDeepLink

private const val EXTRA_TYPE = "type"
private const val EXTRA_CLASS_ID = "classId"

fun Intent.toScheduleNotificationDeepLink(): ScheduleNotificationDeepLink? {
    val extras: Bundle = extras ?: return null
    val type = extras.getString(EXTRA_TYPE) ?: return null
    val classId = extras.getString(EXTRA_CLASS_ID) ?: return null
    return ScheduleNotificationDeepLink(type = type, classId = classId)
}
