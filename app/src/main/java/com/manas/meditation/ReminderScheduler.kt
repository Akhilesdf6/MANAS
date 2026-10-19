package com.manas.meditation

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.Calendar

object ReminderScheduler {
 fun schedule(context:Context,morning:Boolean,evening:Boolean){
  val alarm=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
  listOf(Triple(101,7,morning),Triple(102,20,evening)).forEach { (id,hour,enabled) ->
   val intent=Intent(context,ReminderReceiver::class.java).putExtra("id",id)
   val pending=PendingIntent.getBroadcast(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
   alarm.cancel(pending)
   if(enabled){
    val time=Calendar.getInstance().apply {set(Calendar.HOUR_OF_DAY,hour);set(Calendar.MINUTE,0);set(Calendar.SECOND,0);set(Calendar.MILLISECOND,0);if(timeInMillis<=System.currentTimeMillis())add(Calendar.DAY_OF_YEAR,1)}
    alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,time.timeInMillis,AlarmManager.INTERVAL_DAY,pending)
   }
  }
 }
}
class ReminderReceiver:BroadcastReceiver(){
 override fun onReceive(context:Context,intent:Intent){
  val manager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  if(Build.VERSION.SDK_INT>=26) manager.createNotificationChannel(NotificationChannel("manas_daily","Meditation reminders",NotificationManager.IMPORTANCE_DEFAULT))
  val open=PendingIntent.getActivity(context,0,Intent(context,MainActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
  val notification=NotificationCompat.Builder(context,"manas_daily")
   .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
   .setContentTitle("MANAS · Meditation reminder")
   .setContentText("ഇന്നത്തെ ധ്യാനത്തിന് കുറച്ച് സമയം മാറ്റിവയ്ക്കാം 🌿")
   .setContentIntent(open).setAutoCancel(true).build()
  try { manager.notify(intent.getIntExtra("id",101),notification) } catch (_:SecurityException) { }
 }
}
class ReminderBootReceiver:BroadcastReceiver(){
 override fun onReceive(context:Context,intent:Intent){
  if(intent.action==Intent.ACTION_BOOT_COMPLETED){
   val prefs=context.getSharedPreferences("manas_progress",Context.MODE_PRIVATE)
   ReminderScheduler.schedule(context,prefs.getBoolean("morning",false),prefs.getBoolean("evening",false))
  }
 }
}
