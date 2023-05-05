package com.github.freeman.bootcamp.games.guessit

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.freeman.bootcamp.R
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.BEST_SCORE
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.IS_NOT_PERCENTAGE
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.IS_PERCENTAGE
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.NB_CORRECT_GUESSES
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.NB_GAMES_PLAYED
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.PCT_MAKE_GUESS
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.PCT_QUICKEST_GUESSER
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.PCT_RIGHT_GUESSSER
import com.github.freeman.bootcamp.games.guessit.StatsActivity.Companion.STATS_TITLE
import com.github.freeman.bootcamp.utilities.firebase.FirebaseUtilities
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class StatsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = Firebase.auth.currentUser?.uid
        val dbUserStatsRef = Firebase.database.reference
            .child(getString(R.string.profiles_path))
            .child(userId.toString())
            .child(getString(R.string.stats_path))

        setContent {
            TopAppbarStats()
            StatsScreen(dbUserStatsRef)
        }
    }

    companion object {
        const val STATS_TITLE = "Games Statistics"

        const val NB_GAMES_PLAYED = "Number of games played"
        const val BEST_SCORE = "Best Score"
        const val NB_CORRECT_GUESSES = "As a guesser, number of correct guesses"
        const val PCT_QUICKEST_GUESSER = "As a guesser, percentage of round you were the quickest to guess"
        const val PCT_RIGHT_GUESSSER = "As a guesser, percentage of round you guesses right"
        const val PCT_MAKE_GUESS = "As an artist, percentage of round somebody guessed"

        const val IS_PERCENTAGE = "%"
        const val IS_NOT_PERCENTAGE = ""
    }
}

@Composable
fun StatsScreen(dbUserStatsRef: DatabaseReference) {
    val context = LocalContext.current

    val nbGamesPlayed = remember { mutableStateOf(0) }
    val bestScore = remember { mutableStateOf(0) }
    val nbCorrectGuesses = remember { mutableStateOf(0) }
    val percentageFirstGuesser = remember { mutableStateOf(0.0) }
    val percentageGuesser = remember { mutableStateOf(0.0) }
    val percentageSomebodyGuessed = remember { mutableStateOf(0.0) }

    retrieveAllStats(
        nbGamesPlayed,
        bestScore,
        nbCorrectGuesses,
        percentageFirstGuesser,
        percentageGuesser,
        percentageSomebodyGuessed,
        dbUserStatsRef,
        context)

    val stats = Stats(
        nbGamesPlayed.value,
        bestScore.value,
        nbCorrectGuesses.value,
        percentageFirstGuesser.value,
        percentageGuesser.value,
        percentageSomebodyGuessed.value
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .testTag("statsScreen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(12.dp))
        NumericStatistic(testTag = "nbGames", NB_GAMES_PLAYED, stats.nb_games_played.toDouble(), IS_NOT_PERCENTAGE)
        Spacer(modifier = Modifier.size(12.dp))
        NumericStatistic(testTag = "bestScore", BEST_SCORE, stats.best_score.toDouble(), IS_NOT_PERCENTAGE)
        Spacer(modifier = Modifier.size(12.dp))
        NumericStatistic(testTag = "nbCorrectGs", NB_CORRECT_GUESSES, stats.nb_correct_guesses.toDouble(), IS_NOT_PERCENTAGE)
        Spacer(modifier = Modifier.size(12.dp))
        NumericStatistic(testTag = "pctFirstGuesser", PCT_QUICKEST_GUESSER, stats.percentage_first_guesser, IS_PERCENTAGE)
        Spacer(modifier = Modifier.size(12.dp))
        NumericStatistic(testTag = "pctRightGuesser", PCT_RIGHT_GUESSSER, stats.percentage_guesser, IS_PERCENTAGE)
        Spacer(modifier = Modifier.size(12.dp))
        NumericStatistic(testTag = "pctSomebodyGuessed", PCT_MAKE_GUESS, stats.percentage_somebody_guessed, IS_PERCENTAGE)
    }
}

/**
 * Retrieve all user stats from the Firebase database
 */
fun retrieveAllStats(
    nbGamesPlayed: MutableState<Int>,
    bestScore: MutableState<Int>,
    nbCorrectGuesses: MutableState<Int>,
    percentageFirstGuesser: MutableState<Double>,
    percentageGuesser: MutableState<Double>,
    percentageSomebodyGuessed: MutableState<Double>,
    dbUserStatsRef: DatabaseReference,
    context: Context
) {
    FirebaseUtilities.databaseGet(dbUserStatsRef.child(context.getString(R.string.stats_nb_games_played_path)))
        .thenAccept {
            nbGamesPlayed.value = it.toInt()
        }
    FirebaseUtilities.databaseGet(dbUserStatsRef.child(context.getString(R.string.stats_best_score_path)))
        .thenAccept {
            bestScore.value = it.toInt()
        }
    FirebaseUtilities.databaseGet(dbUserStatsRef.child(context.getString(R.string.stats_nb_correct_guesses_path)))
        .thenAccept {
            nbCorrectGuesses.value = it.toInt()
        }
    FirebaseUtilities.databaseGet(dbUserStatsRef.child(context.getString(R.string.stats_percentage_first_guesser_path)))
        .thenAccept {
            percentageFirstGuesser.value = it.toDouble()
        }
    FirebaseUtilities.databaseGet(dbUserStatsRef.child(context.getString(R.string.stats_percentage_guesser_path)))
        .thenAccept {
            percentageGuesser.value = it.toDouble()
        }
    FirebaseUtilities.databaseGet(dbUserStatsRef.child(context.getString(R.string.stats_percentage_somebody_guessed_path)))
        .thenAccept {
            percentageSomebodyGuessed.value = it.toDouble()
        }
}

/**
 * A Numeric Statistic (for example, the number of games played)
 * @param statTitle title of the stat
 * @param stat the numeric stat
 * @param type "%" if it is a percentage
 */
@Composable
fun NumericStatistic(testTag: String, statTitle: String, stat: Double, type: String) {
    val sbStatTitle = StringBuilder()
    sbStatTitle.append(statTitle).append(": ")

    val sbStat = StringBuilder()
    if (type == IS_PERCENTAGE) {
        sbStat.append(100*stat).append("%")
    } else {
        sbStat.append(stat.toInt())
    }

    Text(
        modifier = Modifier.testTag("$testTag title"),
        textAlign = TextAlign.Center,
        text = sbStatTitle.toString(),
        style = TextStyle(
            fontSize = 18.sp
        )
    )

    Text(
        modifier = Modifier.testTag(testTag),
        text = sbStat.toString(),
        style = TextStyle(
            fontSize = 30.sp
        )
    )
}

/**
 * The top bar with activity title and back button
 */
@Composable
fun TopAppbarStats(context: Context = LocalContext.current) {
    TopAppBar(
        modifier = Modifier.testTag("topAppbarStats"),
        title = {
            Text(
                modifier = Modifier.testTag("topBarStatsText"),
                text = STATS_TITLE,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(onClick = {
                val activity = (context as? Activity)
                activity?.finish()
            }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }
        }
    )
}

