package com.solinftec.apontamentosmondaydash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
@Preview
fun App() {


    val viewModel = viewModel<MainViewModel>()

    val fileResult by viewModel.fileResult.collectAsState()
    val logString by viewModel.logString.collectAsState()

    var tabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Grafico", "Log Apontamentos")

    val singleLauncher = rememberFilePickerLauncher(
        mode = FileKitMode.Single,
        type = FileKitType.File(extensions = listOf("csv", "xlsx"))
    ) { file ->
        if (file != null) {
            viewModel.readFile(file)
        }
    }


    MaterialTheme {
        Scaffold(floatingActionButton = {
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
                    .fillMaxSize(),
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
                        0 -> GraficoApontamentoScreen(fileResult)
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
fun GraficoApontamentoScreen(apontamentos: List<Apontamento>) {

    val names = remember(apontamentos) {
        derivedStateOf {
            apontamentos.groupBy { it.name }
        }
    }

    var selected by remember { mutableStateOf("") }
    var selectedDateEpoch by remember { mutableStateOf(Clock.System.now().toEpochMilliseconds()) }
    val filterApontamentos = remember(selectedDateEpoch, apontamentos, selected) {
        derivedStateOf {
            val selectedDate = Instant.fromEpochMilliseconds(selectedDateEpoch).toLocalDateTime(TimeZone.UTC).date
            apontamentos.filter {
                val startDate = if (it.startDate != null) Instant.fromEpochMilliseconds(
                    it.startDate.atStartOfDayIn(TimeZone.UTC)
                        .toEpochMilliseconds()
                ).toLocalDateTime(TimeZone.UTC).date else 0L
                val endDate =  if (it.endDate != null) Instant.fromEpochMilliseconds(
                    it.endDate.atStartOfDayIn(TimeZone.UTC)
                        .toEpochMilliseconds()
                ).toLocalDateTime(TimeZone.UTC).date else 0L

                selected == it.name && (selectedDate == endDate || selectedDate == startDate)

            }.sortedBy { it.startTime?.hour }
        }
    }


    LaunchedEffect(apontamentos) {
        if (selected.isEmpty() && apontamentos.isNotEmpty()) {
            selected = names.value.keys.first()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            LongBasicDropdownMenu(
                selected,
                names.value.keys.toList(),
                onSelect = { selected = it },
                modifier = Modifier.weight(1f)
            )
            DatePickerDocked(selectedDateEpoch, modifier = Modifier.weight(1f)) {
                selectedDateEpoch = it
            }
        }


        Row {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterApontamentos.value) {
                    OutlinedCard(modifier = Modifier.height(52.dp).fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Inicio ${it.startDate.toString()}  - ${it.startTime} | Fim ${it.endDate.toString()}  - ${it.endTime} | ${it.duration}")
                        }
                    }
                }
            }

            Column {



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
        val date = kotlinx.datetime.Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
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


class MainViewModel() : ViewModel() {


    private val _fileResult = MutableStateFlow<List<Apontamento>>(listOf())
    val fileResult = _fileResult.asStateFlow()


    private val _logString = MutableStateFlow("")
    val logString = _logString.asStateFlow()

    fun readFile(file: PlatformFile) = viewModelScope.launch {
        val byteArray = file.readBytes()
        val (listStringContent, logString) = readExcelFile(byteArray)
        _fileResult.value = listStringContent
        _logString.value = logString
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