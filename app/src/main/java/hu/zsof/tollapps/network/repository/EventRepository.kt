package hu.zsof.tollapps.network.repository

import hu.zsof.tollapps.network.ApiService
import hu.zsof.tollapps.network.model.Event
import hu.zsof.tollapps.network.response.Response
import javax.inject.Inject

class EventRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getCurrentEvent(name: String): Event? {
        return try {
            apiService.getCurrentEvent(name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun applyForEvent(
        name: String,
        type: String
    ): Event? {
        return try {
            apiService.applyForCurrentEvent(name, type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun login(name: String): Response? {
        return try {
            apiService.login(name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteApplicationForEvent(name: String): Event? {
        return try {
            apiService.deleteApplicationForCurrentEvent(name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
