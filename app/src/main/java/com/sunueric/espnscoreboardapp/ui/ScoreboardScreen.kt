package com.sunueric.espnscoreboardapp.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.tv.material3.Card
import androidx.tv.material3.Text
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.sunueric.espnscoreboardapp.R
import com.sunueric.espnscoreboardapp.data.model.Headline
import com.sunueric.espnscoreboardapp.data.model.LiveOrScheduledMatch
import com.sunueric.espnscoreboardapp.data.model.UpComingMatch
import com.sunueric.espnscoreboardapp.ui.composables.LiveMatchItem
import com.sunueric.espnscoreboardapp.ui.viewmodels.ScoreboardViewModel
import kotlinx.coroutines.delay

@Composable
fun ScoreboardScreen(espnViewModel: ScoreboardViewModel) {
    val scoreboardState by espnViewModel.scoreboardState.collectAsState()
    val liveOrScheduledMatches by espnViewModel.liveOrScheduledMatches.collectAsState()
    val scheduledOrPassedMatches by espnViewModel.scheduledOrPassedMatches.collectAsState()
    val headlines by espnViewModel.headlines.collectAsState()


    espnViewModel.fetchScoreboard("soccer", "uefa.champions_qual")
    Log.d("ScoreboardScreen", "Scoreboard state: $scoreboardState")

    LaunchedEffect(scoreboardState) {
        while (true) {
            delay(1000)
            espnViewModel.fetchScoreboard("soccer", "uefa.champions_qual")
            scoreboardState?.let { espnViewModel.parseLeagueData(it, "soccer") }
        }
    }

    if (scoreboardState == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val context = LocalContext.current

            // Scoreboard
            if (!liveOrScheduledMatches.isNullOrEmpty()) {
                ScoreboardCard(
                    context = context,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    liveOrScheduledMatches,
                    "baseball"
                )
            }

            // Scheduled matches
            if (!scheduledOrPassedMatches.isNullOrEmpty()) {
                scheduledOrPassedMatches?.let {
                    ScheduledOrPassedMatches(
                        modifier = Modifier.weight(
                            1f
                        ), it
                    )
                }
            }

            // Headlines
            if (!headlines.isNullOrEmpty()) {
                headlines?.let {
                    AutoScrollingHeadlineCard(
                        modifier = Modifier.weight(1f),
                        headlines = it
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreboardCard(
    context: Context,
    modifier: Modifier,
    matches: List<LiveOrScheduledMatch?>?,
    sportType: String
) {
    val listState = rememberLazyListState()
    val visibleItems = 1
    var playTime by remember { mutableIntStateOf(0) }

    Log.d("ScoreboardCard", "matches: $matches")

    if (matches != null) {
        playTime = when (sportType) {
            "soccer" -> 90
            else -> 120
        }



        LaunchedEffect(key1 = listState) {
            while (true) {
                delay(10000) // Delay between scrolls
                val nextIndex =
                    if (listState.firstVisibleItemIndex + visibleItems >= matches.size) {
                        0 // Reset to the beginning if end is reached
                    } else {
                        listState.firstVisibleItemIndex + visibleItems
                    }
                listState.animateScrollToItem(
                    nextIndex,
                    scrollOffset = 0
                ) // Smooth scroll with animation
            }
        }

        LazyRow(
            state = listState,
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            items(matches) { match ->
                Box(modifier = Modifier.fillParentMaxWidth()) { // Ensure each item fills the full width
                    Log.d("ScoreboardCard", "match: $match")
                    if (match != null) {
                        LiveMatchItem(Modifier.fillMaxWidth(), context, match, playTime)
                    }
                }

            }
        }
    }
}

@Composable
fun ScheduledOrPassedMatches(modifier: Modifier, upComingMatches: List<UpComingMatch>) {
    var cardHeight by remember { mutableIntStateOf(0) }
    val itemHeight = with(LocalDensity.current) { (cardHeight).toDp() } // For 2 items at a time
    val listState = rememberLazyListState()
    val visibleItems = 1
    var cardHeader by remember { mutableStateOf("Upcoming Matches") }

    LaunchedEffect(key1 = listState) {
        while (true) {
            delay(5000) // Delay between scrolls
            val nextIndex =
                if (listState.firstVisibleItemIndex + visibleItems >= upComingMatches.size) {
                    0 // Reset to the beginning if end is reached
                } else {
                    listState.firstVisibleItemIndex + visibleItems
                }
            listState.animateScrollToItem(
                nextIndex,
                scrollOffset = 0
            ) // Smooth scroll with animation
        }
    }

    Card(
        onClick = {},
        modifier = modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (header, matchesGrid) = createRefs()
            Text(
                modifier = Modifier
                    .constrainAs(header) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(top = 4.dp),
                text = cardHeader,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .constrainAs(matchesGrid) {
                        top.linkTo(header.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(top = 8.dp)
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        cardHeight = coordinates.size.height
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                items(upComingMatches) { upComingMatch ->
                    cardHeader = if (upComingMatch.tag == "upcoming")
                        "Upcoming Matches"
                    else
                        "Passed Matches"
                    UpcomingMatchItem(
                        upComingMatch = upComingMatch,
                        itemHeight = itemHeight,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.TV_1080p
)
@Composable
fun ScoreboardScreenPreview() {
    ScoreboardScreen(espnViewModel = ScoreboardViewModel())
}


@Composable
fun UpcomingMatchItem(upComingMatch: UpComingMatch, itemHeight: Dp, modifier: Modifier) {
    ConstraintLayout(
        modifier = modifier
            .height(itemHeight) // Set the height for each item
    ) {
        val (teamA, teamB, dateTime, scores) = createRefs()

        // Team A
        Row(modifier = Modifier
            .constrainAs(teamA) {
                top.linkTo(parent.top)
                end.linkTo(scores.start)
                bottom.linkTo(parent.bottom)
            }
            .padding(end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 6.dp),
                text = upComingMatch.teamAName, style = TextStyle(fontSize = 26.sp)
            )

            Image(
                modifier = Modifier
                    .size(62.dp),
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = upComingMatch.teamAImageUrl)
                        .apply {
                            placeholder(R.drawable.default_club_logo) // Replace with your placeholder image
                            error(R.drawable.default_club_logo) // Replace with your error image
                        }
                        .build()
                ),
                contentDescription = "Team A logo",
                contentScale = ContentScale.Crop
            )
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .constrainAs(dateTime) {
                    top.linkTo(parent.top)
                    bottom.linkTo(scores.top)
                    start.linkTo(scores.start)
                    end.linkTo(scores.end)
                }
                .padding(start = 4.dp, end = 4.dp, )
        ) {
            Text(text = upComingMatch.time, style = TextStyle(fontSize = 16.sp))

            Text(
                text = upComingMatch.date,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
            )
        }


        Text(
            modifier = Modifier.constrainAs(scores) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            text = if (upComingMatch.tag == "passed") "${upComingMatch.teamAScore} : ${upComingMatch.teamBScore}" else "0 : 0",
            style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
        )


        // Team B
        Row(modifier = Modifier
            .constrainAs(teamB) {
                start.linkTo(scores.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            .padding(start = 6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier
                    .size(62.dp),
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = upComingMatch.teamBImageUrl)
                        .apply {
                            placeholder(R.drawable.default_club_logo) // Replace with your placeholder image
                            error(R.drawable.default_club_logo) // Replace with your error image
                        }
                        .build()
                ),
                contentDescription = "Team B logo",
                contentScale = ContentScale.Crop
            )

            Text(
                modifier = Modifier.padding(horizontal = 6.dp),
                text = upComingMatch.teamBName, style = TextStyle(fontSize = 26.sp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true, device = Devices.TV_1080p)
fun UpcomingMatchItemPreview() {
    UpcomingMatchItem(
        upComingMatch = UpComingMatch(
            tag = "upcoming",
            teamAName = "Manchester United",
            teamAImageUrl = "https://a.espncdn.com/i/teamlogos/soccer/500/360.png",
            teamAScore = "",
            teamBName = "Chelsea",
            teamBImageUrl = "https://a.espncdn.com/i/teamlogos/soccer/500/363.png",
            teamBScore = "",
            time = "19:00",
            date = "2023-04-23"
        ),
        itemHeight = 200.dp,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun AutoScrollingHeadlineCard(modifier: Modifier, headlines: List<Headline>) {
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = listState) {
        while (true) {
            delay(5000) // Delay between scrolls
            val nextIndex = if (listState.firstVisibleItemIndex + 1 >= headlines.size) {
                0 // Reset to the beginning if end is reached
            } else {
                listState.firstVisibleItemIndex + 1
            }
            listState.animateScrollToItem(
                nextIndex,
                scrollOffset = 0
            ) // Smooth scroll with animation
        }
    }

    Card(
        onClick = {},
        modifier = modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (header, headlineGrid) = createRefs()
            Text(
                modifier = Modifier
                    .constrainAs(header) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(top = 4.dp),
                text = "Headlines",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .constrainAs(headlineGrid) {
                        top.linkTo(header.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(headlines) { headline ->
                    HeadlineItem(headline = headline)
                }
            }
        }
    }
}

@Composable
fun HeadlineItem(headline: Headline) {
    ConstraintLayout(
        modifier = Modifier
            .padding(8.dp)
    ) {
        val (type, description, shortLinkText) = createRefs()

        Text(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .constrainAs(type) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
            text = headline.type, style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold)
        )

        Text(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .constrainAs(description) {
                    top.linkTo(type.bottom, margin = 4.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            text = headline.description, style = TextStyle(fontSize = 18.sp)
        )

        Text(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .constrainAs(shortLinkText) {
                    top.linkTo(description.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            text = headline.shortLinkText, style = TextStyle(fontSize = 16.sp, color = Color.Gray)
        )
    }
}

