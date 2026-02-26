package edu.watumull.presencify.core.data.repository.schedule

import edu.watumull.presencify.core.data.mapper.schedule.toDomain
import edu.watumull.presencify.core.data.network.schedule.RemoteRoomDataSource
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.enums.DayOfWeek
import edu.watumull.presencify.core.domain.enums.RoomSortBy
import edu.watumull.presencify.core.domain.enums.RoomSortOrder
import edu.watumull.presencify.core.domain.enums.RoomType
import edu.watumull.presencify.core.domain.map
import edu.watumull.presencify.core.domain.model.schedule.ClassSession
import edu.watumull.presencify.core.domain.model.schedule.Room
import edu.watumull.presencify.core.domain.model.schedule.RoomListWithTotalCount
import edu.watumull.presencify.core.domain.repository.schedule.RoomRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class RoomRepositoryImpl(
    private val remoteDataSource: RemoteRoomDataSource
) : RoomRepository {

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
    ): Result<RoomListWithTotalCount, DataError.Remote> {
        return remoteDataSource.getRooms(
            searchQuery = searchQuery,
            sortBy = sortBy,
            sortOrder = sortOrder,
            freeBetweenStartTime = freeBetweenStartTime,
            freeBetweenEndTime = freeBetweenEndTime,
            dayOfWeek = dayOfWeek,
            page = page,
            limit = limit,
            getAll = getAll,
            type = type,
            minCapacity = minCapacity,
            maxCapacity = maxCapacity
        ).map { response ->
            RoomListWithTotalCount(
                rooms = response.rooms.map { it.toDomain() },
                totalCount = response.totalCount
            )
        }
    }

    override suspend fun addRoom(
        roomNumber: String,
        sittingCapacity: Int,
        name: String?,
        type: RoomType?
    ): Result<Room, DataError.Remote> {
        return remoteDataSource.addRoom(roomNumber, sittingCapacity, name, type).map { it.toDomain() }
    }

    override suspend fun getRoomById(roomId: String): Result<Room, DataError.Remote> {
        return remoteDataSource.getRoomById(roomId).map { it.toDomain() }
    }

    override suspend fun getRoomSchedule(
        roomId: String,
        startDate: LocalDate,
        endDate: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime
    ): Result<List<ClassSession>, DataError.Remote> {
        return remoteDataSource.getRoomSchedule(roomId, startDate, endDate, startTime, endTime).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun updateRoom(
        roomId: String,
        roomNumber: String?,
        sittingCapacity: Int?,
        name: String?,
        type: RoomType?
    ): Result<Room, DataError.Remote> {
        return remoteDataSource.updateRoom(roomId, roomNumber, sittingCapacity, name, type).map { it.toDomain() }
    }

    override suspend fun removeRoom(roomId: String): Result<Unit, DataError.Remote> {
        return remoteDataSource.removeRoom(roomId)
    }
}
