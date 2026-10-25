package com.manas.meditation

import java.time.LocalDate
import java.time.temporal.ChronoUnit

internal fun streak(dates:Set<String>, today:LocalDate):Int {
 var day=if(today.toString() in dates) today else today.minusDays(1)
 var count=0
 while(day.toString() in dates) { count++; day=day.minusDays(1) }
 return count
}

internal fun longestStreak(dates:Set<String>):Int {
 val sorted=dates.mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.sorted()
 var best=0;var run=0;var previous:LocalDate?=null
 for(day in sorted){ run=if(previous!=null && ChronoUnit.DAYS.between(previous,day)==1L) run+1 else 1;best=maxOf(best,run);previous=day }
 return best
}
