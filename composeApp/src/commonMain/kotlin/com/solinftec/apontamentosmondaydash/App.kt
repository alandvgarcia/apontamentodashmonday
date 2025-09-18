package com.solinftec.apontamentosmondaydash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import apontamentosmondaydash.composeapp.generated.resources.Res
import apontamentosmondaydash.composeapp.generated.resources.logo_solinftec
import com.solinftec.apontamentosmondaydash.viewmodel.MainViewModel
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.collections.filter
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {


    val viewModel = viewModel<MainViewModel>()

    val fileResult by viewModel.fileResult.collectAsState()
    val logString by viewModel.logString.collectAsState()

    var tabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Apontamentos", "Log Apontamentos")

    val singleLauncher = rememberFilePickerLauncher(
        mode = FileKitMode.Single,
        type = FileKitType.File(extensions = listOf("csv", "xlsx"))
    ) { file ->
        if (file != null) {
            viewModel.readFile(file)
        }
    }


    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar({
                    Image(
                        painterResource(Res.drawable.logo_solinftec),
                        null,
                        modifier = Modifier.size(112.dp))
                })
            },

            floatingActionButton = {
                ExtendedFloatingActionButton(onClick = {
                    singleLauncher.launch()
                }) {
                    Text("Carregar arquivo apontamento")
                }
            }) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .safeContentPadding()
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TabRow(selectedTabIndex = tabIndex) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                text = { Text(title) },
                                selected = tabIndex == index,
                                onClick = { tabIndex = index }
                            )
                        }
                    }
                    when (tabIndex) {
                        0 -> ApontamentoScreen(fileResult)
                        1 -> LogApontamentoScreen(logString)
                    }
                }


            }
        }
    }
}


@Composable
fun LogApontamentoScreen(logString: String, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(logString)
        }
    }
}


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
            val selectedDate =
                Instant.fromEpochMilliseconds(selectedDateEpoch).toLocalDateTime(TimeZone.currentSystemDefault()).date
            apontamentos.filter {
                val startDate = if (it.startDate != null) Instant.fromEpochMilliseconds(
                    it.startDate.atStartOfDayIn(TimeZone.currentSystemDefault())
                        .toEpochMilliseconds()
                ).toLocalDateTime(TimeZone.UTC).date else 0L
                val endDate = if (it.endDate != null) Instant.fromEpochMilliseconds(
                    it.endDate.atStartOfDayIn(TimeZone.currentSystemDefault())
                        .toEpochMilliseconds()
                ).toLocalDateTime(TimeZone.UTC).date else 0L

                selected == it.name && (selectedDate == endDate || selectedDate == startDate)

            }.sortedBy { it.startTime?.hour }
        }
    }


    val totalDurationDay: State<Duration?> = remember(filterApontamentos) {
        derivedStateOf {
            var total = Duration.ZERO
            filterApontamentos.value.mapNotNull { it.duration }.forEach { total += it }
            total
        }
    }

    val alertHours = remember(totalDurationDay) {
        derivedStateOf {
            ((totalDurationDay.value?.inWholeHours ?: 0) > 9.5 || (totalDurationDay.value?.inWholeHours
                ?: 0) < 7) && (totalDurationDay.value?.inWholeHours ?: 0) > 0
        }
    }


    LaunchedEffect(apontamentos) {
        if (selected.isEmpty() && apontamentos.isNotEmpty()) {
            selected = names.value.keys.first()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LongBasicDropdownMenu(
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
                                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
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
                                    Text("Inicio ${it.startDate.toString()} ${it.startTime}")
                                }

                                Row {
                                    Icon(Icons.Default.TimerOff, null)
                                    Text("Fim ${it.endDate.toString()} ${it.endTime}")
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
                DaysWrongScreen(apontamentos, selected)
            }


        }
    }
}


@Composable
fun DaysWrongScreen(apontamentos: List<Apontamento>, selectedName: String, modifier: Modifier = Modifier) {

    val wrongNotes = remember(apontamentos, selectedName) {
        derivedStateOf {
            apontamentos.filter { it.name == selectedName }.groupBy { it.startDate }.filter {
                var total = Duration.ZERO
                it.value.mapNotNull { it.duration }.forEach { total += it }
                (total.inWholeHours > 9.5 || total.inWholeHours < 7) && total.inWholeHours > 0
            }.map {
                var total = Duration.ZERO
                it.value.mapNotNull { it.duration }.forEach { total += it }
                it.key to total
            }
        }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(16.dp)) {
        item {
            Text("Dias errados", style = MaterialTheme.typography.titleLarge)
        }
        items(wrongNotes.value.toList()) { (date, totalDuration) ->
            OutlinedCard(modifier = Modifier.height(52.dp).fillMaxWidth()) {
                ListItem(leadingContent = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CalendarToday, null)
                        Text(date.toString())
                    }
                }, headlineContent = {
                    Text("$totalDuration")
                })
            }
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DatePickerDocked(initialDate: Long, modifier: Modifier = Modifier, onSelect: (Long) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val initialDate = datePickerState.selectedDateMillis ?: initialDate


    val selectedDate = initialDate.let {
        val date =
            kotlinx.datetime.Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
        onSelect(it)
        "${date.day}/${date.month.name}/${date.year}"
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = { },
            label = { Text("Selecione a data") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        if (showDatePicker) {
            Popup(
                onDismissRequest = { showDatePicker = false },
                alignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 64.dp)
                        .shadow(elevation = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )
                }
            }
        }
    }
}


@Composable
fun LongBasicDropdownMenu(
    itemSelected: String,
    items: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val menuItemData = items

    Box(
        modifier = modifier
            .padding(16.dp)
    ) {
        Row {
            TextButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.Person, contentDescription = "More options")
                Text(itemSelected)
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            menuItemData.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        expanded = false
                        onSelect(option)
                    }
                )
            }
        }
    }
}