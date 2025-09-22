package com.solinftec.apontamentosmondaydash.extensions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun Instant.formatterLocalized(): String {
    return toLocalDateTime(TimeZone.currentSystemDefault()).format(
        LocalDateTime.Format {
            day();char('/');monthNumber();char('/');year()
        }
    )
}

fun LocalDate.formatterLocalized(): String {
    return format(LocalDate.Format {
        day();char('/');monthNumber();char('/');year()
    })
}


@OptIn(ExperimentalTime::class)
fun Long.epochToLocalDate(utcDate: Boolean = false): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(if(utcDate) TimeZone.UTC else TimeZone.currentSystemDefault()).date.atStartOfDayIn(
        TimeZone.currentSystemDefault()).toLocalDateTime(if(utcDate) TimeZone.UTC else TimeZone.currentSystemDefault())
}