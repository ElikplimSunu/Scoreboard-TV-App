package com.sunueric.espnscoreboardapp.network

import com.sunueric.espnscoreboardapp.data.model.ScoreboardResponse

class ScoreboardRepository {
    suspend fun getScoreboard(sport: String, leagueSlug: String): ScoreboardResponse {
        return RetrofitInstance.api.getScoreboard(sport, leagueSlug, "20240522")
    }
}
