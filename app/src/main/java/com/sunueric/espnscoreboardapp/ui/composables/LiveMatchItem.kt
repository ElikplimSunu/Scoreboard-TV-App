package com.sunueric.espnscoreboardapp.ui.composables

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.tv.material3.Card
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.sunueric.espnscoreboardapp.R
import com.sunueric.espnscoreboardapp.data.model.LiveOrScheduledMatch
import com.sunueric.espnscoreboardapp.data.model.liveOrScheduledMatches

class LiveMatchItem {
}

@Composable
fun LiveMatchItem(modifier: Modifier, context: Context, liveOrScheduledMatches: LiveOrScheduledMatch, playTime: Int) {
    var progressFloat by remember { mutableFloatStateOf(0.0f) }
    var displayTimeInt by remember { mutableIntStateOf(0) }

    val displayTimeParts = liveOrScheduledMatches.displayTime?.replace("'", "")?.split("+")
    val extraTime = 0
//    (displayTimeParts?.get(1)?.toInt()) ?: 0

//    if (displayTimeParts != null) {
//        displayTimeInt = displayTimeParts.sumOf { it.toInt() }
//    }
//    progressFloat = displayTimeInt / ((playTime + (extraTime)).toFloat())

    var matchStatusColor by remember { mutableStateOf(Color.Gray) }
    matchStatusColor = if(liveOrScheduledMatches.matchStatus == "Scheduled") {
        Color.Yellow
    } else if (liveOrScheduledMatches.isCompleted == true) Color.Red else Color.Green


    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val boxWithConstraintsScope = this
        val width = boxWithConstraintsScope.maxWidth
        val height = boxWithConstraintsScope.maxHeight
        Log.d("LiveMatchItem", "Width: $width, Height: $height")
    Card(
        onClick = {}, modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val (header, teamA, teamB, score, timeProgress, matchStatus) = createRefs()
            Row(modifier = Modifier
                .fillMaxWidth()
                .constrainAs(header) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier
                            .size(22.dp),
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(data = liveOrScheduledMatches.leagueLogoUrl)
                                .apply {
                                    // Placeholder image
                                    placeholder(R.drawable.default_club_logo)
                                    // Error image
                                    error(R.drawable.default_club_logo)
                                }
                                .build()
                        ),
                        contentDescription = "The league logo",
                        contentScale = ContentScale.Crop
                    )
                    liveOrScheduledMatches.leagueName?.let {
                        Text(
                            text = it,
                            style = TextStyle(fontSize = 14.sp)
                        )
                    }
                }

                liveOrScheduledMatches.season?.let {
                    Text(
                        text = it,
                        style = TextStyle(fontSize = 14.sp)
                    )
                }
            }

            //Match status
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.constrainAs(matchStatus) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(header.bottom)
                }) {
                liveOrScheduledMatches.matchStatus?.let {
                    Text(
                        text = it,
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.circle),
                    contentDescription = "Match status",
                    modifier = Modifier.size(12.dp),
                    tint = matchStatusColor
                )
            }

            // Team A
            Column(modifier = Modifier
                .constrainAs(teamA) {
                    top.linkTo(header.bottom)
                    start.linkTo(parent.start)
                    bottom.linkTo(timeProgress.top)
                    end.linkTo(score.start)
                }
                .padding(start = 12.dp),

                horizontalAlignment = Alignment.CenterHorizontally) {

                Image(
                    modifier = Modifier
                        .size(80.dp),
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(data = liveOrScheduledMatches.homeTeamLogoUrl)
                            .apply(block = {
                                // Placeholder image
                                placeholder(R.drawable.default_club_logo)
                                // Error image
                                error(R.drawable.default_club_logo)
                            })
                            .build()
                    ),
                    contentDescription = "Home team logo",
                    contentScale = ContentScale.Crop
                )


                liveOrScheduledMatches.homeTeam?.let {
                    Text(
                        text = it, style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                if(liveOrScheduledMatches.homeTeamRecord != null) {
                    Text(
                        text = "(${liveOrScheduledMatches.homeTeamRecord})",
                        style = TextStyle(fontSize = 18.sp)
                    )
                }

                Text(text = "Home", style = TextStyle(fontSize = 16.sp))

            }

            Column(
                modifier = Modifier.constrainAs(score) {
                    top.linkTo(header.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(timeProgress.top)
                },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                liveOrScheduledMatches.displayTime?.let {
                    Text(
                        text = it,
                        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    )
                }

                Text(
                    modifier = Modifier
                        .wrapContentHeight(),
                    text = "${liveOrScheduledMatches.homeTeamScore} : ${liveOrScheduledMatches.awayTeamScore}",
                    style = TextStyle(
                        fontSize = 86.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
            }

            // Team B
            Column(modifier = Modifier
                .constrainAs(teamB) {
                    top.linkTo(header.bottom)
                    bottom.linkTo(timeProgress.top)
                    end.linkTo(parent.end)
                    start.linkTo(score.end)
                }
                .padding(end = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    modifier = Modifier
                        .size(80.dp),
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(data = liveOrScheduledMatches.awayTeamLogoUrl)
                            .apply(block = {
                                // Placeholder image
                                placeholder(R.drawable.default_club_logo)
                                // Error image
                                error(R.drawable.default_club_logo)
                            })
                            .build()
                    ),
                    contentDescription = "Away team logo",
                    contentScale = ContentScale.Crop
                )


                liveOrScheduledMatches.awayTeam?.let {
                    Text(
                        text = it, style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                if(liveOrScheduledMatches.awayTeamRecord != null) {
                    Text(
                        text = "(${liveOrScheduledMatches.awayTeamRecord})",
                        style = TextStyle(fontSize = 18.sp)
                    )
                }

                Text(text = "Away", style = TextStyle(fontSize = 16.sp))

            }

            LinearProgressIndicator(
                modifier = Modifier
                    .constrainAs(timeProgress) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxWidth()
                    .height(8.dp),
                progress = { progressFloat }
            )
        }
    }
}
}

@Preview(showBackground = true, device = Devices.TV_1080p)
@Composable
fun LiveMatchItemPreview() {
    LiveMatchItem(modifier = Modifier, context = LocalContext.current, liveOrScheduledMatches = liveOrScheduledMatches[0], playTime = 90)
}