package hu.zsof.tollapps.network

import hu.zsof.tollapps.network.model.Event
import hu.zsof.tollapps.network.response.Response
import retrofit2.http.*

interface ApiService {

    @GET("current")
    suspend fun getCurrentEvent(@Query("name") name: String): Event

    @POST("login")
    suspend fun login(
        @Query("name") name: String,
    ): Response

    @POST("apply")
    suspend fun applyForCurrentEvent(
        @Query("name") name: String,
        @Query("type") type: String,
    ): Event

    @DELETE(".")
    suspend fun deleteApplicationForCurrentEvent(@Query("name") name: String): Event
}
