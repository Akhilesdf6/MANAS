package com.manas.meditation

import android.content.Context
import android.os.Bundle
import android.os.Build
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip


private val Navy=Color(0xFF10142E)
private val LightBg=Color(0xFFF6F5FC)
private val LightPanel=Color(0xFFFFFFFF)
private val Panel=Color(0xFF23264D)
private val Lavender=Color(0xFFB7A8FF)
private val Muted=Color(0xFFCDD0EB)
private val Gold=Color(0xFFFFD89A)

class MainActivity:ComponentActivity(){
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent { ManasApp(this) }}
}

@Composable
private fun ManasApp(context:Context){
 val prefs=remember { context.getSharedPreferences("manas_progress",Context.MODE_PRIVATE) }
 // MANAS_PHASE4_CLEANUP
 // MANAS_V11_PATCH
 // MANAS_V12_QUICK_RELIEF
 var page by remember { mutableStateOf("Home") }
 var language by remember { mutableStateOf(prefs.getString("language", "ml") ?: "ml") }
 var quickRelief by remember { mutableStateOf(false) }
   var reliefIndex by remember { mutableIntStateOf(0) }
 var themeChoice by remember { mutableStateOf(prefs.getString("theme", "System") ?: "System") }
 var morning by remember { mutableStateOf(prefs.getBoolean("morning", false)) }
 var evening by remember { mutableStateOf(prefs.getBoolean("evening", false)) }
 var completionVersion by remember { mutableIntStateOf(0) }
 val dates=remember(completionVersion) { prefs.getStringSet("meditation_dates", emptySet())!!.toSet() }
 val today=LocalDate.now()
 val currentStreak=remember(dates) { streak(dates, today) }
 val longestStreak=remember(dates) { longestStreak(dates) }
 val requestNotifications=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
   if(granted) ReminderScheduler.schedule(context, morning, evening)
 }
 fun setReminder(isMorning:Boolean, enabled:Boolean) {
   if(isMorning) morning=enabled else evening=enabled
   prefs.edit().putBoolean("morning",morning).putBoolean("evening",evening).apply()
   ReminderScheduler.schedule(context,morning,evening)
   if(enabled && Build.VERSION.SDK_INT>=33 && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=android.content.pm.PackageManager.PERMISSION_GRANTED)
     requestNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
 }
 val dark=when(themeChoice){ "Light"->false;"Dark"->true;else->(androidx.compose.foundation.isSystemInDarkTheme()) }
 val bg=if(dark) Navy else LightBg
 val panel=if(dark) Panel else LightPanel
 val fg=if(dark) Color.White else Color(0xFF17182D)
 val secondary=if(dark) Muted else Color(0xFF4F536D)
 val accent=if(dark) Lavender else Color(0xFF5842B6)
 val gold=if(dark) Gold else Color(0xFF77520E)
 val textColor=fg
 MaterialTheme(colorScheme=if(dark) darkColorScheme(primary=accent,background=bg,surface=panel,onSurface=fg,onBackground=fg) else lightColorScheme(primary=accent,background=bg,surface=panel,onSurface=fg,onBackground=fg)) {
 Column(Modifier.fillMaxSize().background(if(dark) Brush.verticalGradient(listOf(Navy,Color(0xFF1D1941),Navy)) else Brush.verticalGradient(listOf(bg,bg)))) {
   Row(Modifier.fillMaxWidth().padding(20.dp),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){
      Column { Text("MANAS",fontSize=28.sp,fontWeight=FontWeight.Bold,color=fg);Text("A Journey Within",fontSize=12.sp,color=secondary) }
      Icon(Icons.Default.SelfImprovement,null,tint=Gold,modifier=Modifier.size(34.dp))
   }
   Box(Modifier.weight(1f)) {
     when(page){

"Home" -> LazyColumn(
    contentPadding = PaddingValues(20.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp)
) {
    // MANAS_MOOD_CHECKIN_V1
    item { MoodCheckIn(context) }

    // MANAS_QUICK_RELIEF_V3
    item {
        Column(
            Modifier.fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF29224E),
                            Color(0xFF55439A),
                            Color(0xFF7662B3)
                        )
                    ),
                    RoundedCornerShape(28.dp)
                )
                .padding(24.dp)
        ) {
            Text(
                "QUICK RELIEF",
                color = Color(0xFFFFD89A),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(14.dp))

            Text(
                if(language=="ml")
                    "മനസ്സിന് ഒരു ചെറിയ ഇടവേള"
                else "A moment to feel calmer",
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                if(language=="ml")
                    "നിങ്ങൾക്ക് അനുയോജ്യമായ ലളിതമായ പരിശീലനം തിരഞ്ഞെടുക്കൂ."
                else
                    "Choose a gentle exercise that feels right for you.",
                color = Color(0xFFEAE5FF),
                fontSize = 15.sp
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { page = "Ground" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF403478)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Explore 8 Exercises")
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, null)
            }
        }
    }


    item {
        Column {
            Text(
                if(language=="ml") "സ്വാഗതം"
                else "Welcome back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = fg
            )
            Text(
                if(language=="ml")
                    "നിങ്ങൾക്കായി ഒരു ശാന്ത നിമിഷം"
                else "A peaceful moment, just for you",
                fontSize = 15.sp,
                color = secondary
            )
        }
    }

    item {
        Column(
            Modifier.fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF403478),
                            Color(0xFF7864BC),
                            Color(0xFF51428E)
                        )
                    ),
                    RoundedCornerShape(28.dp)
                )
                .padding(24.dp)
        ) {
            Icon(
                Icons.Default.SelfImprovement,
                null,
                tint = Gold,
                modifier = Modifier.size(44.dp)
            )

            Spacer(Modifier.height(18.dp))

            Text(
                "DAILY CALM",
                color = Gold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(10.dp))

            Text(
                if(language=="ml")
                    "ശാന്തതയിലേക്ക് ഒരു ചെറിയ ചുവട്"
                else "Find your moment of calm",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(10.dp))

            Text(
                if(language=="ml")
                    "അഞ്ച് മിനിറ്റ് നിങ്ങൾക്കായി മാറ്റിവയ്ക്കൂ."
                else "Take five minutes for yourself.",
                color = Color(0xFFECE7FF)
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { page = "Breathe" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF403478)
                )
            ) {
                Text("Begin")
            }
        }
    }

    item {
        Text(
            "Your Wellness Space",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = fg
        )
    }

    item {
        Tile {
            Text(
                "Breathing Exercises",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = fg
            )
            Text(
                "Breathe gently, at your own pace",
                color = secondary
            )
            Spacer(Modifier.height(10.dp))
            Button(onClick = { page = "Breathe" }) {
                Text("Explore")
            }
        }
    }

    item {
        Tile {
            Text(
                "MINDFUL MOMENT",
                color = gold,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if(language=="ml")
                    "ഈ നിമിഷത്തിൽ ശ്രദ്ധിക്കൂ."
                else "Be present in this moment.",
                fontSize = 20.sp,
                color = fg
            )
        }
    }
}


// MANAS_BREATHING_V2
"Breathe" -> {
    var breathingActive by remember {
        mutableStateOf(false)
    }

    var cycle by remember {
        mutableIntStateOf(0)
    }

    val transition = rememberInfiniteTransition(
        label = "breathing"
    )

    val animatedSize by transition.animateFloat(
        initialValue = 150f,
        targetValue = 250f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingSize"
    )

    val circleSize = if(breathingActive) {
        animatedSize.dp
    } else {
        150.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if(language=="ml")
                "ശ്വാസത്തിലേക്ക് ശ്രദ്ധിക്കാം"
            else "Mindful Breathing",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = fg
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = if(language=="ml")
                "നിങ്ങളുടെ സ്വാഭാവിക താളത്തിൽ ശ്വസിക്കൂ"
            else "Breathe at your natural pace",
            fontSize = 15.sp,
            color = secondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFFB7A8FF),
                                Color(0xFF7864BC),
                                Color(0xFF403478)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Air,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(46.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = if(breathingActive) {
                if(language=="ml")
                    "ശ്വാസം സൗമ്യമായി ശ്രദ്ധിക്കൂ"
                else "Follow your natural breath"
            } else {
                if(language=="ml")
                    "തയ്യാറാകുമ്പോൾ ആരംഭിക്കൂ"
                else "Begin when you're ready"
            },
            color = fg,
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(26.dp))

        Button(
            onClick = {
                breathingActive = !breathingActive
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                if(breathingActive)
                    Icons.Default.Pause
                else Icons.Default.PlayArrow,
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text(
                if(breathingActive)
                    "Pause"
                else "Start Breathing"
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                breathingActive = false
                cycle = 0
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset")
        }

        Spacer(Modifier.height(12.dp))

        TextButton(
            onClick = {
                breathingActive = false
                page = "Home"
            }
        ) {
            Text("Back to Home")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = if(language=="ml")
                "അസ്വസ്ഥത തോന്നിയാൽ നിർത്തി വിശ്രമിക്കൂ."
            else "If you feel uncomfortable, stop and rest.",
            fontSize = 12.sp,
            color = secondary,
            textAlign = TextAlign.Center
        )
    }
}


"Ground" -> {
    val titles = listOf(
        "5-4-3-2-1 Grounding",
        "Calm Breathing",
        "Racing Thoughts",
        "Feeling Overwhelmed",
        "Body Relaxation",
        "Before Sleep",
        "Focus Reset",
        "One-Minute Pause"
    )

    val titlesMl = listOf(
        "ഗ്രൗണ്ടിംഗ് പരിശീലനം",
        "ശാന്തമായ ശ്വാസം",
        "വേഗത്തിലുള്ള ചിന്തകൾ",
        "മനസ്സിന് ഭാരം തോന്നുമ്പോൾ",
        "ശരീരത്തിന് വിശ്രമം",
        "ഉറങ്ങുന്നതിന് മുമ്പ്",
        "ശ്രദ്ധ വീണ്ടെടുക്കാം",
        "ഒരു മിനിറ്റ് ഇടവേള"
    )

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                "Quick Relief",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = fg
            )

            Spacer(Modifier.height(8.dp))

            Text(
                if(language=="ml")
                    "നിങ്ങൾക്ക് അനുയോജ്യമായ പരിശീലനം തിരഞ്ഞെടുക്കൂ."
                else "Choose what feels helpful right now.",
                color = secondary
            )
        }

        items(titles.size) { index ->
            Tile {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            if(language=="ml") titlesMl[index]
                            else titles[index],
                            color = fg,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            titles[index],
                            color = secondary,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = {
                            reliefIndex = index
                            page = "ReliefDetail"
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Open exercise",
                            tint = accent
                        )
                    }
                }
            }
        }

        item {
            TextButton(onClick = { page = "Home" }) {
                Text("Back to Home")
            }
        }
    }
}

"ReliefDetail" -> {
    val names = listOf(
        "5-4-3-2-1 Grounding",
        "Calm Breathing",
        "Racing Thoughts",
        "Feeling Overwhelmed",
        "Body Relaxation",
        "Before Sleep",
        "Focus Reset",
        "One-Minute Pause"
    )

    val namesMl = listOf(
        "ഗ്രൗണ്ടിംഗ് പരിശീലനം",
        "ശാന്തമായ ശ്വാസം",
        "വേഗത്തിലുള്ള ചിന്തകൾ",
        "മനസ്സിന് ഭാരം തോന്നുമ്പോൾ",
        "ശരീരത്തിന് വിശ്രമം",
        "ഉറങ്ങുന്നതിന് മുമ്പ്",
        "ശ്രദ്ധ വീണ്ടെടുക്കാം",
        "ഒരു മിനിറ്റ് ഇടവേള"
    )

    val stepsEn = listOf(
        listOf(
            "Notice five things you can see.",
            "Notice four things you can feel and three sounds.",
            "Notice two smells and one taste, if available."
        ),
        listOf(
            "Sit comfortably and relax your shoulders.",
            "Notice your natural breathing without forcing it.",
            "Continue at your own pace. Pause whenever needed."
        ),
        listOf(
            "Notice the thoughts passing through your mind.",
            "You do not need to solve every thought right now.",
            "Gently bring your attention to your surroundings."
        ),
        listOf(
            "Find a comfortable place to pause.",
            "Choose one small thing you can do next.",
            "Give yourself permission to take your time."
        ),
        listOf(
            "Relax your forehead and jaw.",
            "Let your shoulders rest comfortably.",
            "Notice where your body feels supported."
        ),
        listOf(
            "Settle into a comfortable resting position.",
            "Notice the support beneath your body.",
            "Allow your attention to rest without forcing sleep."
        ),
        listOf(
            "Look away from your task for a moment.",
            "Notice three details in your surroundings.",
            "Choose one small task to return to."
        ),
        listOf(
            "Pause what you are doing.",
            "Notice your feet and your natural breathing.",
            "Return when you feel ready."
        )
    )

    val stepsMl = listOf(
        listOf(
            "കാണാൻ കഴിയുന്ന അഞ്ച് വസ്തുക്കളെ ശ്രദ്ധിക്കൂ.",
            "സ്പർശിച്ച് അറിയാവുന്ന നാലു കാര്യങ്ങളും മൂന്ന് ശബ്ദങ്ങളും ശ്രദ്ധിക്കൂ.",
            "സാധ്യമെങ്കിൽ രണ്ട് മണങ്ങളും ഒരു രുചിയും ശ്രദ്ധിക്കൂ."
        ),
        listOf(
            "സൗകര്യമായി ഇരുന്ന് തോളുകൾ വിശ്രമിക്കാൻ അനുവദിക്കൂ.",
            "ശ്വാസം നിർബന്ധിക്കാതെ സ്വാഭാവികമായി ശ്രദ്ധിക്കൂ.",
            "നിങ്ങളുടെ താളത്തിൽ തുടരൂ. ആവശ്യമെങ്കിൽ ഇടവേള എടുക്കൂ."
        ),
        listOf(
            "മനസ്സിലൂടെ കടന്നുപോകുന്ന ചിന്തകളെ ശ്രദ്ധിക്കൂ.",
            "എല്ലാ ചിന്തകൾക്കും ഇപ്പോൾ പരിഹാരം കണ്ടെത്തേണ്ടതില്ല.",
            "ചുറ്റുപാടുകളിലേക്ക് ശ്രദ്ധ സൗമ്യമായി തിരിക്കൂ."
        ),
        listOf(
            "സൗകര്യപ്രദമായ സ്ഥലത്ത് അല്പനേരം ഇരിക്കൂ.",
            "അടുത്തതായി ചെയ്യാവുന്ന ഒരു ചെറിയ കാര്യം തിരഞ്ഞെടുക്കൂ.",
            "നിങ്ങളുടെ സമയമെടുത്ത് മുന്നോട്ട് പോകൂ."
        ),
        listOf(
            "നെറ്റിയും താടിയെല്ലും വിശ്രമിക്കാൻ അനുവദിക്കൂ.",
            "തോളുകൾ സൗകര്യപ്രദമായി വയ്ക്കൂ.",
            "ശരീരത്തിന് ലഭിക്കുന്ന പിന്തുണ ശ്രദ്ധിക്കൂ."
        ),
        listOf(
            "സൗകര്യപ്രദമായി വിശ്രമിക്കൂ.",
            "ശരീരത്തിന് താഴെയുള്ള പിന്തുണ ശ്രദ്ധിക്കൂ.",
            "ഉറക്കം നിർബന്ധിക്കാതെ വിശ്രമിക്കാൻ അനുവദിക്കൂ."
        ),
        listOf(
            "ചെയ്യുന്ന കാര്യത്തിൽ നിന്ന് അല്പനേരം കണ്ണുകൾ മാറ്റൂ.",
            "ചുറ്റുമുള്ള മൂന്ന് കാര്യങ്ങൾ ശ്രദ്ധിക്കൂ.",
            "തിരികെ ചെയ്യാനുള്ള ഒരു ചെറിയ കാര്യം തിരഞ്ഞെടുക്കൂ."
        ),
        listOf(
            "ചെയ്യുന്ന കാര്യം അല്പനേരം നിർത്തൂ.",
            "കാലുകളും സ്വാഭാവിക ശ്വാസവും ശ്രദ്ധിക്കൂ.",
            "തയ്യാറാകുമ്പോൾ തിരികെ തുടരൂ."
        )
    )

    var step by remember(reliefIndex) {
        mutableIntStateOf(0)
    }

    val instructions =
        if(language=="ml") stepsMl[reliefIndex]
        else stepsEn[reliefIndex]

    Column(
        Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Spa,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(64.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            if(language=="ml") namesMl[reliefIndex]
            else names[reliefIndex],
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Step ${step + 1} of ${instructions.size}",
            color = secondary
        )

        Spacer(Modifier.height(28.dp))

        Tile {
            Text(
                instructions[step],
                color = fg,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = {
                if(step < instructions.lastIndex) {
                    step++
                } else {
                    page = "Ground"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if(step == instructions.lastIndex)
                    "Finish"
                else "Next Step"
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { page = "Ground" },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Exercises")
        }
    }
}

"Player" -> Column(Modifier.fillMaxSize().padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
          Icon(Icons.Default.SelfImprovement, "Meditation", modifier=Modifier.size(88.dp),tint=accent)
          Spacer(Modifier.height(20.dp))
          Text(if(language=="ml") "സൗമ്യമായ ഇടവേള" else "A Gentle Pause",fontSize=26.sp,fontWeight=FontWeight.Bold,color=fg,textAlign=TextAlign.Center)
          Spacer(Modifier.height(16.dp))
          Text(if(language=="ml") "സുഖമായി ഇരിക്കൂ. ചുറ്റുമുള്ള വസ്തുക്കൾ ശ്രദ്ധിക്കൂ. ശ്വാസം സ്വാഭാവികമായി തുടരട്ടെ. ആവശ്യമെങ്കിൽ ഇടവേള എടുക്കാം." else "Sit comfortably. Notice what is around you. Let your breathing stay natural. Take a break whenever you need.",color=secondary,textAlign=TextAlign.Center)
          Spacer(Modifier.height(24.dp))
          Button(onClick={page="Home"}) { Text(if(language=="ml") "പൂർത്തിയായി" else "Done") }
       }
       "Progress" -> Column(Modifier.padding(22.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
          Text("My Progress",fontSize=25.sp,color=fg,fontWeight=FontWeight.Bold)
          Tile { Text("$currentStreak day current streak",color=fg,fontSize=22.sp); Text("Longest streak: $longestStreak days",color=secondary) }
          val moodPrefs=context.getSharedPreferences("manas_mood_history",Context.MODE_PRIVATE)
          val moodCount=moodPrefs.getString("records", "").orEmpty().lineSequence().count { it.isNotBlank() }
          Tile { Text("Mood check-ins: $moodCount",color=fg,fontSize=20.sp) }
          Tile { Text("Meditation days recorded: ${dates.size}",color=fg,fontSize=20.sp) }
          Text("Progress is stored on this device only. Reinstalling may remove it.",color=secondary,fontSize=12.sp)
       }
       "Settings" -> LazyColumn(contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
         item { Text("Settings",fontSize=25.sp,color=fg,fontWeight=FontWeight.Bold) }
         item { Tile {
           Text("Language / ഭാഷ",color=fg,fontWeight=FontWeight.Bold)
           listOf("ml" to "മലയാളം", "en" to "English").forEach { (code,label) ->
             Row(verticalAlignment=Alignment.CenterVertically) {
               RadioButton(selected=language==code,onClick={language=code;prefs.edit().putString("language",code).apply()})
               Text(label,color=fg)
             }
           }
         } }
         item { Tile {
           Text("Appearance",color=fg,fontWeight=FontWeight.Bold)
           listOf("System","Light","Dark").forEach { choice ->
             Row(verticalAlignment=Alignment.CenterVertically) {
               RadioButton(selected=themeChoice==choice,onClick={themeChoice=choice;prefs.edit().putString("theme",choice).apply()})
               Text(choice,color=fg)
             }
           }
         } }
         item { Tile {
           Text("Daily reminders",color=fg,fontWeight=FontWeight.Bold)
           Row(verticalAlignment=Alignment.CenterVertically) { Text("Morning · 7:00 AM",color=fg,modifier=Modifier.weight(1f)); Switch(checked=morning,onCheckedChange={setReminder(true,it)}) }
           Row(verticalAlignment=Alignment.CenterVertically) { Text("Evening · 8:00 PM",color=fg,modifier=Modifier.weight(1f)); Switch(checked=evening,onCheckedChange={setReminder(false,it)}) }
           Text("Reminders may arrive slightly later due to Android battery optimization.",color=secondary,fontSize=12.sp)
         } }
         item { Tile { Text("Your progress",color=fg,fontWeight=FontWeight.Bold); Text("Current streak: $currentStreak days",color=secondary);Text("Longest streak: $longestStreak days",color=secondary) } }
         item { Text("MANAS · Progress stored on this device",color=secondary,fontSize=12.sp) }
       }
     }
   }
   NavigationBar(containerColor=panel){
      listOf(Triple("Home",Icons.Default.Home,"Home"),Triple("Progress",Icons.Default.BarChart,"Progress"),Triple("Settings",Icons.Default.Settings,"Settings")).forEach { (name,icon,label)->NavigationBarItem(selected=page==name,onClick={page=name},icon={Icon(icon,label)},label={Text(label)},colors=NavigationBarItemDefaults.colors(selectedIconColor=accent,selectedTextColor=accent,unselectedIconColor=secondary,unselectedTextColor=secondary)) }
   }
 }
 }
}
@Composable private fun Tile(content:@Composable ColumnScope.()->Unit){Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface,RoundedCornerShape(20.dp)).padding(18.dp),content=content)}

private fun streak(dates:Set<String>, today:LocalDate):Int {
 var day=if(today.toString() in dates) today else today.minusDays(1)
 var count=0
 while(day.toString() in dates) { count++; day=day.minusDays(1) }
 return count
}
private fun longestStreak(dates:Set<String>):Int {
 val sorted=dates.mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.sorted()
 var best=0;var run=0;var previous:LocalDate?=null
 for(day in sorted){ run=if(previous!=null && ChronoUnit.DAYS.between(previous,day)==1L) run+1 else 1;best=maxOf(best,run);previous=day }
 return best
}
