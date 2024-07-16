package com.sunueric.espnscoreboardapp.data.model

data class UpComingMatch(
    val tag: String,
    val teamAName: String,
    val teamAImageUrl: String,
    val teamAScore: String?,
    val teamBName: String,
    val teamBImageUrl: String,
    val teamBScore: String?,
    val time: String,
    val date: String
)

data class Headline(
    val type: String,
    val description: String,
    val shortLinkText: String
)

data class LiveOrScheduledMatch(
    val leagueName: String?,
    val leagueLogoUrl: String?,
    val matchStatus: String?,
    val isCompleted: Boolean?,
    val season: String?,
    val homeTeam: String?,
    val homeTeamLogoUrl: String?,
    val homeTeamScore: String?,
    val homeTeamRecord: String?,
    val awayTeam: String?,
    val awayTeamLogoUrl: String?,
    val awayTeamScore: String?,
    val awayTeamRecord: String?,
    val displayTime: String?
)

val liveOrScheduledMatches = listOf(
    LiveOrScheduledMatch(
        leagueName = "NBA",
        leagueLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/nba.png",
        matchStatus = "Live",
        season = "2023-2024",
        homeTeam = "Boston Celtics",
        homeTeamLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/bos.png",
        homeTeamScore = "102",
        homeTeamRecord = "89-90",
        awayTeam = "Dallas Mavericks",
        awayTeamLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/dal.png",
        awayTeamScore = "98",
        awayTeamRecord = "34-55",
        displayTime = "60'",
        isCompleted = false
    ),
    LiveOrScheduledMatch(
        leagueName = "NBA",
        leagueLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/nba.png",
        matchStatus = "Live",
        season = "2023-2024",
        homeTeam = "Los Angeles Lakers",
        homeTeamLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/lal.png",
        homeTeamScore = "75",
        homeTeamRecord = "89-90",
        awayTeam = "Brooklyn Nets",
        awayTeamLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/bkn.png",
        awayTeamScore = "80",
        awayTeamRecord = "34-55",
        displayTime = "45'",
        isCompleted = false
    ),
    LiveOrScheduledMatch(
        leagueName = "NBA",
        leagueLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/nba.png",
        matchStatus = "Live",
        season = "2023-2024",
        homeTeam = "Golden State Warriors",
        homeTeamLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/gs.png",
        homeTeamScore = "20",
        homeTeamRecord = "89-90",
        awayTeam = "Miami Heat",
        awayTeamLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/mia.png",
        awayTeamScore = "36",
        awayTeamRecord = "34-55",
        displayTime = "32'",
        isCompleted = false
    ),
    LiveOrScheduledMatch(
        leagueName = "NBA",
        leagueLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/nba.png",
        matchStatus = "Live",
        season = "2023-2024",
        homeTeam = "Chicago Bulls",
        homeTeamLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/chi.png",
        homeTeamScore = "98",
        homeTeamRecord = "89-90",
        awayTeam = "Houston Rockets",
        awayTeamLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/hou.png",
        awayTeamScore = "89",
        awayTeamRecord = "34-55",
        displayTime = "20'",
        isCompleted = false
    ),
    LiveOrScheduledMatch(
        leagueName = "NBA",
        leagueLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/nba.png",
        matchStatus = "Live",
        season = "2023-2024",
        homeTeam = "Milwaukee Bucks",
        homeTeamLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/mil.png",
        homeTeamScore = "85",
        homeTeamRecord = "89-90",
        awayTeam = "Philadelphia 76ers",
        awayTeamLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/phi.png",
        awayTeamScore = "88",
        awayTeamRecord = "34-55",
        displayTime = "60'",
        isCompleted = false
    ),
    LiveOrScheduledMatch(
        leagueName = "NBA",
        leagueLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/nba.png",
        matchStatus = "Live",
        season = "2023-2024",
        homeTeam = "Toronto Raptors",
        homeTeamLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/tor.png",
        homeTeamScore = "0",
        homeTeamRecord = "89-90",
        awayTeam = "Denver Nuggets",
        awayTeamLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/den.png",
        awayTeamScore = "0",
        awayTeamRecord = "34-55",
        displayTime = "50'",
        isCompleted = false
    ),
    LiveOrScheduledMatch(
        leagueName = "NBA",
        leagueLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/nba.png",
        matchStatus = "Live",
        season = "2023-2024",
        homeTeam = "Phoenix Suns",
        homeTeamLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/phx.png",
        homeTeamScore = "105",
        homeTeamRecord = "89-90",
        awayTeam = "Utah Jazz",
        awayTeamLogoUrl = "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/uta.png",
        awayTeamScore = "101",
        awayTeamRecord = "34-55",
        displayTime = "31'",
        isCompleted = false
    )
    // Add more details as needed
)

val upComingMatches = listOf(
    UpComingMatch(
        "upcoming",

        "Los Angeles Lakers",
        "2",
        "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/lal.png",
        "Brooklyn Nets",
        "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/bkn.png",
        "3",
        "8:00 PM",
        "Jan 5"
    ),
    UpComingMatch(
        "upcoming",
        "Golden State Warriors",
        "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/gs.png",
        "4",
        "Miami Heat",
        "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/mia.png",
        "3",
        "7:30 PM",
        "Jan 10"
    ),
    UpComingMatch(
        "upcoming",
        "Chicago Bulls",
        "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/chi.png",
        "8",
        "Houston Rockets",
        "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/hou.png",
        "89",
        "5:00 PM",
        "Jan 15"
    ),
    UpComingMatch(
        "upcoming",

        "Milwaukee Bucks",
        "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/mil.png",
        "56",
        "Philadelphia 76ers",
        "https://a.espncdn.com/i/teamlogos/nba/500/scoreboard/phi.png",
        "78",
        "7:00 PM",
        "Jan 20"
    )
    // Add more matches as needed
)

val headlines = listOf(
    Headline(
        type = "Recap",
        description = "— Jayson Tatum put his hands behind his head, with TD Garden fans standing on their feet cheering around him, and took it all in.",
        shortLinkText = "Celtics win 18th NBA championship with 106-88 Game 5 victory over Dallas Mavericks",
    ),
    Headline(
        type = "Highlight",
        description = "— Luka Doncic delivers a masterclass performance in a thrilling win against the Lakers.",
        shortLinkText = "Doncic leads Mavericks to stunning victory over Lakers",
    ),
    Headline(
        type = "News",
        description = "— LeBron James announces his retirement from the NBA after 20 seasons.",
        shortLinkText = "LeBron James retires from the NBA",
    )
)

data class ScoreboardResponse(
    val leagues: List<League>,
    val events: List<Event>
)

data class League(
    val name: String,
    val season: Season,
    val logos: List<Logo>
    )

    data class Season(
        val year: String,
        val startDate: String,
        val endDate: String,
        val displayName: String
    )

    data class Logo(
        val href: String
    )

data class Event(
    val id: String,
    val name: String,
    val date: String,
    val status: Status,
    val competitions: List<Competition>
)

data class Status(
    val clock: Float,
    val displayClock: String,
    val type: Type
)

data class Type(
    val name: String,
    val state: String,
    val description: String,
    val completed: Boolean
)

data class Competition(
    val startDate: String,
    val status: Status,
    val venue: Venue,
    val format: Format,
    val headlines: List<ServerHeadline>,
    val competitors: List<Competitor>
)
    data class Venue(
        val fullName: String
    )

    data class Format(
        val regulation: Regulation
    )
        data class Regulation(
            val periods: Int
        )

    data class ServerHeadline(
        val description: String,
        val type: String,
        val shortLinkText: String
    )

data class Competitor(
    val id: String,
    val team: Team,
    val type: String,
    val homeAway: String,
    val winner: Boolean,
    val score: String,
    val records: List<Record>
)

data class Team(
    val displayName: String,
    val color: String,
    val alternateColor: String,
    val isActive: Boolean,
    val logo: String
)

data class Record(
    val name: String,
    val summary: String
)
