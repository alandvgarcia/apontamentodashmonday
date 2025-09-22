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
import com.solinftec.apontamentosmondaydash.extensions.formatterLocalized
import com.solinftec.apontamentosmondaydash.model.Apontamento
import com.solinftec.apontamentosmondaydash.model.filterWrongNoteDays
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun DaysWrongList(apontamentos: List<Apontamento>, selectedName: String, modifier: Modifier = Modifier) {

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
            Text("Dias errados", style = MaterialTheme.typography.titleLarge)
        }
        items(wrongNotes.value.toList()) { (date, totalDuration) ->
            OutlinedCard(modifier = Modifier.height(52.dp).fillMaxWidth()) {
                ListItem(
                    leadingContent = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.CalendarToday, null)
                            Text(date?.formatterLocalized() ?: "")
                        }
                    }, headlineContent = {
                        Text("$totalDuration")
                    },
                    colors = ListItemDefaults.colors().copy(leadingIconColor = Color.White)
                )
            }
        }
    }


}