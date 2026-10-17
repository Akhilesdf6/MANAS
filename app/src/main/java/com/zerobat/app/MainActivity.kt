package com.zerobat.app

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Purple = Color(0xFFA78BFA)
private val Dark = Color(0xFF100F18)
private val Panel = Color(0xFF1C1A29)
private val Muted = Color(0xFFBDB8CC)
private const val DEFAULT_PIN = "1234"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ZeroBatApp(applicationContext) }
    }
}

private enum class Screen { SETUP, BREAK, COMPLETE }

@Composable
private fun ZeroBatApp(context: Context) {
    val prefs = remember { context.getSharedPreferences("zerobat_settings", Context.MODE_PRIVATE) }
    var minutes by remember { mutableIntStateOf(5) }
    var custom by remember { mutableStateOf("20") }
    var screen by remember { mutableStateOf(Screen.SETUP) }
    var remaining by remember { mutableLongStateOf(0L) }
    var deadline by remember { mutableLongStateOf(0L) }
    var paused by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(false) }
    var changePin by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf(prefs.getString("parent_pin", DEFAULT_PIN) ?: DEFAULT_PIN) }
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var endConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(screen, paused, deadline) {
        if (screen == Screen.BREAK && !paused) {
            while (true) {
                val ms = (deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
                remaining = (ms + 999L) / 1000L
                if (ms == 0L) {
                    screen = Screen.COMPLETE
                    break
                }
                delay(200L)
            }
        }
    }

    BackHandler(enabled = screen == Screen.BREAK) { endConfirm = true }
    BackHandler(enabled = screen == Screen.COMPLETE) { screen = Screen.SETUP }

    MaterialTheme(colorScheme = darkColorScheme(primary = Purple, background = Dark, surface = Panel)) {
        Surface(Modifier.fillMaxSize(), color = Dark) {
            when (screen) {
                Screen.SETUP -> SetupScreen(minutes, { minutes = it }, custom, { custom = it }, {
                    val duration = if (minutes == -1) (custom.toIntOrNull() ?: 1).coerceIn(1, 180) else minutes
                    remaining = duration * 60L
                    deadline = SystemClock.elapsedRealtime() + remaining * 1000L
                    paused = false
                    screen = Screen.BREAK
                }, { settings = true })
                Screen.BREAK -> BreakScreen(remaining, paused, onPauseResume = {
                    if (paused) {
                        deadline = SystemClock.elapsedRealtime() + remaining * 1000L
                        paused = false
                    } else {
                        remaining = ((deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0L) + 999L) / 1000L
                        paused = true
                    }
                }, onEnd = { endConfirm = true })
                Screen.COMPLETE -> CompleteScreen { screen = Screen.SETUP }
            }
        }

        if (endConfirm) AlertDialog(
            onDismissRequest = { endConfirm = false },
            title = { Text("End break?") },
            text = { Text("Your break will stop. You can start another one whenever you want.") },
            confirmButton = { TextButton(onClick = { endConfirm = false; screen = Screen.SETUP }) { Text("End break") } },
            dismissButton = { TextButton(onClick = { endConfirm = false }) { Text("Continue") } }
        )
        if (settings) AlertDialog(
            onDismissRequest = { settings = false },
            title = { Text("Parent settings") },
            text = { Column {
                Text("PIN protects settings only. Anyone can end a break for safety.")
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { settings = false; changePin = true; oldPin = ""; newPin = ""; pinError = false }) {
                    Text("Change parent PIN")
                }
            } },
            confirmButton = { TextButton(onClick = { settings = false }) { Text("Done") } }
        )
        if (changePin) AlertDialog(
            onDismissRequest = { changePin = false },
            title = { Text("Change parent PIN") },
            text = { Column {
                Text("Enter the current PIN, then a new 4–8 digit PIN.")
                OutlinedTextField(oldPin, { if (it.length <= 8 && it.all(Char::isDigit)) oldPin = it },
                    label = { Text("Current PIN") }, singleLine = true, isError = pinError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation())
                OutlinedTextField(newPin, { if (it.length <= 8 && it.all(Char::isDigit)) newPin = it },
                    label = { Text("New PIN") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation())
                if (pinError) Text("Check the current PIN and new PIN length.", color = MaterialTheme.colorScheme.error)
            } },
            confirmButton = { TextButton(onClick = {
                if (oldPin == pin && newPin.length in 4..8) {
                    pin = newPin
                    prefs.edit().putString("parent_pin", pin).apply()
                    changePin = false
                } else pinError = true
            }) { Text("Save") } },
            dismissButton = { TextButton(onClick = { changePin = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun SetupScreen(minutes: Int, setMinutes: (Int) -> Unit, custom: String,
    setCustom: (String) -> Unit, onStart: () -> Unit, onSettings: () -> Unit) {
    Column(Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🌙 ZeroBat", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = Purple)
        Spacer(Modifier.height(8.dp))
        Text("Take a mindful screen break", fontSize = 17.sp, color = Color.White)
        Spacer(Modifier.height(24.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Choose break duration", color = Muted)
                listOf(1, 5, 10, 15, 30).forEach { n ->
                    Row(Modifier.fillMaxWidth().clickable { setMinutes(n) }.padding(vertical = 1.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(minutes == n, { setMinutes(n) })
                        Text("$n minute${if (n == 1) "" else "s"}", color = Color.White)
                    }
                }
                Row(Modifier.fillMaxWidth().clickable { setMinutes(-1) }, verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(minutes == -1, { setMinutes(-1) })
                    Text("Custom (1–180 min)", color = Color.White)
                }
                if (minutes == -1) OutlinedTextField(custom,
                    { if (it.length <= 3 && it.all(Char::isDigit)) setCustom(it) },
                    label = { Text("Minutes") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(Modifier.height(18.dp))
        Button(onClick = onStart, enabled = minutes != -1 || (custom.toIntOrNull() ?: 0) in 1..180,
            modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp)) {
            Text("START BREAK", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onSettings, modifier = Modifier.fillMaxWidth()) { Text("Parent settings / PIN") }
        Spacer(Modifier.height(14.dp))
        Text("Break mode is voluntary. Android Home and emergency functions remain available.",
            color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun BreakScreen(remaining: Long, paused: Boolean, onPauseResume: () -> Unit, onEnd: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Dark).safeDrawingPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("🌿", fontSize = 64.sp)
        Spacer(Modifier.height(20.dp))
        Text(if (paused) "Break paused" else "Take a break", color = Color.White,
            fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Rest your eyes. Breathe. Recharge.", color = Muted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        Text("%02d:%02d".format(remaining / 60, remaining % 60), fontSize = 64.sp,
            color = Purple, fontWeight = FontWeight.Bold)
        Text("Time remaining", color = Muted)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onPauseResume, modifier = Modifier.fillMaxWidth().height(54.dp)) {
            Text(if (paused) "RESUME BREAK" else "PAUSE BREAK")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onEnd, modifier = Modifier.fillMaxWidth().height(54.dp)) { Text("END BREAK") }
        Spacer(Modifier.height(20.dp))
        Text("You can end this break at any time.", color = Muted, fontSize = 12.sp)
    }
}

@Composable
private fun CompleteScreen(onDone: () -> Unit) {
    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("✨", fontSize = 64.sp)
        Spacer(Modifier.height(18.dp))
        Text("Break complete!", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text("Nice work taking time away from your screen.", color = Muted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth().height(54.dp)) { Text("DONE") }
    }
}
