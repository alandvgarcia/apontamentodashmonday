package com.solinftec.apontamentosmondaydash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solinftec.apontamentosmondaydash.model.Apontamento
import com.solinftec.apontamentosmondaydash.readExcelFile
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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