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


@OptIn(ExperimentalTime::class)
fun List<Apontamento>.filterWrongNoteDays(namePerson: String): List<Pair<Instant, Duration>> {
    return filter { it.name == namePerson }.groupBy { it.startDate?.atStartOfDayIn(TimeZone.currentSystemDefault()) }
        .filter {
            var total = Duration.ZERO
            it.value.mapNotNull { it.duration }.forEach { total += it }
            (total.inWholeHours > 9.5 || total.inWholeHours < 7)
        }.filter { it.key != null }.map {
            var total = Duration.ZERO
            it.value.mapNotNull { it.duration }.forEach { total += it }
            it.key!! to total
        }
}


