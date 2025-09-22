package com.solinftec.apontamentosmondaydash.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.solinftec.apontamentosmondaydash.extensions.formatterLocalized
import com.solinftec.apontamentosmondaydash.model.Apontamento
import com.solinftec.apontamentosmondaydash.model.alertTotalHours
import com.solinftec.apontamentosmondaydash.model.filterByDateSortByHour
import com.solinftec.apontamentosmondaydash.model.getTotalHours
import com.solinftec.apontamentosmondaydash.ui.components.DatePickerDocked
import com.solinftec.apontamentosmondaydash.ui.components.DaysWrongList
import com.solinftec.apontamentosmondaydash.ui.components.MenuDropDownPerson
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
fun ApontamentoScreen(apontamentos: List<Apontamento>) {

    val names = remember(apontamentos) {
        derivedStateOf {
            apontamentos.groupBy { it.name }
        }
    }

    var selected by rememberSaveable { mutableStateOf("") }
    var selectedDateEpoch by rememberSaveable { mutableStateOf(Clock.System.now().toEpochMilliseconds()) }


    val filterApontamentos = rememberSaveable(selectedDateEpoch, apontamentos, selected) {
        derivedStateOf {
            apontamentos.filterByDateSortByHour(selectedDateEpoch, selected)
        }
    }

    val totalDurationDay: State<Duration?> = remember(filterApontamentos) {
        derivedStateOf {
            filterApontamentos.value.getTotalHours()
        }
    }

    val alertHours = remember(totalDurationDay) {
        derivedStateOf {
            totalDurationDay.value?.alertTotalHours() ?: false
        }
    }

    LaunchedEffect(apontamentos) {
        if (selected.isEmpty() && apontamentos.isNotEmpty()) {
            selected = names.value.keys.first()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MenuDropDownPerson(
                selected,
                names.value.keys.toList(),
                onSelect = { selected = it },
                modifier = Modifier.weight(1f)
            )

            Column(modifier = Modifier.weight(0.4f)) {
                Text("Total horas dia")
                SuggestionChip(
                    {},
                    {
                        Text("${totalDurationDay.value}")
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors()
                        .copy(containerColor = if (alertHours.value) Color.Red else Color.White)
                )
            }

            DatePickerDocked(selectedDateEpoch, modifier = Modifier.weight(1f)) {
                selectedDateEpoch = it
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ElevatedCard(modifier = Modifier.weight(0.6f).fillMaxSize()) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(16.dp)) {
                    item {
                        Text(
                            "Apontamentos do dia ${
                                Instant.fromEpochMilliseconds(selectedDateEpoch)
                                    .formatterLocalized()
                            }", style = MaterialTheme.typography.titleLarge
                        )
                    }
                    items(filterApontamentos.value) {
                        OutlinedCard(modifier = Modifier.height(182.dp).fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {

                                Text(it.nameTask)


                                Row {
                                    Icon(Icons.Default.Timer, null)
                                    Text("Inicio ${it.startDate?.formatterLocalized()} ${it.startTime}")
                                }

                                Row {
                                    Icon(Icons.Default.TimerOff, null)
                                    Text("Fim ${it.endDate?.formatterLocalized()} ${it.endTime}")
                                }


                                Row {
                                    Icon(Icons.Default.Timeline, null)
                                    Text("Duração total: ${it.duration}")
                                }

                            }
                        }
                    }
                }
            }

            ElevatedCard(modifier = Modifier.weight(1f).fillMaxSize()) {
                DaysWrongList(apontamentos, selected)
            }

            ElevatedCard(modifier = Modifier.weight(1f).fillMaxSize()) {
                DaysWrongList(apontamentos, selected)
            }


        }
    }
}
