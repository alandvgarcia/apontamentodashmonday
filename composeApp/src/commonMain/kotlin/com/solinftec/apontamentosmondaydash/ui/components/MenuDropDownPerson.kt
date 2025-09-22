package com.solinftec.apontamentosmondaydash.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuDropDownPerson(
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
            Button(onClick = { expanded = !expanded }) {
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