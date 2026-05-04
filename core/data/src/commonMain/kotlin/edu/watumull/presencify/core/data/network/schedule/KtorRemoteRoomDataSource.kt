package edu.watumull.presencify.core.data.network.schedule

import edu.watumull.presencify.core.data.HttpClientProvider
import edu.watumull.presencify.core.data.dto.schedule.ClassDto
import edu.watumull.presencify.core.data.dto.schedule.RoomDto
import edu.watumull.presencify.core.data.dto.schedule.RoomListWithTotalCountDto
import edu.watumull.presencify.core.data.dto.schedule.request.AddRoomRequest
import edu.watumull.presencify.core.data.dto.schedule.request.UpdateRoomRequest
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.ADD_ROOM
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.GET_ROOMS
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.GET_ROOM_BY_ID
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.GET_ROOM_SCHEDULE
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.REMOVE_ROOM
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.UPDATE_ROOM
import edu.watumull.presencify.core.data.repository.safeCall
import edu.watumull.presencify.core.data.util.toApiTimeString
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.enums.DayOfWeek
import edu.watumull.presencify.core.domain.enums.RoomSortBy
import edu.watumull.presencify.core.domain.enums.RoomSortOrder
import edu.watumull.presencify.core.domain.enums.RoomType
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class KtorRemoteRoomDataSource(
    private val clientProvider: HttpClientProvider
) : RemoteRoomDataSource {
    

    override suspend fun getRooms(
        searchQuery: String?,
        sortBy: RoomSortBy?,
        sortOrder: RoomSortOrder?,
        freeBetweenStartTime: LocalTime?,
        freeBetweenEndTime: LocalTime?,
        dayOfWeek: DayOfWeek?,
        page: Int?,
        limit: Int?,
        getAll: Boolean?,
        type: RoomType?,
        minCapacity: Int?,
        maxCapacity: Int?
    ): Result<RoomListWithTotalCountDto, DataError.Remote> {
        return safeCall<RoomListWithTotalCountDto> {
            clientProvider.getClient().get(GET_ROOMS) {
                searchQuery?.let { parameter("searchQuery", it) }
                sortBy?.let { parameter("sortBy", it.value) }
                sortOrder?.let { parameter("sortOrder", it.value) }
                freeBetweenStartTime?.let {
                    parameter("freeBetweenStartTime", it.toApiTimeString()) // Formats as HH:MM:SS
                }
                freeBetweenEndTime?.let {
                    parameter("freeBetweenEndTime", it.toApiTimeString()) // Formats as HH:MM:SS
                }
                dayOfWeek?.let { parameter("dayOfWeek", it.value) }
                page?.let { parameter("page", it) }
                limit?.let { parameter("limit", it) }
                getAll?.let { parameter("getAll", it) }
                type?.let { parameter("type", it.value) }
                minCapacity?.let { parameter("minCapacity", it) }
                maxCapacity?.let { parameter("maxCapacity", it) }
            }
        }
    }

    override suspend fun addRoom(
        roomNumber: String,
        sittingCapacity: Int,
        name: String?,
        type: RoomType?
    ): Result<RoomDto, DataError.Remote> {
        return safeCall<RoomDto> {
            clientProvider.getClient().post(ADD_ROOM) {
                contentType(ContentType.Application.Json)
                setBody(
                    AddRoomRequest(
                        roomNumber = roomNumber,
                        sittingCapacity = sittingCapacity,
                        name = name,
                        type = type?.value
                    )
                )
            }
        }
    }

    override suspend fun getRoomById(id: String): Result<RoomDto, DataError.Remote> {
        return safeCall<RoomDto> {
            clientProvider.getClient().get("$GET_ROOM_BY_ID/$id")
        }
    }

    override suspend fun getRoomSchedule(
        id: String,
        startDate: LocalDate,
        endDate: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime
    ): Result<List<ClassDto>, DataError.Remote> {
        return safeCall<List<ClassDto>> {
            clientProvider.getClient().get("$GET_ROOM_SCHEDULE/$id") {
                parameter("startDate", startDate.toString())
                parameter("endDate", endDate.toString())
                parameter("startTime", startTime.toApiTimeString())
                parameter("endTime", endTime.toApiTimeString())
            }
        }
    }

    override suspend fun updateRoom(
        id: String,
        roomNumber: String?,
        sittingCapacity: Int?,
        name: String?,
        type: RoomType?
    ): Result<RoomDto, DataError.Remote> {
        return safeCall<RoomDto> {
            clientProvider.getClient().put("$UPDATE_ROOM/$id") {
                contentType(ContentType.Application.Json)
                setBody(
                    UpdateRoomRequest(
                        roomNumber = roomNumber,
                        sittingCapacity = sittingCapacity,
                        name = name,
                        type = type?.value
                    )
                )
            }
        }
    }

    override suspend fun removeRoom(id: String): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().delete("$REMOVE_ROOM/$id")
        }
    }
}
