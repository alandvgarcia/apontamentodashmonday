package com.solinftec.apontamentosmondaydash

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.vinceglb.filekit.FileKit

fun main() = application {

    FileKit.init(appId = "com.solinftec.apontamentosmondaydash")


    Window(
        onCloseRequest = ::exitApplication,
        title = "apontamentosmondaydash",
    ) {
        App()
    }
}