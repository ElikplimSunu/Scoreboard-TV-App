package com.sunueric.espnscoreboardapp.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.sunueric.espnscoreboardapp.R
import com.sunueric.espnscoreboardapp.data.model.CallDetails
import com.sunueric.espnscoreboardapp.data.model.Headline
import com.sunueric.espnscoreboardapp.data.model.LiveOrScheduledMatch
import com.sunueric.espnscoreboardapp.data.model.ScheduledOrPassedMatch
import com.sunueric.espnscoreboardapp.data.model.headlinesTestData
import com.sunueric.espnscoreboardapp.data.model.liveOrScheduledMatchesTestData
import com.sunueric.espnscoreboardapp.data.model.scheduledOrPassedMatchesTestData
import com.sunueric.espnscoreboardapp.ui.composables.LiveMatchItem
import com.sunueric.espnscoreboardapp.ui.theme.Purple40
import com.sunueric.espnscoreboardapp.ui.theme.PurpleGrey80
import com.sunueric.espnscoreboardapp.ui.viewmodels.ScoreboardViewModel
import kotlinx.coroutines.delay

@Composable
fun ScoreboardScreen(espnViewModel: ScoreboardViewModel) {
    val scoreboardStates by espnViewModel.scoreboardStates.collectAsState()
    val parsedLeagues by espnViewModel.parsedLeagues.collectAsState()
    val itemWidth = LocalConfiguration.current.screenWidthDp.dp

    val callDetails = listOf(
        CallDetails(
            "soccer",
            "uefa.champions_qual"
        ),
        CallDetails(
            "soccer",
            "fifa.friendly.w"
        ),
        CallDetails(
            "soccer",
            "uefa.euro.u19"
        ),
        CallDetails(
            "soccer",
            "conmebol.sudamericana"
        ),
    )

    val listState = rememberLazyListState()
    val visibleItems = 1

    LaunchedEffect(Unit) {
        espnViewModel.fetchScoreboard(callDetails)

    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            espnViewModel.fetchScoreboard(callDetails)
            espnViewModel.callParseLeagueData()
        }
    }

    LaunchedEffect(listState) {
        while (true) {
            delay(15000) // Delay between scrolls
            val nextIndex =
                if (listState.firstVisibleItemIndex + visibleItems >= (scoreboardStates?.size
                        ?: 0)
                ) {
                    0 // Reset to the beginning if end is reached
                } else {
                    listState.firstVisibleItemIndex + visibleItems
                }
            listState.animateScrollToItem(nextIndex, scrollOffset = 0)
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center
    ) {
        if (!parsedLeagues.isNullOrEmpty()) {
            itemsIndexed(parsedLeagues ?: emptyList()) { index, league ->
                ScoreboardItem(
                    liveOrScheduledMatches = league.liveOrScheduledMatch,
                    scheduledOrPassedMatches = league.scheduledOrPassedMatches,
                    headlines = league.headlines,
                    sportType = callDetails[index].sport,
                    itemWidth = itemWidth
                )
            }
        } else {
            item {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun ScoreboardItem(
    liveOrScheduledMatches: List<LiveOrScheduledMatch?>?,
    scheduledOrPassedMatches: List<ScheduledOrPassedMatch>?,
    headlines: List<Headline>?,
    sportType: String,
    itemWidth: Dp
) {

    BoxWithConstraints(modifier = Modifier.width(itemWidth)) {
        val boxWithConstraintsScope = this
        val width = boxWithConstraintsScope.maxWidth
        val height = boxWithConstraintsScope.maxHeight
        Log.d("ScoreboardItem", "Width: $width, Height: $height")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .width(itemWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val context = LocalContext.current

            if (!liveOrScheduledMatches.isNullOrEmpty()) {
                ScoreboardCard(
                    context = context,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    matches = liveOrScheduledMatches,
                    sportType = sportType,
                    parentHeight = height,
                    //parentWidth = width
                )
            }

            if (!scheduledOrPassedMatches.isNullOrEmpty()) {
                ScheduledOrPassedMatchesCard(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    scheduledOrPassedMatches = scheduledOrPassedMatches,
                    parentHeight = height,
                    parentWidth = width
                )
            }

            if (!headlines.isNullOrEmpty()) {
                HeadlineCard(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    headlines = headlines,
                    parentHeight = height
                )
            }
        }
    }
}

@Composable
fun ScoreboardCard(
    context: Context,
    modifier: Modifier,
    matches: List<LiveOrScheduledMatch?>?,
    sportType: String,
    parentHeight: Dp
) {
    var cardHeight by remember { mutableIntStateOf(0) }
    val itemHeight = with(LocalDensity.current) { (cardHeight).toDp() } // For 2 items at a time
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

        Card(
            onClick = {},
            border = CardDefaults.border(
                border = Border(BorderStroke(1.dp, PurpleGrey80)),
            ),
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val boxWithConstraintsScope = this
                val childWidth = boxWithConstraintsScope.maxWidth
                val childHeight = boxWithConstraintsScope.maxHeight
                Log.d("ScoreboardCard", "Width: $childWidth, Height: $childHeight")

                val scalingFactor = when {
                    childHeight <= parentHeight / 3 -> {
                        1f // Scaling factor for 1 out of 3 items
                    }

                    childHeight <= parentHeight / 2 -> {
                        1.3f
                    }

                    childHeight <= parentHeight -> {
                        1.4f
                    }

                    else -> {
                        1f
                    }
                }
                //TODO: Add wrapping for the team names

                LazyColumn(
                    state = listState,
                    modifier = modifier
                        .fillMaxSize()
                        .onGloballyPositioned { coordinates ->
                            cardHeight = coordinates.size.height
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    items(matches) { match ->
                        Box(modifier = Modifier.fillParentMaxWidth()) { // Ensure each item fills the full width
                            Log.d("ScoreboardCard", "match: $match")
                            if (match != null) {
                                LiveMatchItem(
                                    Modifier.fillMaxWidth(),
                                    context,
                                    match,
                                    playTime,
                                    itemHeight,
                                    scalingFactor
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun ScheduledOrPassedMatchesCard(
    modifier: Modifier,
    scheduledOrPassedMatches: List<ScheduledOrPassedMatch>,
    parentHeight: Dp,
    parentWidth: Dp
) {
    var cardHeight by remember { mutableIntStateOf(0) }
    var visibleItems by remember { mutableIntStateOf(1) }
    val itemHeight =
        with(LocalDensity.current) { (cardHeight / visibleItems).toDp() } // For 2 items at a time
    val listState = rememberLazyListState()
    var cardHeader by remember { mutableStateOf("Upcoming Matches") }

    LaunchedEffect(key1 = listState, key2 = visibleItems) {
        while (true) {
            delay(5000) // Delay between scrolls
            val nextIndex =
                if (listState.firstVisibleItemIndex + visibleItems >= scheduledOrPassedMatches.size) {
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
        border = CardDefaults.border(
            border = Border(BorderStroke(1.dp, PurpleGrey80)),
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val boxWithConstraintsScope = this
            val childWidth = boxWithConstraintsScope.maxWidth
            val childHeight = boxWithConstraintsScope.maxHeight
            Log.d("ScheduledOrPassedMatches", "Width: $childWidth, Height: $childHeight")


            visibleItems = when {
                childHeight <= parentHeight / 3 -> {
                    Log.d(
                        "ScheduledOrPassedMatches",
                        "Height in range: The card is 1 out of 3 items on screen. Height: $childHeight"
                    )
                    1
                }

                childHeight <= parentHeight / 2 -> {
                    Log.d(
                        "ScheduledOrPassedMatches",
                        "Height in range: The card is 1 out of 2 items on screen. Height: $childHeight"
                    )
                    2
                }

                childHeight <= parentHeight -> {
                    Log.d(
                        "ScheduledOrPassedMatches",
                        "Height in range: The card is 1 out of 1 items on screen. Height: $childHeight"
                    )
                    4
                }

                else -> {
                    Log.d(
                        "ScheduledOrPassedMatches",
                        "Height out of range: The card is 1 out of 3 items on screen. Height: $childHeight"
                    )
                    1
                }
            }

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
                        .onGloballyPositioned { coordinates ->
                            cardHeight = coordinates.size.height
                        }
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    items(scheduledOrPassedMatches) { upComingMatch ->
                        cardHeader = if (upComingMatch.tag == "upcoming")
                            "Upcoming Matches"
                        else
                            "Passed Matches"
                        ScheduledOrPassedMatchItem(
                            scheduledOrPassedMatch = upComingMatch,
                            itemHeight = itemHeight,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduledOrPassedMatchItem(
    scheduledOrPassedMatch: ScheduledOrPassedMatch,
    itemHeight: Dp,
    modifier: Modifier
) {

    Card(
        onClick = {},
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.onSecondary
        ),
        modifier = Modifier
            .height(itemHeight)
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 6.dp)
    ) {
        ConstraintLayout(
            modifier = modifier
                .fillMaxSize() // Set the height for each item
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
                    text = scheduledOrPassedMatch.teamAName,
                    style = TextStyle(fontSize = 26.sp),
                    fontWeight = FontWeight.Bold
                )

                Image(
                    modifier = Modifier
                        .size(62.dp),
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = scheduledOrPassedMatch.teamAImageUrl)
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
                    .padding(start = 4.dp, end = 4.dp)
            ) {
                Text(text = scheduledOrPassedMatch.time, style = TextStyle(fontSize = 16.sp))

                Text(
                    text = scheduledOrPassedMatch.date,
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
                text = if (scheduledOrPassedMatch.tag == "passed") "${scheduledOrPassedMatch.teamAScore} : ${scheduledOrPassedMatch.teamBScore}" else "0 : 0",
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
                            .data(data = scheduledOrPassedMatch.teamBImageUrl)
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
                    text = scheduledOrPassedMatch.teamBName,
                    style = TextStyle(fontSize = 26.sp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HeadlineCard(
    modifier: Modifier,
    headlines: List<Headline>,
    parentHeight: Dp
    //parentWidth: Dp
) {
    val listState = rememberLazyListState()
    var cardHeight by remember { mutableIntStateOf(0) }
    var visibleItems by remember { mutableIntStateOf(1) }
    val itemHeight = with(LocalDensity.current) { (cardHeight / visibleItems).toDp() }

    LaunchedEffect(key1 = listState, key2 = visibleItems) {
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
        border = CardDefaults.border(
            border = Border(BorderStroke(1.dp, PurpleGrey80)),
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val boxWithConstraintsScope = this
            val childWidth = boxWithConstraintsScope.maxWidth
            val childHeight = boxWithConstraintsScope.maxHeight
            Log.d("HeadlineCard", "Width: $childWidth, Height: $childHeight")

            visibleItems = when {
                childHeight <= parentHeight / 3 -> {
                    Log.d(
                        "HeadlineCard",
                        "Height in range: The card is 1 out of 3 items on screen. Height: $childHeight"
                    )
                    1
                }

                childHeight <= parentHeight / 2 -> {
                    Log.d(
                        "HeadlineCard",
                        "Height in range: The card is 1 out of 2 items on screen. Height: $childHeight"
                    )
                    2
                }

                childHeight <= parentHeight -> {
                    Log.d(
                        "HeadlineCard",
                        "Height in range: The card is 1 out of 1 items on screen. Height: $childHeight"
                    )
                    3
                }

                else -> {
                    Log.d(
                        "HeadlineCard",
                        "Height out of range: The card is 1 out of 3 items on screen. Height: $childHeight"
                    )
                    1
                }
            }

            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (header, headlineGrid) = createRefs()
                Text(
                    modifier = Modifier
                        .constrainAs(header) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .padding(bottom = 10.dp),
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
                        .fillMaxSize()
                        .onGloballyPositioned { coordinates ->
                            cardHeight = coordinates.size.height
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(headlines) { headline ->
                        HeadlineItem(
                            headline = headline,
                            itemHeight = itemHeight,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeadlineItem(headline: Headline, itemHeight: Dp) {
    Card(
        onClick = {},
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.onSecondary
        ),
        modifier = Modifier
            .height(itemHeight)
            .padding(6.dp)
            .fillMaxWidth()
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize()
        ) {
            val (type, description, shortLinkText) = createRefs()

            Text(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .constrainAs(type) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    },
                text = headline.type,
                style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold)
            )

            Text(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .constrainAs(description) {
                        top.linkTo(type.bottom, margin = 4.dp)
                        start.linkTo(parent.start)
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
                text = headline.shortLinkText,
                style = TextStyle(fontSize = 16.sp, color = Color.Gray)
            )
        }
    }
}

@Composable
@Preview(showBackground = true, device = Devices.TV_1080p)
fun UpcomingMatchItemPreview() {
    ScheduledOrPassedMatchItem(
        scheduledOrPassedMatch = ScheduledOrPassedMatch(
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

