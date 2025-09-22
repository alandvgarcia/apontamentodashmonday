package com.solinftec.apontamentosmondaydash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import apontamentosmondaydash.composeapp.generated.resources.Res
import apontamentosmondaydash.composeapp.generated.resources.logo_solinftec
import com.solinftec.apontamentosmondaydash.extensions.epochToLocalDate
import com.solinftec.apontamentosmondaydash.ui.ApontamentoScreen
import com.solinftec.apontamentosmondaydash.ui.theme.AppTheme
import com.solinftec.apontamentosmondaydash.viewmodel.MainViewModel
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {

    val viewModel = viewModel<MainViewModel>()

    val fileResult by viewModel.fileResult.collectAsState()

    val singleLauncher = rememberFilePickerLauncher(
        mode = FileKitMode.Single,
        type = FileKitType.File(extensions = listOf("csv", "xlsx"))
    ) { file ->
        if (file != null) {
            viewModel.readFile(file)
        }
    }


    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar({
                    Image(
                        painterResource(Res.drawable.logo_solinftec),
                        null,
                        modifier = Modifier.size(112.dp)
                    )
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
                ApontamentoScreen(fileResult)
            }
        }
    }
}









