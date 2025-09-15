package com.solinftec.apontamentosmondaydash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import apontamentosmondaydash.composeapp.generated.resources.Res
import apontamentosmondaydash.composeapp.generated.resources.compose_multiplatform
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
        if(file != null){
            viewModel.readFile(file)
        }
    }


    MaterialTheme {
        Scaffold(floatingActionButton = {
            ExtendedFloatingActionButton(onClick = {
                singleLauncher.launch()
            }){
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
                            Tab(text = { Text(title) },
                                selected = tabIndex == index,
                                onClick = { tabIndex = index }
                            )
                        }
                    }
                    when (tabIndex) {
                        0 -> GraficoApontamentoScreen()
                        1 -> LogApontamentoScreen(logString)
                    }
                }


            }
        }
    }
}


@Composable
fun LogApontamentoScreen(logString: String, modifier: Modifier = Modifier){
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item{
            Text(logString)
        }
    }
}


@Composable
fun GraficoApontamentoScreen(){


    Scaffold {

    }



}



class MainViewModel(): ViewModel() {


    private val _fileResult = MutableStateFlow<List<Apontamento>>(listOf())
    val fileResult = _fileResult.asStateFlow()


    private val _logString = MutableStateFlow("")
    val logString = _logString.asStateFlow()

    fun readFile(file: PlatformFile) = viewModelScope.launch{
        val byteArray = file.readBytes()
        val (listStringContent, logString) = readExcelFile(byteArray)
        _fileResult.value = listStringContent
        _logString.value = logString
    }


}