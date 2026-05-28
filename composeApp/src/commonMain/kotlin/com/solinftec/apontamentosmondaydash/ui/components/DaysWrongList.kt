package com.solinftec.apontamentosmondaydash.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solinftec.apontamentosmondaydash.extensions.formatterLocalized
import com.solinftec.apontamentosmondaydash.model.Apontamento
import com.solinftec.apontamentosmondaydash.model.ErrorType
import com.solinftec.apontamentosmondaydash.model.filterWrongNoteDays
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun DaysWrongList(
    apontamentos: List<Apontamento>,
    selectedName: String,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    val wrongNotes = remember(apontamentos, selectedName) {
        derivedStateOf {
            apontamentos.filterWrongNoteDays(selectedName)
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ) {
        item {
            Text("Dias com divergências", style = MaterialTheme.typography.titleLarge)
        }
        items(wrongNotes.value.toList()) { (date, totalDuration, errors) ->
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onDateSelected(date.toEpochMilliseconds()) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.CalendarToday, null)
                            Text(date.formatterLocalized())
                        }
                        Text("$totalDuration", style = MaterialTheme.typography.bodyLarge)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        errors.forEach { error ->
                            val (label, color) = when (error) {
                                ErrorType.OVERLAP -> "Sobreposição" to Color.Red
                                ErrorType.UNDER_HOURS -> "Falta de horas" to Color(0xFFFFA500) // Orange
                                ErrorType.OVER_HOURS -> "Horas a mais" to Color(0xFF9C27B0) // Purple
                            }
                            AssistChip(
                                onClick = { },
                                label = { Text(label, fontSize = 10.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    labelColor = color
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
