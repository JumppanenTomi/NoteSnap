package fi.notesnap.notesnap.utilities

import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Formats a timestamp to represent the time difference from the current date in a user-friendly manner.
 *
 * @param updatedAt The timestamp to be formatted.
 * @return A formatted string indicating the time difference (e.g., "2d ago", "5m ago", "3y ago").
 */
fun formatTimeAgo(updatedAt: Long): String {
    val updatedAtInstant = Instant.ofEpochMilli(updatedAt)
    val updatedAtZonedDateTime = ZonedDateTime.ofInstant(updatedAtInstant, ZoneId.systemDefault())
    val today = LocalDate.now()
    val period = Period.between(updatedAtZonedDateTime.toLocalDate(), today)

    return when {
        period.days == 0 -> {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            updatedAtZonedDateTime.format(formatter)
        }

        period.months == 0 -> "${period.days}d ago"
        period.years == 0 -> "${period.months}m ago"
        else -> "${period.years}y ago"
    }
}