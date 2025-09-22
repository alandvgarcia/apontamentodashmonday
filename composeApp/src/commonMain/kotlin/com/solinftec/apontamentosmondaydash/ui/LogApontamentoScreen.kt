package com.solinftec.apontamentosmondaydash.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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
