package com.sunueric.espnscoreboardapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunueric.espnscoreboardapp.data.model.Event
import com.sunueric.espnscoreboardapp.data.model.Headline
import com.sunueric.espnscoreboardapp.data.model.LiveOrScheduledMatch
import com.sunueric.espnscoreboardapp.data.model.ScoreboardResponse
import com.sunueric.espnscoreboardapp.data.model.UpComingMatch
import com.sunueric.espnscoreboardapp.network.ScoreboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class ScoreboardViewModel : ViewModel() {
    private val repository = ScoreboardRepository()

    private val _scoreboardState = MutableStateFlow<ScoreboardResponse?>(null)
    val scoreboardState: StateFlow<ScoreboardResponse?> = _scoreboardState

    private val _liveOrScheduledMatches = MutableStateFlow<List<LiveOrScheduledMatch?>?>(null)
    val liveOrScheduledMatches: StateFlow<List<LiveOrScheduledMatch?>?> = _liveOrScheduledMatches

    private val _upComingMatches = MutableStateFlow<List<UpComingMatch>?>(null)
    val upComingMatches: StateFlow<List<UpComingMatch>?> = _upComingMatches

    private val _scheduledOrPassedMatches = MutableStateFlow<List<UpComingMatch>?>(null)
    val scheduledOrPassedMatches: StateFlow<List<UpComingMatch>?> = _scheduledOrPassedMatches

    private val _headlines = MutableStateFlow<List<Headline>?>(null)
    val headlines: StateFlow<List<Headline>?> = _headlines

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun fetchScoreboard(sport: String, leagueSlug: String) {
        viewModelScope.launch {
            try {
                val response = repository.getScoreboard(sport, leagueSlug)
                _scoreboardState.value = response
            } catch (e: Exception) {
                // Handle error
                Log.e("ScoreboardViewModel", "$e")
            }
        }
    }

    fun parseLeagueData(scoreboardState: ScoreboardResponse, sport: String) {
        val headlines: MutableList<Headline> = mutableListOf()
        val events = scoreboardState.events
        if(events.isNotEmpty()) {
//            checkMatchTime(event = event)

            parseLiveOrScheduledMatches(scoreboardState, sport)

            parseScheduledOrPassedMatches(events)
    }

        events.forEach { event ->
            event.competitions.forEach { competition ->
                competition.headlines.forEach { headline ->
                    headlines.add(
                        Headline(
                            type = headline.type,
                            shortLinkText = headline.shortLinkText,
                            description = headline.description
                        )
                    )
                }
            }
        }

        _headlines.value = headlines
    }

    private fun parseUpcomingMatches(event: Event) {
        val upcomingMatches: MutableList<UpComingMatch> = mutableListOf()

        event.competitions.forEach { competition ->
            val competitors = competition.competitors
                val (parsedTime, parsedDate) = parseAndFormatDateTime(event.date)
                val match = UpComingMatch(
                    tag = "upcoming",
                    teamAName = competitors[0].team.displayName,
                    teamAImageUrl = competitors[0].team.logo,
                    teamAScore = null,
                    teamBName = competitors[1].team.displayName,
                    teamBImageUrl = competitors[1].team.logo,
                    teamBScore = null,
                    time = parsedTime,
                    date = parsedDate
                )
            upcomingMatches.add(match)
        }

        _upComingMatches.value = upcomingMatches
    }

    private fun parseScheduledOrPassedMatches(events: List<Event>) {
        val scheduledOrPassedMatches: MutableList<UpComingMatch> = mutableListOf()
        val scheduledEvent = events.filter { it.status.type.state == "pre" }
        val passedEvent = events.filter { it.status.type.state == "post" }
        val scheduledOrPassedEvents = scheduledEvent.plus(passedEvent)

        if (scheduledOrPassedEvents.isNotEmpty()) {
            scheduledOrPassedEvents.forEach { event ->
                val competitors = event.competitions.first().competitors
                // TODO: Iterate of over the compititions
                val (parsedTime, parsedDate) = parseAndFormatDateTime(event.date)
                val match = UpComingMatch(
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

            _scheduledOrPassedMatches.value = scheduledOrPassedMatches
        }
    }

    private fun parseLiveOrScheduledMatches(scoreboardState: ScoreboardResponse, sport: String) {
        val liveOrScheduledMatches: MutableList<LiveOrScheduledMatch> = mutableListOf()
        val liveEvents = scoreboardState.events.filter { it.status.type.state == "live" }

        if (liveEvents.isEmpty()) {
            return
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

            _liveOrScheduledMatches.value = liveOrScheduledMatches
        }
    }


    private fun checkMatchTime(events: List<Event>) {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val event = events.first()
        val matchTime = dateFormat.parse(event.date)?.time

        if (matchTime != null) {
            when {
                matchTime < currentTime -> {
                    _message.value = "The match has already passed."
//                    parsePassedMatches(event)
                }
                matchTime == currentTime -> {
                    _message.value = "The match is currently ongoing."
                    parseUpcomingMatches(event)
                }
                else -> {
                    // Match is yet to come, parse upcoming matches
                    parseUpcomingMatches(event)
                }
            }
        }
    }
}

fun parseAndFormatDateTime(utcDateTime: String): Pair<String, String> {
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