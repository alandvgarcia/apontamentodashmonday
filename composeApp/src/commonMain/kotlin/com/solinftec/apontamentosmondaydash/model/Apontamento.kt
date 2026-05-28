package com.solinftec.apontamentosmondaydash.model

import com.solinftec.apontamentosmondaydash.extensions.epochToLocalDate
import com.solinftec.apontamentosmondaydash.extensions.formatterLocalized
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Apontamento(
    val name: String,
    val nameTask: String,
    val startDate: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endDate: LocalDate? = null,
    val endTime: LocalTime? = null,
    val duration: Duration? = null,
) {
    companion object {
        const val MAX_HOUR_ALERT_LIMIT = 9.5
        const val MIN_HOUR_ALERT_LIMIT = 7
    }
}

fun Apontamento.startDateFormattedLocalized(): String? {
    return startDate?.formatterLocalized()
}

fun Apontamento.endDateFormattedLocalized(): String? {
    return endDate?.formatterLocalized()
}

fun List<Apontamento>.getTotalHours(): Duration {
    var total = Duration.ZERO
    mapNotNull { it.duration }.forEach { total += it }
    return total
}

fun List<Apontamento>.getIsAlertOn() {
    getTotalHours().alertTotalHours()
}

fun Duration.alertTotalHours() =
    inWholeHours > Apontamento.MAX_HOUR_ALERT_LIMIT || inWholeHours < Apontamento.MIN_HOUR_ALERT_LIMIT

@OptIn(ExperimentalTime::class)
fun List<Apontamento>.filterByDateSortByHour(selectedDateEpoch: Long, namePerson: String): List<Apontamento> {
    return filter {

        val selectedDate = selectedDateEpoch.epochToLocalDate(true)

        val startDate = it.startDate
        val endDate = it.endDate

        namePerson == it.name && (selectedDate == endDate || selectedDate == startDate)

    }.sortedBy { it.startTime?.hour }
}


enum class ErrorType {
    OVERLAP, UNDER_HOURS, OVER_HOURS
}

@OptIn(ExperimentalTime::class)
fun List<Apontamento>.detectErrors(): List<ErrorType> {
    val errors = mutableListOf<ErrorType>()
    val totalHours = getTotalHours().inWholeHours

    if (totalHours < Apontamento.MIN_HOUR_ALERT_LIMIT) errors.add(ErrorType.UNDER_HOURS)
    if (totalHours > Apontamento.MAX_HOUR_ALERT_LIMIT) errors.add(ErrorType.OVER_HOURS)

    val hasOverlap = anyIndexed { index, current ->
        val currentStart = current.startTime
        val currentEnd = current.endTime
        if (currentStart != null && currentEnd != null) {
            val cs = currentStart.hour * 60 + currentStart.minute
            val ce = if (currentEnd.hour == 0 && currentEnd.minute == 0) 24 * 60 else currentEnd.hour * 60 + currentEnd.minute

            filterIndexed { i, _ -> i != index }.any { other ->
                val otherStart = other.startTime
                val otherEnd = other.endTime
                if (otherStart != null && otherEnd != null) {
                    val os = otherStart.hour * 60 + otherStart.minute
                    val oe = if (otherEnd.hour == 0 && otherEnd.minute == 0) 24 * 60 else otherEnd.hour * 60 + otherEnd.minute
                    os < ce && oe > cs
                } else false
            }
        } else false
    }

    if (hasOverlap) errors.add(ErrorType.OVERLAP)

    return errors
}

private inline fun <T> List<T>.anyIndexed(predicate: (Int, T) -> Boolean): Boolean {
    forEachIndexed { index, t ->
        if (predicate(index, t)) return true
    }
    return false
}

@OptIn(ExperimentalTime::class)
fun List<Apontamento>.filterWrongNoteDays(namePerson: String): List<Triple<Instant, Duration, List<ErrorType>>> {
    return filter { it.name == namePerson }
        .groupBy { it.startDate?.atStartOfDayIn(TimeZone.currentSystemDefault()) }
        .filter { it.key != null }
        .map { (date, dailyApontamentos) ->
            Triple(date!!, dailyApontamentos.getTotalHours(), dailyApontamentos.detectErrors())
        }
        .filter { (_, total, errors) ->
            total.inWholeHours > Apontamento.MAX_HOUR_ALERT_LIMIT || 
            total.inWholeHours < Apontamento.MIN_HOUR_ALERT_LIMIT || 
            errors.contains(ErrorType.OVERLAP)
        }
}


