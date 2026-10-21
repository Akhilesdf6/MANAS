package com.manas.meditation

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val moodOptions = listOf("Calm", "Good", "Okay", "Low", "Anxious", "Stressed")

@Composable
fun MoodCheckIn(context: Context) {
    val prefs = remember(context) { context.getSharedPreferences("manas_mood_history", Context.MODE_PRIVATE) }
    var selected by remember { mutableStateOf<String?>(null) }
    var records by remember { mutableStateOf(prefs.getString("records", "") ?: "") }
    var saved by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("How are you feeling right now?", style = MaterialTheme.typography.titleLarge)
            moodOptions.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { mood ->
                        OutlinedButton(
                            onClick = { selected = mood; saved = false },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp),
                            border = BorderStroke(1.dp, if (selected == mood) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                        ) { Text(mood, style = MaterialTheme.typography.labelSmall) }
                    }
                }
            }
            Button(onClick = {
                val mood = selected ?: return@Button
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
                val next = (records + timestamp + "|" + mood + "\n")
                if (prefs.edit().putString("records", next).commit()) {
                    records = next
                    saved = true
                }
            }, enabled = selected != null, modifier = Modifier.fillMaxWidth()) {
                Text("Save My Mood")
            }
            if (saved) Text("Mood saved on this device", style = MaterialTheme.typography.bodySmall)
            val count = records.lineSequence().count { it.isNotBlank() }
            Text("Mood check-ins: $count", style = MaterialTheme.typography.bodySmall)
        }
    }
}
