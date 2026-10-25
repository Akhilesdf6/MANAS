package com.manas.meditation

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class StreakLogicTest {

    private val today = LocalDate.of(2026, 10, 25)

    @Test
    fun emptyHistoryReturnsZero() {
        assertEquals(0, streak(emptySet(), today))
        assertEquals(0, longestStreak(emptySet()))
    }

    @Test
    fun todayCountsAsOneDay() {
        assertEquals(1, streak(setOf("2026-10-25"), today))
    }

    @Test
    fun yesterdayKeepsStreakActive() {
        assertEquals(2, streak(
            setOf("2026-10-23", "2026-10-24"),
            today
        ))
    }

    @Test
    fun missingDayBreaksStreak() {
        assertEquals(1, streak(
            setOf("2026-10-22", "2026-10-24"),
            today
        ))
    }

    @Test
    fun longestStreakFindsConsecutiveDays() {
        assertEquals(3, longestStreak(
            setOf(
                "2026-10-10",
                "2026-10-11",
                "2026-10-12",
                "2026-10-20"
            )
        ))
    }

    @Test
    fun invalidDatesAreIgnored() {
        assertEquals(2, longestStreak(
            setOf(
                "invalid",
                "2026-10-10",
                "2026-10-11"
            )
        ))
    }
}
