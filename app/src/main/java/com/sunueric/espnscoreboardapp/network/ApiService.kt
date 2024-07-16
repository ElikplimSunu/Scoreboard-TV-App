package com.sunueric.espnscoreboardapp.network

import com.sunueric.espnscoreboardapp.data.model.ScoreboardResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("sports/{sport}/{leagueSlug}/scoreboard")
    suspend fun getScoreboard(
        @Path("sport") sport: String,
        @Path("leagueSlug") leagueSlug: String,
//        @Query("dates") date: String,
    ): ScoreboardResponse
}
