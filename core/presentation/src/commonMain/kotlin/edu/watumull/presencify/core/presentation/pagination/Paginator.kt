package edu.watumull.presencify.core.presentation.pagination

import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result

class Paginator<Key, Item>(
    private val initialKey: Key,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextKey: Key) -> Result<Item, DataError.Remote>,
    private val getNextKey: suspend (currentKey: Key, result: Item) -> Key,
    private val onError: suspend (DataError.Remote) -> Unit,
    private val onSuccess: suspend (result: Item, newKey: Key) -> Unit,
    private val endReached: (currentKey: Key, result: Item) -> Boolean
) {

    private var currentKey = initialKey
    private var isMakingRequest = false
    private var isEndReached = false

    suspend fun loadNextItems() {
        if(isMakingRequest || isEndReached) {
            return
        }

        isMakingRequest = true
        onLoadUpdated(true)

        val result = onRequest(currentKey)
        isMakingRequest = false

        when (result) {
            is Result.Error -> {
                onError(result.error)
                onLoadUpdated(false)
                return
            }
            is Result.Success -> {
                val item = result.data
                currentKey = getNextKey(currentKey, item)
                onSuccess(item, currentKey)
                onLoadUpdated(false)
                isEndReached = endReached(currentKey, item)
            }
        }
    }

    fun reset() {
        currentKey = initialKey
        isEndReached = false
    }
}