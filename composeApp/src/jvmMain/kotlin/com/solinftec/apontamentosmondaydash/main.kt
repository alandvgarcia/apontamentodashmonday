package com.solinftec.apontamentosmondaydash

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import io.github.vinceglb.filekit.FileKit

fun main() = application {

    FileKit.init(appId = "com.solinftec.apontamentosmondaydash")


    Window(
        onCloseRequest = ::exitApplication,
        title = "apontamentosmondaydash",
        state = WindowState(WindowPlacement.Fullscreen)
    ) {
        App()
    }
}