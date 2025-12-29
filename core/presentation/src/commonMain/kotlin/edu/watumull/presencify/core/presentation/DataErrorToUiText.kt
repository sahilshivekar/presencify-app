package edu.watumull.presencify.core.presentation

import edu.watumull.presencify.core.domain.DataError
import presencify.core.presentation.generated.resources.Res
import presencify.core.presentation.generated.resources.error_disk_full
import presencify.core.presentation.generated.resources.error_no_internet
import presencify.core.presentation.generated.resources.error_serialization
import presencify.core.presentation.generated.resources.error_unauthorized
import presencify.core.presentation.generated.resources.error_unknown
import presencify.core.presentation.generated.resources.server_error
import presencify.core.presentation.generated.resources.the_request_timed_out
import presencify.core.presentation.generated.resources.youve_hit_your_rate_limit

fun DataError.toUiText(): UiText {

    return when(this) {
        // Dynamic String Case
        is DataError.Remote.VALIDATION_ERROR -> {
            UiText.DynamicString(this.message)
        }
        // Resource ID Cases
        DataError.Local.DISK_FULL -> UiText.StringResourceId(Res.string.error_disk_full)
        DataError.Local.UNKNOWN -> UiText.StringResourceId(Res.string.error_unknown)
        DataError.Remote.REQUEST_TIMEOUT -> UiText.StringResourceId(Res.string.the_request_timed_out)
        DataError.Remote.TOO_MANY_REQUESTS -> UiText.StringResourceId(Res.string.youve_hit_your_rate_limit)
        DataError.Remote.NO_INTERNET -> UiText.StringResourceId(Res.string.error_no_internet)
        DataError.Remote.SERVER_ERROR -> UiText.StringResourceId(Res.string.server_error)
        DataError.Remote.SERIALIZATION -> UiText.StringResourceId(Res.string.error_serialization)
        DataError.Remote.UNKNOWN -> UiText.StringResourceId(Res.string.error_unknown)
        DataError.Remote.UNAUTHORIZED -> UiText.StringResourceId(Res.string.error_unauthorized)
    }
}