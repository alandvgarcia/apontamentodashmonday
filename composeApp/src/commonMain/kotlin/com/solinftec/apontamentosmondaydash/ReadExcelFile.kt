package com.solinftec.apontamentosmondaydash

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.char
import kotlin.time.ExperimentalTime

expect fun readExcelFile(byteArray: ByteArray): Pair<List<Apontamento>, String>

data class Apontamento(
    val name: String,
    val startDate: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endDate: LocalDate? = null,
    val endTime: LocalTime? = null,
    val duration: String? = null,
)

@OptIn(ExperimentalTime::class)
fun parseToApontamentoGroupedByPerson(groupedData: Map<String, Map<String, List<List<String>>>>): Pair<List<Apontamento>, String> {
    var stringLog = ""

    val listApontamento = groupedData.flatMap { (name, daysData) ->


        stringLog = stringLog.plus("----------------------------------------\n")
        stringLog = stringLog.plus("Nome: $name\n")
        stringLog = stringLog.plus("----------------------------------------\n")


        daysData.flatMap { (date, rows) ->

            stringLog.plus("  Data: $date\n")

            rows.map { row ->
                val startDate = row.getOrNull(2)
                val startTime = row.getOrNull(4)
                val endDate = row.getOrNull(3)
                val endTime = row.getOrNull(5)
                val duration = row.getOrNull(6)

                stringLog =
                    stringLog.plus("    - Início: ${startDate ?: "N/A"} ${startTime ?: "N/A"}, Fim: ${endDate ?: "N/A"} ${endTime ?: "N/A"}, Duração: ${duration ?: "N/A"}\n")

                val startDateLocal = if (startDate != null) LocalDate.parse(startDate, formatterDate) else null
                val startTimeLocal = if (startTime != null) LocalTime.parse(startTime, formatterTime) else null
                val endDateLocal = if (endDate != null && endDate.isNotEmpty()) LocalDate.parse(endDate, formatterDate) else null
                val endTimeLocal = if (endTime != null && endTime.isNotEmpty()) LocalTime.parse(endTime, formatterTime) else null


                val durationTime = duration

                Apontamento(
                    name,
                    startDateLocal,
                    startTimeLocal,
                    endDateLocal,
                    endTimeLocal,
                    duration = durationTime
                )
            }
        }
    }

    return listApontamento to stringLog
}


private val formatterDate = LocalDate.Format {
    year();char('-');monthNumber();char('-');day()
}

private val formatterTime = LocalTime.Format {
    amPmHour();char(':');minute();char(':');second();char(' ');amPmMarker("AM", "PM")
}