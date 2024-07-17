package com.sunueric.espnscoreboardapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunueric.espnscoreboardapp.data.model.CallDetails
import com.sunueric.espnscoreboardapp.data.model.Event
import com.sunueric.espnscoreboardapp.data.model.Headline
import com.sunueric.espnscoreboardapp.data.model.LiveOrScheduledMatch
import com.sunueric.espnscoreboardapp.data.model.ParseDetails
import com.sunueric.espnscoreboardapp.data.model.ScoreboardResponse
import com.sunueric.espnscoreboardapp.data.model.ScheduledOrPassedMatch
import com.sunueric.espnscoreboardapp.network.ScoreboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class ParsedLeague(
    val liveOrScheduledMatch: List<LiveOrScheduledMatch>,
    val scheduledOrPassedMatches: List<ScheduledOrPassedMatch>,
    val headlines: List<Headline>
)

class ScoreboardViewModel : ViewModel() {
    private val repository = ScoreboardRepository()

    private val _scoreboardStates = MutableStateFlow<List<Pair<ScoreboardResponse, String>?>?>(null)
    val scoreboardStates: StateFlow<List<Pair<ScoreboardResponse, String>?>?> = _scoreboardStates

    private val _parsedLeagues = MutableStateFlow<List<ParsedLeague>?>(null)
    val parsedLeagues: StateFlow<List<ParsedLeague>?> = _parsedLeagues

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun fetchScoreboard(callDetails: List<CallDetails>) {
        viewModelScope.launch {
            try {
                val responses: MutableList<Pair<ScoreboardResponse, String>> = mutableListOf()
                callDetails.forEach { callDetail ->
                    val response = repository.getScoreboard(callDetail.sport, callDetail.leagueSlug)
                    responses.add(Pair(response, callDetail.sport))
                }
                _scoreboardStates.value = responses
            } catch (e: Exception) {
                // Handle error
                Log.e("ScoreboardViewModel", "$e")
            }
        }
    }

    fun callParseLeagueData() {
        val parseDetails: MutableList<ParseDetails> = mutableListOf()
        scoreboardStates.value?.forEach { scoreboardState ->
            if (scoreboardState != null) {
                parseDetails.add(ParseDetails(scoreboardState.first, scoreboardState.second))
            }
        }
        parseLeagueData(parseDetails)
    }

    private fun parseLeagueData(scoreboardStates: List<ParseDetails>) {
        val parsedLeagues: MutableList<ParsedLeague> = mutableListOf()

        scoreboardStates.forEach { scoreboardState ->
            val events = scoreboardState.scoreboardState.events
            if (events.isNotEmpty()) {
                val liveOrScheduledMatch = parseLiveOrScheduledMatches(
                    scoreboardState.scoreboardState,
                    scoreboardState.sport
                )

                val scheduledOrPassedMatches = parseScheduledOrPassedMatches(events)

                val headlines = parseHeadlines(events)

                val league = ParsedLeague(
                    liveOrScheduledMatch = liveOrScheduledMatch ?: emptyList(),
                    scheduledOrPassedMatches = scheduledOrPassedMatches ?: emptyList(),
                    headlines = headlines ?: emptyList()
                )

                parsedLeagues.add(league)
            }
        }

        _parsedLeagues.value = parsedLeagues
    }

    private fun parseHeadlines(
        events: List<Event>
    ): List<Headline>? {

        val headlines: MutableList<Headline> = mutableListOf()

        events.forEach { event ->
            event.competitions.forEach { competition ->
                if (competition.headlines?.isNotEmpty() == true) { // This is very important
                    competition.headlines.forEach { headline ->
                        headlines.add(
                            Headline(
                                type = headline.type,
                                shortLinkText = headline.shortLinkText,
                                description = headline.description
                            )
                        )
                    }
                } else {
                    return null
                }
            }
        }

        return headlines
    }

    private fun parseScheduledOrPassedMatches(events: List<Event>): List<ScheduledOrPassedMatch>? {
        val scheduledOrPassedMatches: MutableList<ScheduledOrPassedMatch> = mutableListOf()
        val scheduledEvent = events.filter { it.status.type.state == "pre" }
        val passedEvent = events.filter { it.status.type.state == "post" }
        val scheduledOrPassedEvents = scheduledEvent.plus(passedEvent)

        if (scheduledOrPassedEvents.isNotEmpty()) {
            scheduledOrPassedEvents.forEach { event ->
                val competitors = event.competitions.first().competitors
                // TODO: Iterate of over the compititions
                val (parsedTime, parsedDate) = parseAndFormatDateTime(event.date)
                val match = ScheduledOrPassedMatch(
                    tag = if (event.status.type.state == "pre") "upcoming" else "passed",
                    teamAName = competitors[0].team.displayName,
                    teamAImageUrl = competitors[0].team.logo,
                    teamAScore = competitors[0].score,
                    teamBName = competitors[1].team.displayName,
                    teamBImageUrl = competitors[1].team.logo,
                    teamBScore = competitors[1].score,
                    time = parsedTime,
                    date = parsedDate
                )
                scheduledOrPassedMatches.add(match)
            }

            return scheduledOrPassedMatches
        } else {
            return null
        }
    }

    private fun parseLiveOrScheduledMatches(
        scoreboardState: ScoreboardResponse,
        sport: String
    ): List<LiveOrScheduledMatch>? {
        val liveOrScheduledMatches: MutableList<LiveOrScheduledMatch> = mutableListOf()
        val liveEvents = scoreboardState.events.filter { it.status.type.state == "in" }

        if (liveEvents.isEmpty()) {
            return null
        } else {
            liveEvents.forEach { liveEvent ->
                val competitors = liveEvent.competitions.firstOrNull()?.competitors
                val match = LiveOrScheduledMatch(
                    leagueName = scoreboardState.leagues.first().name,
                    leagueLogoUrl = scoreboardState.leagues.firstOrNull()?.logos?.firstOrNull()?.href,
                    matchStatus = liveEvent.status.type.description,
                    isCompleted = liveEvent.status.type.completed,
                    season = scoreboardState.leagues.firstOrNull()?.season?.displayName,
                    homeTeam = competitors?.first()?.team?.displayName,
                    homeTeamLogoUrl = competitors?.first()?.team?.logo,
                    homeTeamScore = competitors?.first()?.score,
                    homeTeamRecord = if (sport == "baseball") competitors?.first()?.records?.firstOrNull()?.summary else null,
                    awayTeam = competitors?.get(1)?.team?.displayName,
                    awayTeamLogoUrl = competitors?.get(1)?.team?.logo,
                    awayTeamScore = competitors?.get(1)?.score,
                    awayTeamRecord = if (sport == "baseball") competitors?.get(1)?.records?.firstOrNull()?.summary else null,
                    displayTime = liveEvent.status.displayClock
                )
                liveOrScheduledMatches.add(match)
            }

            return liveOrScheduledMatches
        }
    }

    private fun parseAndFormatDateTime(utcDateTime: String): Pair<String, String> {
        // Define the input and output formatters
        val inputFormatter = DateTimeFormatter.ISO_DATE_TIME
        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

        // Parse the UTC date-time string
        val utcZonedDateTime = ZonedDateTime.parse(utcDateTime, inputFormatter)

        // Convert to the system's time zone
        val systemZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.systemDefault())

        // Format the date and time separately
        val formattedDate = systemZonedDateTime.format(dateFormatter)
        val formattedTime = systemZonedDateTime.format(timeFormatter)

        return Pair(formattedDate, formattedTime)
    }
}