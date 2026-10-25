package com.manas.meditation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val moods = listOf(
    "Calm", "Good", "Okay", "Low", "Anxious", "Stressed"
)

private fun moodColor(mood: String?): Color = when(mood) {
    "Calm" -> Color(0xFF70B9A5)
    "Good" -> Color(0xFF83B978)
    "Okay" -> Color(0xFFB6A4E8)
    "Low" -> Color(0xFF8198C8)
    "Anxious" -> Color(0xFFE1AC72)
    "Stressed" -> Color(0xFFD68E9B)
    else -> Color(0xFF77778A)
}

@Composable
fun ProgressDashboard(context: Context) {
    val moodPrefs = remember {
        context.getSharedPreferences(
            "manas_mood_history", Context.MODE_PRIVATE
        )
    }

    val progressPrefs = remember {
        context.getSharedPreferences(
            "manas_progress", Context.MODE_PRIVATE
        )
    }

    val refresh = Unit

    val records = remember(refresh) {
        moodPrefs.getString("records", "").orEmpty()
            .lineSequence()
            .mapNotNull { line ->
                val parts = line.split("|")
                if (parts.size != 2 || parts[0].length < 10) null
                else {
                    val date = parts[0].take(10)
                    val mood = parts[1].trim()
                    if (mood in moods) date to mood else null
                }
            }.toList()
    }

    val latestMood = records.toMap()
    val today = LocalDate.now()
    val days = (27 downTo 0).map { today.minusDays(it.toLong()) }
    val week = (6 downTo 0).map { today.minusDays(it.toLong()) }

    val sessions = progressPrefs.getInt("meditation_sessions", 0)
    val totalMinutes = progressPrefs.getLong("meditation_total_minutes", 0L)
    val breathSeconds = progressPrefs.getLong("breath_total_seconds", 0L)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                "Your Progress",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Understand your practice and mood patterns",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Card {
                Column(
                    Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Your Activity", fontWeight = FontWeight.Bold)
                    Text("Mood check-ins: ${records.size}")
                    Text("Meditation sessions: $sessions")
                    Text("Meditation time: $totalMinutes min")
                    Text("Breathing time: ${breathSeconds / 60} min")
                }
            }
        }

        item {
            Card {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Mood Calendar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text("Last 28 days · Latest mood of each day")

                    days.chunked(7).forEach { row ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            row.forEach { day ->
                                val mood = latestMood[day.toString()]
                                Column(
                                    Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        Modifier
                                            .size(30.dp)
                                            .background(
                                                moodColor(mood).copy(
                                                    alpha = if (mood == null) 0.15f else 1f
                                                ),
                                                RoundedCornerShape(9.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "${day.dayOfMonth}",
                                            fontSize = 11.sp,
                                            color = if (mood == null)
                                                MaterialTheme.colorScheme.onSurface
                                            else Color(0xFF17182D)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Text("Mood legend", fontWeight = FontWeight.SemiBold)

                    moods.chunked(3).forEach { row ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            row.forEach { mood ->
                                Row(
                                    Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        Modifier.size(10.dp).background(
                                            moodColor(mood),
                                            RoundedCornerShape(3.dp)
                                        )
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(mood, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Card {
                Column(
                    Modifier.fillMaxWidth().padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Weekly Mood",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    week.forEach { day ->
                        val mood = latestMood[day.toString()]
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                day.format(DateTimeFormatter.ofPattern("EEE")),
                                modifier = Modifier.width(54.dp)
                            )
                            Box(
                                Modifier.size(12.dp).background(
                                    moodColor(mood),
                                    RoundedCornerShape(4.dp)
                                )
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(mood ?: "No check-in")
                        }
                    }
                }
            }
        }

        item {
            Card {
                Column(
                    Modifier.fillMaxWidth().padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Weekly Meditation",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    val values = week.map {
                        progressPrefs.getInt("meditation_day_$it", 0)
                    }
                    val max = (values.maxOrNull() ?: 0).coerceAtLeast(1)

                    week.forEachIndexed { index, day ->
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                day.format(DateTimeFormatter.ofPattern("EEE")),
                                modifier = Modifier.width(45.dp)
                            )
                            LinearProgressIndicator(
                                progress = {
                                    values[index].toFloat() / max
                                },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "${values[index]}m",
                                modifier = Modifier.width(38.dp)
                            )
                        }
                    }

                    Text(
                        "Daily tracking begins with newly completed sessions.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item {
            Text(
                "Your data stays on this device. " +
                "Reinstalling may remove your history.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
