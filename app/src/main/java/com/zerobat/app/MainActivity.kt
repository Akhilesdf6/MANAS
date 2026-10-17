package com.zerobat.app

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Purple = Color(0xFFA78BFA)
private val Dark = Color(0xFF12101D)
private val Panel = Color(0xFF242034)
private val Muted = Color(0xFFC5BED6)
private enum class Page { HOME, SESSION, BREATH, PROGRESS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MeditationApp(applicationContext) }
    }
}

@Composable
private fun MeditationApp(context: Context) {
    val prefs = remember { context.getSharedPreferences("meditation", Context.MODE_PRIVATE) }
    var malayalam by remember { mutableStateOf(prefs.getBoolean("malayalam", true)) }
    var page by remember { mutableStateOf(Page.HOME) }
    var duration by remember { mutableIntStateOf(5) }
    var remaining by remember { mutableLongStateOf(300L) }
    var deadline by remember { mutableLongStateOf(0L) }
    var running by remember { mutableStateOf(false) }
    var completed by remember { mutableStateOf(false) }
    var sessions by remember { mutableIntStateOf(prefs.getInt("sessions", 0)) }
    var totalMinutes by remember { mutableIntStateOf(prefs.getInt("minutes", 0)) }
    fun t(ml: String, en: String) = if (malayalam) ml else en
    fun stopSession() { running = false; completed = false; page = Page.HOME }
    BackHandler(enabled = page != Page.HOME) { if (page == Page.SESSION) stopSession() else page = Page.HOME }

    LaunchedEffect(page, running, deadline) {
        if (page == Page.SESSION && running) {
            while (running && page == Page.SESSION) {
                val ms = (deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
                remaining = (ms + 999) / 1000
                if (ms <= 0) {
                    running = false
                    completed = true
                    sessions += 1
                    totalMinutes += duration
                    prefs.edit().putInt("sessions", sessions).putInt("minutes", totalMinutes).apply()
                    break
                }
                delay(200)
            }
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(primary = Purple, background = Dark, surface = Panel)) {
        Column(Modifier.fillMaxSize().background(Dark).windowInsetsPadding(WindowInsets.safeDrawing).padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("🧘 ZeroBat", color = Purple, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = {
                    malayalam = !malayalam
                    prefs.edit().putBoolean("malayalam", malayalam).apply()
                }) { Text(if (malayalam) "English" else "മലയാളം") }
            }
            Spacer(Modifier.height(12.dp))
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when (page) {
                    Page.HOME -> Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(t("ശാന്തതയിലേക്ക് സ്വാഗതം", "Welcome to your calm"), fontSize = 25.sp, color = Color.White, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(10.dp))
                        Text(t("ചെറിയൊരു ഇടവേള. മനസ്സിന് ആശ്വാസം.", "A gentle moment for yourself."), color = Muted)
                        Spacer(Modifier.height(30.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp)) {
                            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                                Text(t("മെഡിറ്റേഷൻ സമയം", "Meditation duration"), color = Color.White, fontSize = 18.sp)
                                Spacer(Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                    listOf(3, 5, 10, 15).forEach { n ->
                                        FilterChip(selected = duration == n, onClick = { duration = n }, label = { Text("$n min") })
                                    }
                                }
                                Spacer(Modifier.height(14.dp))
                                Button(onClick = {
                                    remaining = duration * 60L
                                    deadline = SystemClock.elapsedRealtime() + remaining * 1000
                                    completed = false
                                    running = true
                                    page = Page.SESSION
                                }, modifier = Modifier.fillMaxWidth()) { Text(t("മെഡിറ്റേഷൻ തുടങ്ങാം", "Start meditation")) }
                            }
                        }
                        Spacer(Modifier.height(18.dp))
                        OutlinedButton(onClick = { page = Page.BREATH }, modifier = Modifier.fillMaxWidth()) { Text(t("🌬️ ശ്വസന പരിശീലനം", "🌬️ Breathing exercise")) }
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = { page = Page.PROGRESS }, modifier = Modifier.fillMaxWidth()) { Text(t("📊 എന്റെ പുരോഗതി", "📊 My progress")) }
                        Spacer(Modifier.height(20.dp))
                        Text(t("തുടക്കക്കാർക്കായി • ഓഫ്‌ലൈൻ", "For beginners • Works offline"), color = Muted, fontSize = 13.sp)
                    }
                    Page.SESSION -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(if (completed) t("പൂർത്തിയായി! ✨", "Session complete! ✨") else t("ശ്വാസത്തിൽ ശ്രദ്ധിക്കുക", "Focus on your breath"), color = Purple, fontSize = 24.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(28.dp))
                        Text("%02d:%02d".format(remaining / 60, remaining % 60), color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        Text(t("സുഖമായി ഇരിക്കുക. പതുക്കെ ശ്വസിക്കുക.", "Sit comfortably. Breathe gently."), color = Muted, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(30.dp))
                        if (!completed) Button(onClick = {
                            if (running) {
                                remaining = ((deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0L) + 999) / 1000
                                running = false
                            } else {
                                deadline = SystemClock.elapsedRealtime() + remaining * 1000
                                running = true
                            }
                        }) { Text(if (running) t("താൽക്കാലികമായി നിർത്തുക", "Pause") else t("തുടരുക", "Resume")) }
                        Spacer(Modifier.height(10.dp))
                        OutlinedButton(onClick = { stopSession() }) { Text(t("അവസാനിപ്പിക്കുക", "Finish / Exit")) }
                    }
                    Page.BREATH -> BreathingScreen(malayalam)
                    Page.PROGRESS -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(t("എന്റെ പുരോഗതി", "My progress"), color = Purple, fontSize = 27.sp)
                        Spacer(Modifier.height(24.dp))
                        Text(t("പൂർത്തിയാക്കിയ സെഷനുകൾ: $sessions", "Completed sessions: $sessions"), color = Color.White, fontSize = 20.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(t("ആകെ മിനിറ്റുകൾ: $totalMinutes", "Total minutes: $totalMinutes"), color = Color.White, fontSize = 20.sp)
                        Spacer(Modifier.height(14.dp))
                        Text(t("പൂർത്തിയായ സെഷനുകൾ മാത്രം രേഖപ്പെടുത്തുന്നു.", "Only completed sessions are counted."), color = Muted, fontSize = 13.sp)
                    }
                }
            }
            if (page != Page.HOME) {
                TextButton(onClick = { if (page == Page.SESSION) stopSession() else page = Page.HOME }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(t("← ഹോം", "← Home"))
                }
            }
        }
    }
}

@Composable
private fun BreathingScreen(malayalam: Boolean) {
    var active by remember { mutableStateOf(true) }
    var seconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(active) {
        while (active) { delay(1000); seconds += 1 }
    }
    val inhale = seconds % 8 < 4
    val transition = rememberInfiniteTransition(label = "breathing")
    val size by transition.animateFloat(
        initialValue = 0.60f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(4000), repeatMode = RepeatMode.Reverse),
        label = "breathCircle"
    )
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(if (malayalam) "ശ്വസന പരിശീലനം" else "Breathing exercise", color = Purple, fontSize = 25.sp)
        Spacer(Modifier.height(25.dp))
        Box(Modifier.size(230.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(color = Purple.copy(alpha = 0.15f), radius = this.size.minDimension / 2)
                drawCircle(color = Purple, radius = this.size.minDimension * (if (active) size else 0.60f) / 2)
            }
            Text(if (!active) (if (malayalam) "നിർത്തി" else "Paused") else if (inhale) (if (malayalam) "ശ്വാസം എടുക്കുക" else "Inhale") else (if (malayalam) "ശ്വാസം വിടുക" else "Exhale"), color = Dark, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(24.dp))
        Text(if (malayalam) "4 സെക്കൻഡ് ശ്വാസം എടുക്കുക, 4 സെക്കൻഡ് വിടുക." else "Inhale for 4 seconds, exhale for 4 seconds.", color = Muted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(22.dp))
        Button(onClick = { active = !active }) { Text(if (active) (if (malayalam) "നിർത്തുക" else "Pause") else (if (malayalam) "തുടരുക" else "Resume")) }
        Spacer(Modifier.height(10.dp))
        Text(if (malayalam) "അസൗകര്യം തോന്നിയാൽ സാധാരണ രീതിയിൽ ശ്വസിക്കുക." else "Breathe normally if you feel uncomfortable.", color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}
