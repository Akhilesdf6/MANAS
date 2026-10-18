package com.manas.meditation

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import java.util.Locale

private val Navy=Color(0xFF10142E)
private val Panel=Color(0xFF23264D)
private val Lavender=Color(0xFFB7A8FF)
private val Muted=Color(0xFFCDD0EB)
private val Gold=Color(0xFFFFD89A)
private val FreeTitles=listOf("ശ്വാസത്തെ അറിയാം", "ശാന്തമായ തുടക്കം", "ശരീരത്തെ ശ്രദ്ധിക്കാം", "ചിന്തകളെ നിരീക്ഷിക്കാം", "നന്ദിയോടെ ഒരു നിമിഷം", "മനസ്സിന് വിശ്രമം", "ഏഴ് ദിവസത്തെ യാത്ര")
private val Themes=listOf("ശ്വാസവും ശ്രദ്ധയും", "മനസ്സിന്റെ ശാന്തത", "ശരീരവും വിശ്രമവും", "ഏകാഗ്രത", "സ്വയം കരുതൽ", "ഉറക്കത്തിനുള്ള ശാന്തത", "ദൈനംദിന ധ്യാനം")
private fun title(day:Int)=if(day<=7) FreeTitles[day-1] else Themes[((day-8)/12).coerceAtMost(6)]
private fun guidance(day:Int)="നമസ്കാരം. മനസ് ധ്യാനത്തിലേക്ക് സ്വാഗതം. സുഖമായി ഇരിക്കുക. കണ്ണുകൾ അടയ്ക്കാൻ താൽപര്യമുണ്ടെങ്കിൽ അടയ്ക്കാം. ശ്വാസം സ്വാഭാവികമായി അകത്തേക്കും പുറത്തേക്കും പോകുന്നത് ശ്രദ്ധിക്കുക. ശ്രദ്ധ മാറിയാൽ സൗമ്യമായി ശ്വാസത്തിലേക്ക് തിരികെ വരിക. ഈ നിമിഷത്തിൽ ശാന്തമായി തുടരുക. ഇന്നത്തെ പരിശീലനം ഇവിടെ അവസാനിക്കുന്നു. നന്ദി."

class MainActivity:ComponentActivity(){
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent { ManasApp(this) }}
}

@Composable
private fun ManasApp(context:Context){
 val prefs=remember { context.getSharedPreferences("manas_progress",Context.MODE_PRIVATE) }
 val completed=remember { mutableStateListOf<Int>().apply { addAll((1..90).filter { prefs.getBoolean("done_$it",false) }) } }
 var page by remember { mutableStateOf("Home") }
 var selectedDay by remember { mutableIntStateOf(1) }
 var speaking by remember { mutableStateOf(false) }
 var ttsReady by remember { mutableStateOf(false) }
 val tts=remember { TextToSpeech(context) { status -> ttsReady=status==TextToSpeech.SUCCESS } }
 DisposableEffect(tts){onDispose { tts.stop();tts.shutdown() }}
 val textColor=Color.White
 MaterialTheme(colorScheme=darkColorScheme(primary=Lavender,background=Navy,surface=Panel,onSurface=Color.White,onBackground=Color.White)) {
 Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Navy,Color(0xFF1D1941),Navy)))) {
   Row(Modifier.fillMaxWidth().padding(20.dp),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){
      Column { Text("MANAS",fontSize=28.sp,fontWeight=FontWeight.Bold,color=Color.White);Text("A Journey Within",fontSize=12.sp,color=Muted) }
      Icon(Icons.Default.SelfImprovement,null,tint=Gold,modifier=Modifier.size(34.dp))
   }
   Box(Modifier.weight(1f)) {
     when(page){
       "Home" -> LazyColumn(contentPadding=PaddingValues(horizontal=18.dp,vertical=8.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
          item { Text("മനസ്സിലേക്കൊരു യാത്ര 🌙",fontSize=23.sp,fontWeight=FontWeight.Bold,color=textColor) }
          item { Tile { Text("7 DAYS FREE",color=Gold,fontWeight=FontWeight.Bold); Spacer(Modifier.height(8.dp));Text("ഇന്നത്തെ ധ്യാനം",fontSize=22.sp,color=Color.White);Text("Day ${((1..7).firstOrNull { it !in completed } ?: 7)} · ${title((1..7).firstOrNull { it !in completed } ?: 7)}",color=Muted);Spacer(Modifier.height(12.dp));Button(onClick={selectedDay=(1..7).firstOrNull { it !in completed } ?: 7;page="Player"}) {Text("Start Meditation")}} }
          item { Tile { Text("90 DAYS PREMIUM",color=Gold,fontWeight=FontWeight.Bold);Text("₹199 / 12 months",fontSize=25.sp,color=Color.White);Text("Payment is not enabled in this preview build.",color=Muted,fontSize=12.sp);TextButton(onClick={page="Premium"}){Text("View Premium Plan →")}} }
          item { Text("Completed: ${completed.size} / 90",color=Muted);LinearProgressIndicator(progress={completed.size/90f},modifier=Modifier.fillMaxWidth(),color=Lavender) }
       }
       "Journey" -> LazyColumn(contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
          item { Text("90-Day Meditation Journey",fontSize=22.sp,fontWeight=FontWeight.Bold,color=Color.White);Text("Day 1–7 Free · Day 8–90 Premium",color=Muted) }
          items((1..90).toList()){day->
            Tile { Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.fillMaxWidth()){
              Column(Modifier.weight(1f)) {Text("Day $day · ${title(day)}",color=Color.White,fontWeight=FontWeight.Medium);Text(if(day<=7) "FREE · 5 min" else "PREMIUM · 10 min",color=if(day<=7) Color(0xFFA2E6C7) else Gold,fontSize=12.sp)}
              if(day in completed) Icon(Icons.Default.CheckCircle,null,tint=Color(0xFFA2E6C7)) else if(day>7) Icon(Icons.Default.Lock,null,tint=Gold) else IconButton(onClick={selectedDay=day;page="Player"}){Icon(Icons.Default.PlayArrow,"Play",tint=Lavender)}
            }}
          }
       }
       "Player" -> Column(Modifier.fillMaxSize().padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
           Icon(Icons.Default.SelfImprovement,null,modifier=Modifier.size(110.dp),tint=Lavender)
           Spacer(Modifier.height(20.dp));Text("Day $selectedDay",color=Gold);Text(title(selectedDay),fontSize=25.sp,color=Color.White,textAlign=TextAlign.Center)
           Spacer(Modifier.height(14.dp));Text("Malayalam guidance · device text-to-speech preview",color=Muted,textAlign=TextAlign.Center,fontSize=12.sp)
           Spacer(Modifier.height(22.dp));Button(enabled=ttsReady,onClick={if(speaking){tts.stop();speaking=false}else{tts.language=Locale.forLanguageTag("ml-IN");tts.speak(guidance(selectedDay),TextToSpeech.QUEUE_FLUSH,null,"manas-$selectedDay");speaking=true}}){Icon(if(speaking) Icons.Default.Stop else Icons.Default.PlayArrow,null);Text(if(speaking) "Stop guidance" else "Play guidance")}
           Spacer(Modifier.height(10.dp));Text("Malayalam voice availability depends on installed TTS engine.",color=Muted,fontSize=12.sp,textAlign=TextAlign.Center)
           Spacer(Modifier.height(20.dp));Button(onClick={if(selectedDay<=7){if(selectedDay !in completed)completed.add(selectedDay);prefs.edit().putBoolean("done_$selectedDay",true).apply();page="Progress"}else page="Premium"}) {Text(if(selectedDay<=7) "Mark Session Complete" else "Premium Required")}
       }
       "Progress" -> Column(Modifier.padding(22.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){
          Text("My Progress",fontSize=25.sp,color=Color.White,fontWeight=FontWeight.Bold)
          Tile {Text("${completed.size} sessions completed",fontSize=23.sp,color=Color.White);Spacer(Modifier.height(12.dp));LinearProgressIndicator(progress={completed.size/90f},modifier=Modifier.fillMaxWidth())}
          Tile {Text("Free journey: ${completed.count{it<=7}} / 7",color=Color.White);Text("Premium journey: locked in preview",color=Muted)}
          Text("Progress is stored on this device only. Reinstalling may remove it.",color=Muted,fontSize=12.sp)
       }
       "Premium" -> Column(Modifier.padding(22.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){
          Icon(Icons.Default.WorkspacePremium,null,tint=Gold,modifier=Modifier.size(60.dp));Text("MANAS PREMIUM",fontSize=27.sp,fontWeight=FontWeight.Bold,color=Color.White)
          Tile {Text("₹199 / 12 months",fontSize=27.sp,color=Gold,fontWeight=FontWeight.Bold);Text("90-day guided Malayalam meditation curriculum",color=Color.White);Text("No automatic renewal — planned pricing",color=Muted);Spacer(Modifier.height(12.dp));Button(onClick={}) {Text("Payments coming later")}}
          Text("This build does not accept payments or unlock paid content. Billing must be implemented and verified before release.",color=Muted,fontSize=13.sp)
       }
     }
   }
   NavigationBar(containerColor=Panel){
      listOf(Triple("Home",Icons.Default.Home,"Home"),Triple("Journey",Icons.Default.CalendarMonth,"Journey"),Triple("Progress",Icons.Default.BarChart,"Progress"),Triple("Premium",Icons.Default.WorkspacePremium,"Premium")).forEach { (name,icon,label)->NavigationBarItem(selected=page==name,onClick={tts.stop();speaking=false;page=name},icon={Icon(icon,label)},label={Text(label)},colors=NavigationBarItemDefaults.colors(selectedIconColor=Lavender,selectedTextColor=Lavender,unselectedIconColor=Muted,unselectedTextColor=Muted)) }
   }
 }
 }
}
@Composable private fun Tile(content:@Composable ColumnScope.()->Unit){Column(Modifier.fillMaxWidth().background(Panel,RoundedCornerShape(20.dp)).padding(18.dp),content=content)}
