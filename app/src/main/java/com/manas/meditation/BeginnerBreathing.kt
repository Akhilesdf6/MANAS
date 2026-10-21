package com.manas.meditation

import android.content.Context
import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import java.time.LocalDate

@Composable
fun BeginnerBreathing(
    context: Context,
    language: String,
    foreground: Color,
    secondary: Color,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val prefs = remember { context.getSharedPreferences("manas_progress", Context.MODE_PRIVATE) }
    val isMl = language == "ml"
    var durationMinutes by remember { mutableIntStateOf(prefs.getInt("breath_minutes", 3).coerceIn(1, 60)) }
    var inhaleSeconds by remember { mutableIntStateOf(prefs.getInt("breath_inhale", 4).coerceIn(2, 6)) }
    var exhaleSeconds by remember { mutableIntStateOf(prefs.getInt("breath_exhale", 4).coerceIn(2, 8)) }
    var elapsedMs by remember { mutableLongStateOf(0L) }
    var running by remember { mutableStateOf(false) }
    var started by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(false) }
    val totalMs = durationMinutes * 60_000L
    val cycleMs = (inhaleSeconds + exhaleSeconds) * 1000L
    val remainingSec = ((totalMs - elapsedMs).coerceAtLeast(0L) + 999L) / 1000L
    val phaseMs = elapsedMs % cycleMs
    val inhaling = phaseMs < inhaleSeconds * 1000L
    val fraction = if (inhaling) phaseMs.toFloat() / (inhaleSeconds * 1000f)
                   else (phaseMs - inhaleSeconds * 1000L).toFloat() / (exhaleSeconds * 1000f)
    val size = if (!started) 150f else if (inhaling) 150f + 100f * fraction else 250f - 100f * fraction

    fun save() {
        if (saved || !started) return
        val seconds = (elapsedMs / 1000L).coerceAtLeast(0L)
        if (seconds > 0) {
            val today = LocalDate.now().toString()
            val days = prefs.getStringSet("meditation_dates", emptySet()).orEmpty().toMutableSet()
            days.add(today)
            prefs.edit()
                .putLong("breath_total_seconds", prefs.getLong("breath_total_seconds", 0L) + seconds)
                .putInt("breath_session_count", prefs.getInt("breath_session_count", 0) + 1)
                .putStringSet("meditation_dates", days)
                .apply()
            saved = true
            onSaved()
        }
    }

    LaunchedEffect(running) {
        if (running) {
            var previous = SystemClock.elapsedRealtime()
            while (running && elapsedMs < totalMs) {
                delay(50L)
                val now = SystemClock.elapsedRealtime()
                elapsedMs = (elapsedMs + now - previous).coerceAtMost(totalMs)
                previous = now
            }
            if (elapsedMs >= totalMs && running) {
                running = false
                save()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(if (isMl) "ശ്വാസത്തിലേക്ക് ശ്രദ്ധിക്കാം" else "Mindful Breathing",
            fontSize = 26.sp, fontWeight = FontWeight.Bold, color = foreground)
        Text(if (isMl) "സുഖകരമായ താളത്തിൽ ശ്വസിക്കൂ. നിർബന്ധിക്കേണ്ടതില്ല."
             else "Breathe comfortably. Never force your breath.",
            color = secondary, textAlign = TextAlign.Center)
        Text("%02d:%02d".format(remainingSec / 60, remainingSec % 60),
            fontSize = 38.sp, fontWeight = FontWeight.Bold, color = foreground)
        Box(Modifier.fillMaxWidth().height(270.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.size(size.dp).clip(CircleShape).background(
                Brush.radialGradient(listOf(Color(0xFFB7A8FF), Color(0xFF7864BC), Color(0xFF403478)))
            ))
        }
        Text(when {
            saved -> if (isMl) "നന്നായി ചെയ്തു!" else "Well done!"
            !started -> if (isMl) "തയ്യാറാകുമ്പോൾ ആരംഭിക്കൂ" else "Begin when you're ready"
            !running -> if (isMl) "ഇടവേള" else "Paused"
            inhaling -> if (isMl) "ശ്വാസം ഉള്ളിലേക്ക്" else "Breathe in"
            else -> if (isMl) "ശ്വാസം പുറത്തേക്ക്" else "Breathe out"
        }, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = foreground)
        Text(if (isMl) "$inhaleSeconds സെക്കൻഡ് ഉള്ളിലേക്ക് · $exhaleSeconds സെക്കൻഡ് പുറത്തേക്ക്"
             else "$inhaleSeconds sec in · $exhaleSeconds sec out", color = secondary)
        if (!saved) {
            Button(onClick = { started = true; running = !running },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Text(if (running) "Pause" else if (started) "Resume" else "Start")
            }
            if (started) {
                OutlinedButton(onClick = { running = false; save() }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (isMl) "അവസാനിപ്പിക്കുക" else "Finish & Save")
                }
            }
        }
        TextButton(onClick = { running = false; onBack() }) {
            Text(if (isMl) "ഹോമിലേക്ക് മടങ്ങുക" else "Back to Home")
        }
        if (!started) {
            TextButton(onClick = { showOptions = !showOptions }) {
                Text(if (isMl) "സമയം മാറ്റുക" else "Customize (optional)")
            }
            if (showOptions) {
                Text(if (isMl) "ദൈർഘ്യം: $durationMinutes മിനിറ്റ്" else "Duration: $durationMinutes min", color = foreground)
                Slider(value = durationMinutes.toFloat(), onValueChange = {
                    durationMinutes = it.roundToInt().coerceIn(1, 60)
                    prefs.edit().putInt("breath_minutes", durationMinutes).apply()
                }, valueRange = 1f..60f, steps = 58)
                Text("Inhale: $inhaleSeconds sec", color = foreground)
                Slider(value = inhaleSeconds.toFloat(), onValueChange = {
                    inhaleSeconds = it.roundToInt().coerceIn(2, 6)
                    prefs.edit().putInt("breath_inhale", inhaleSeconds).apply()
                }, valueRange = 2f..6f, steps = 3)
                Text("Exhale: $exhaleSeconds sec", color = foreground)
                Slider(value = exhaleSeconds.toFloat(), onValueChange = {
                    exhaleSeconds = it.roundToInt().coerceIn(2, 8)
                    prefs.edit().putInt("breath_exhale", exhaleSeconds).apply()
                }, valueRange = 2f..8f, steps = 5)
            }
        }
        Text(if (isMl) "അസ്വസ്ഥത തോന്നിയാൽ നിർത്തി വിശ്രമിക്കൂ."
             else "If you feel uncomfortable, stop and rest.",
             fontSize = 12.sp, color = secondary, textAlign = TextAlign.Center)
    }
}
